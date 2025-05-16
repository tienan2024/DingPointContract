package com.example.dingpointcontract;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContractCreateFragment extends Fragment {

    private RecyclerView templateRecyclerView;
    private TemplateAdapter templateAdapter;
    private Button downloadButton;
    private Button openButton;
    private Template selectedTemplate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contract_create, container, false);

        // 设置标题
        TextView titleText = view.findViewById(R.id.titleText);
        titleText.setText(getString(R.string.title_select_template));

        templateRecyclerView = view.findViewById(R.id.templateRecyclerView);
        templateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 动态获取 assets 中的模板文件
        List<Template> templates = loadTemplatesFromAssets();

        templateAdapter = new TemplateAdapter(templates, template -> {
            selectedTemplate = template;
        });
        templateRecyclerView.setAdapter(templateAdapter);

        downloadButton = view.findViewById(R.id.downloadButton);
        downloadButton.setText(getString(R.string.button_download_template));
        downloadButton.setOnClickListener(v -> {
            if (selectedTemplate != null) {
                downloadTemplate(selectedTemplate);
            } else {
                showToast(getString(R.string.toast_please_select_template));
            }
        });

        openButton = view.findViewById(R.id.openButton);
        openButton.setText(getString(R.string.button_open_template));
        openButton.setOnClickListener(v -> {
            if (selectedTemplate != null) {
                openTemplate(selectedTemplate);
            } else {
                showToast(getString(R.string.toast_please_select_template));
            }
        });

        return view;
    }

    // 从 assets 加载模板文件
    private List<Template> loadTemplatesFromAssets() {
        List<Template> templates = new ArrayList<>();
        AssetManager assetManager = requireContext().getAssets();

        try {
            // 获取 assets 根目录下的所有文件
            String[] files = assetManager.list("");

            if (files != null) {
                for (String fileName : files) {
                    // 可以根据需要添加文件过滤，例如只显示.docx文件
                    if (fileName.endsWith(".docx")) {
                        // 使用文件名作为模板显示名称（可以进一步处理）
                        String displayName = formatFileName(fileName);
                        templates.add(new Template(displayName, fileName));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showToast(getString(R.string.toast_load_template_failed));
        }
        return templates;
    }

    // 格式化文件名（示例：将"template_v1.docx"转为"Template V1"）
    private String formatFileName(String fileName) {
        // 移除文件扩展名
        String name = fileName.replaceFirst("[.][^.]+$", "");
        // 替换下划线和连字符为空格
        name = name.replaceAll("[_-]", " ");
        // 首字母大写
        return capitalizeWords(name);
    }

    private String capitalizeWords(String input) {
        StringBuilder result = new StringBuilder();
        String[] words = input.split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    private void downloadTemplate(Template template) {
        try {
            File downloadedFile = FileUtils.copyAssetToDownloads(requireContext(), template.getFileName());
            Log.d("DownloadPath", "File saved path: " + downloadedFile.getAbsolutePath());
            showToast(String.format(getString(R.string.toast_download_successful), template.getDisplayName()));
        } catch (IOException e) {
            e.printStackTrace();
            showToast(getString(R.string.toast_download_failed));
        }
    }

    private void openTemplate(Template template) {
        try {
            File file = FileUtils.getFileFromDownloads(requireContext(), template.getFileName());
            if (file != null && file.exists()) {
                new FileViewer(requireContext()).openFile(file);
            } else {
                showToast(getString(R.string.toast_file_not_exist));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getString(R.string.toast_open_file_failed));
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}