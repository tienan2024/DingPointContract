package com.example.dingpointcontract;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RiskEvaluateFragment extends Fragment {

    private static final int FILE_PICK_REQUEST = 1001;
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String API_KEY = "你自己的key";
    private static final String TAG = "RiskEvaluate";

    // 模型常量
    private static final String MODEL_V3 = "deepseek-chat";
    private static final String MODEL_R1 = "deepseek-reasoner";

    // UI组件
    private EditText inputField;
    private TextView aiResponseText;
    private ProgressBar progressBar;
    private TextView progressInfoText;
    private Button analyzeButton;
    private RecyclerView chatHistoryRecyclerView;
    private ChatHistoryAdapter chatHistoryAdapter;

    // 状态管理
    private int clickCount = 0;
    private String currentModel = MODEL_V3;
    private boolean isRequestInProgress = false;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();
    private List<ChatMessage> chatHistory = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_risk_evaluate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    private void setupViews(View view) {
        inputField = view.findViewById(R.id.input_field);
        aiResponseText = view.findViewById(R.id.ai_response_text);
        progressBar = view.findViewById(R.id.progress_bar);
        progressInfoText = view.findViewById(R.id.progress_info_text);
        analyzeButton = view.findViewById(R.id.ask_button);
        Button uploadButton = view.findViewById(R.id.upload_file_button);
        chatHistoryRecyclerView = view.findViewById(R.id.chat_history_recyclerview);

        // 初始化组件
        setupChatHistoryRecyclerView();
        setupButtonListeners(uploadButton);
        setupTitleClickListener(view);
    }

    private void setupChatHistoryRecyclerView() {
        chatHistoryAdapter = new ChatHistoryAdapter(chatHistory, message -> {
            inputField.setText(message.getUserMessage());
            aiResponseText.setText(message.getAiResponse());
        });
        chatHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatHistoryRecyclerView.setAdapter(chatHistoryAdapter);
    }

    private void setupButtonListeners(Button uploadButton) {
        uploadButton.setOnClickListener(v -> openFilePicker());
        analyzeButton.setOnClickListener(v -> startAnalysis());
    }

    private void setupTitleClickListener(View view) {
        view.findViewById(R.id.titleText).setOnClickListener(v -> {
            if (++clickCount >= 5) {
                toggleModel();
                clickCount = 0;
            }
        });
    }

    private void toggleModel() {
        currentModel = currentModel.equals(MODEL_V3) ? MODEL_R1 : MODEL_V3;
        updateModelIndicator();
    }

    private void updateModelIndicator() {
        String modelName = currentModel.equals(MODEL_V3) ?
                "🔵 v3 标准模式" : "🔴 r1 深度分析";
        TextView title = getView().findViewById(R.id.titleText);
        title.setText(modelName);
        Toast.makeText(getContext(), "当前模式: " + modelName, Toast.LENGTH_SHORT).show();
    }

    private void startAnalysis() {
        if (isRequestInProgress) {
            showToast("请等待上一个请求完成");
            return;
        }

        String input = inputField.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            showToast("请输入合同内容或上传文件");
            return;
        }

        isRequestInProgress = true;
        new AnalysisTask().execute(input);
    }

    private class AnalysisTask extends AsyncTask<String, Integer, AnalysisResult> {
        private long startTime;

        @Override
        protected void onPreExecute() {
            startTime = System.currentTimeMillis();
            updateUIState(false);
            progressBar.setVisibility(View.VISIBLE);
            progressInfoText.setVisibility(View.VISIBLE);
            aiResponseText.setText("");
        }

        @Override
        protected AnalysisResult doInBackground(String... inputs) {
            try {
                // 阶段1：准备请求（10%）
                publishProgress(10, 0);

                // 阶段2：构建请求体（20%）
                JSONObject requestBody = buildRequestBody(inputs[0]);
                publishProgress(30, (int) ((System.currentTimeMillis() - startTime)/1000));

                // 阶段3：发送请求（20%）
                Request request = buildRequest(requestBody);
                publishProgress(50, (int) ((System.currentTimeMillis() - startTime)/1000));

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        return new AnalysisResult( // 返回AnalysisResult对象
                                null,
                                "请求失败: " + response.code() + " " + response.message()
                        );
                    }

                    String responseBody = response.body().string();
                    return new AnalysisResult( // 正确包装结果
                            parseResponse(responseBody), // 这里返回的String作为response参数
                            null // 错误信息为null
                    );
                }
            } catch (Exception e) {
                Log.e(TAG, "分析失败", e);
                return new AnalysisResult(
                        null,
                        "分析失败: " + e.getMessage() // 错误信息放在第二个参数
                );
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            progressInfoText.setText(String.format(Locale.CHINA,
                    "分析进度: %d%% (已用 %ds)",
                    values[0],
                    values[1]
            ));
        }

        @Override
        protected void onPostExecute(AnalysisResult result) {
            isRequestInProgress = false;
            updateUIState(true);
            progressBar.setVisibility(View.GONE);
            progressInfoText.setVisibility(View.GONE);

            if (result.error != null) {
                showErrorResult(result.error);
            } else {
                showSuccessResult(result.response);
            }
        }

        private void updateUIState(boolean enabled) {
            analyzeButton.setEnabled(enabled);
            analyzeButton.setAlpha(enabled ? 1.0f : 0.7f);
            inputField.setEnabled(enabled);
        }

        private void showErrorResult(String error) {
            aiResponseText.setTextColor(Color.RED);
            aiResponseText.setText(error);
        }

        private void showSuccessResult(String response) {
            aiResponseText.setTextColor(Color.BLACK);
            aiResponseText.setText(response);
            saveToHistory(response);
        }

        private JSONObject buildRequestBody(String input) throws JSONException {
            return new JSONObject()
                    .put("model", currentModel)
                    .put("messages", new JSONArray()
                            .put(new JSONObject()
                                    .put("role", "system")
                                    .put("content", "你是一个专业合同审查助手，请用中文分析合同中的潜在风险，并根据合同内容给出一个诈骗风险系数（0到100）。\n" +
                                            "格式如下：\n" +
                                            "🔥【诈骗风险系数】XX/100\n" +
                                            "⚠️【风险概要】检测到多处异常条款，主要涉及单方权利过大、违约金比例过高及责任界定模糊\n" +
                                            "🔍【详细分析】\n" +
                                            "🔴 风险类型：单方解释权条款\n" +
                                            "🔵 条款位置：第12条\n" +
                                            "🟡 风险说明：赋予甲方单方面解释合同的权利\n" +
                                            "🟢 修改建议：改为\\\"双方协商一致解释合同条款\\\"\\n"))
                            .put(new JSONObject()
                                    .put("role", "user")
                                    .put("content", "请分析以下合同内容：\n\n" + input)))
                    .put("temperature", 0.2)
                    .put("max_tokens", 1000);
        }
        private Request buildRequest(JSONObject requestBody) {
            return new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .post(RequestBody.create(
                            MediaType.get("application/json"),
                            requestBody.toString()))
                    .build();
        }

        // 修改parseResponse方法
        private String parseResponse(String responseBody) throws JSONException {
            JSONObject json = new JSONObject(responseBody);
            if (currentModel.equals(MODEL_V3)) {
                return json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                return json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getString("text");
            }
        }

        private void saveToHistory(String response) {
            chatHistory.add(0, new ChatMessage(
                    inputField.getText().toString(),
                    response,
                    currentModel
            ));
            chatHistoryAdapter.notifyItemInserted(0);
        }
    }

    // 其他辅助方法
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("*/*")
                .putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                        "application/pdf",
                        "text/plain",
                        "application/msword"
                });
        startActivityForResult(intent, FILE_PICK_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            processFileContent(data.getData());
        }
    }

    private void processFileContent(Uri uri) {
        try (InputStream is = requireContext().getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            inputField.setText(content.toString());
            showToast("文件内容已加载");
        } catch (Exception e) {
            Log.e(TAG, "文件读取失败", e);
            showToast("文件读取失败: " + e.getMessage());
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // 数据类
    private static class AnalysisResult {
        final String response;
        final String error;

        AnalysisResult(String response, String error) {
            this.response = response;
            this.error = error;
        }
    }

    public static class ChatMessage {
        private final String userMessage;
        private final String aiResponse;
        private final String model;
        private final long timestamp;

        public ChatMessage(String userMessage, String aiResponse, String model) {
            this.userMessage = userMessage;
            this.aiResponse = aiResponse;
            this.model = model;
            this.timestamp = System.currentTimeMillis();
        }

        public String getFormattedTime() {
            return new SimpleDateFormat("MM/dd HH:mm", Locale.CHINA).format(timestamp);
        }

        public String getModelBadge() {
            return model.equals(MODEL_V3) ? "[v3]" : "[r1]";
        }

        public String getUserMessage() { return userMessage; }
        public String getAiResponse() { return aiResponse; }
    }

    // 适配器类
    private static class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {
        private final List<ChatMessage> history;
        private final OnItemClickListener listener;

        interface OnItemClickListener { void onItemClick(ChatMessage message); }

        ChatHistoryAdapter(List<ChatMessage> history, OnItemClickListener listener) {
            this.history = history;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChatMessage message = history.get(position);
            holder.timeView.setText(message.getFormattedTime());
            holder.modelView.setText(message.getModelBadge());
            holder.itemView.setOnClickListener(v -> listener.onItemClick(message));
        }

        @Override
        public int getItemCount() { return history.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView timeView, modelView;

            ViewHolder(View itemView) {
                super(itemView);
                timeView = itemView.findViewById(R.id.history_time);
                modelView = itemView.findViewById(R.id.history_model);
            }
        }
    }
}
