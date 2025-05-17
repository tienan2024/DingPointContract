package com.example.dingpointcontract;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private final List<DatabaseHelper.SearchResult> results;
    private final OnItemClickListener listener;
    private String searchQuery = "";  // 搜索关键词，用于高亮显示

    public interface OnItemClickListener {
        void onItemClick(DatabaseHelper.SearchResult result);
    }

    public SearchResultAdapter(List<DatabaseHelper.SearchResult> results, OnItemClickListener listener) {
        this.results = results;
        this.listener = listener;
    }

    public SearchResultAdapter(List<DatabaseHelper.SearchResult> results, OnItemClickListener listener, String searchQuery) {
        this.results = results;
        this.listener = listener;
        this.searchQuery = searchQuery;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseHelper.SearchResult result = results.get(position);
        holder.bind(result, listener, searchQuery);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final TextView typeView;
        private final TextView contentView;
        private final TextView idInfoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.result_title);
            typeView = itemView.findViewById(R.id.result_type);
            contentView = itemView.findViewById(R.id.result_content);
            idInfoView = itemView.findViewById(R.id.result_id_info);
        }

        public void bind(DatabaseHelper.SearchResult result, OnItemClickListener listener, String searchQuery) {
            if (result == null) {
                return;
            }

            // 设置标题
            String title = result.getTitle();
            if (title != null) {
                setTextWithHighlight(titleView, title, searchQuery);
            } else {
                titleView.setText("无标题");
            }

            // 设置类型和图标
            String typeText = "未知类型";
            int typeIconId = 0;
            int typeColorId = 0;

            String type = result.getType();
            if (type != null) {
                switch (type) {
                    case "template":
                        typeText = "合同模板";
                        typeIconId = R.drawable.ic_menu; // 替换为合适的图标
                        typeColorId = R.color.primary;
                        break;
                    case "contract":
                        typeText = "已创建合同";
                        typeIconId = R.drawable.ic_menu; // 替换为合适的图标
                        typeColorId = R.color.primary;
                        break;
                    case "article":
                        typeText = "法条";
                        typeIconId = R.drawable.ic_menu; // 替换为合适的图标
                        typeColorId = R.color.primary;
                        break;
                }
            }

            typeView.setText(typeText);
            if (typeColorId != 0) {
                try {
                    typeView.setTextColor(ContextCompat.getColor(itemView.getContext(), typeColorId));
                } catch (Exception e) {
                    // 如果资源ID无效，使用默认颜色
                    Log.e("SearchResultAdapter", "颜色资源无效: " + e.getMessage());
                }
            }

            // 设置内容预览，添加高亮显示搜索关键词
            String content = result.getContent();
            if (content != null) {
                // 如果内容太长，截取一部分显示
                if (content.length() > 100) {
                    // 查找搜索词位置
                    int searchPos = -1;
                    if (searchQuery != null && !searchQuery.isEmpty()) {
                        searchPos = content.toLowerCase().indexOf(searchQuery.toLowerCase());
                    }

                    // 如果找到搜索词，截取其周围文本
                    if (searchPos >= 0) {
                        int start = Math.max(0, searchPos - 40);
                        int end = Math.min(content.length(), searchPos + searchQuery.length() + 40);
                        content = (start > 0 ? "..." : "") + content.substring(start, end) + (end < content.length() ? "..." : "");
                    } else {
                        // 否则截取前100个字符
                        content = content.substring(0, 100) + "...";
                    }
                }

                setTextWithHighlight(contentView, content, searchQuery);
            } else {
                contentView.setText("无内容");
            }

            // 设置文件名或法条号
            String idInfo = "";
            type = result.getType();
            if (type != null) {
                if (type.equals("template") || type.equals("contract")) {
                    String fileName = result.getFileName();
                    idInfo = "文件名: " + (fileName != null ? fileName : "未知");
                } else if (type.equals("article")) {
                    String articleNumber = result.getArticleNumber();
                    idInfo = "法条: " + (articleNumber != null ? articleNumber : "未知");
                }
            }
            idInfoView.setText(idInfo);

            // 设置点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(result);
                }
            });
        }

        // 设置文本并高亮显示搜索关键词
        private void setTextWithHighlight(TextView textView, String text, String searchQuery) {
            if (textView == null || text == null) {
                return;
            }

            if (searchQuery == null || searchQuery.isEmpty()) {
                textView.setText(text);
                return;
            }

            try {
                SpannableString spannableString = new SpannableString(text);
                String textLower = text.toLowerCase();
                String searchLower = searchQuery.toLowerCase();

                int startPos = 0;
                while (true) {
                    int index = textLower.indexOf(searchLower, startPos);
                    if (index == -1) break;

                    // 设置文字高亮样式
                    try {
                        spannableString.setSpan(
                                new ForegroundColorSpan(ContextCompat.getColor(textView.getContext(), R.color.colorAccent)),
                                index,
                                index + searchQuery.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        // 设置粗体
                        spannableString.setSpan(
                                new StyleSpan(Typeface.BOLD),
                                index,
                                index + searchQuery.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } catch (Exception e) {
                        Log.e("SearchResultAdapter", "设置高亮时出错: " + e.getMessage());
                    }

                    startPos = index + searchQuery.length();
                }

                textView.setText(spannableString);
            } catch (Exception e) {
                // 如果高亮处理失败，至少显示原始文本
                textView.setText(text);
                Log.e("SearchResultAdapter", "文本高亮处理失败: " + e.getMessage());
            }
        }
    }
}