package com.example.dingpointcontract;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LegalArticleAdapter extends RecyclerView.Adapter<LegalArticleAdapter.GroupViewHolder> {

    private Context context;
    private List<String> legalTypes;  // 法律类型列表
    private Map<String, List<LegalArticle>> groupedArticles;  // 按法律类型分组的法条
    private Map<String, Boolean> expandStates;  // 每个组的展开状态
    private OnArticleClickListener listener;

    public interface OnArticleClickListener {
        void onArticleClick(LegalArticle article);
    }

    public LegalArticleAdapter(Context context, List<LegalArticle> articles, OnArticleClickListener listener) {
        this.context = context;
        this.listener = listener;

        // 初始化数据结构
        this.groupedArticles = new HashMap<>();
        this.expandStates = new HashMap<>();
        this.legalTypes = new ArrayList<>();

        // 对法条按类型分组
        groupArticles(articles);
    }

    private void groupArticles(List<LegalArticle> articles) {
        // 按法律类型分组
        for (LegalArticle article : articles) {
            String type = article.getType();
            if (!groupedArticles.containsKey(type)) {
                groupedArticles.put(type, new ArrayList<>());
                legalTypes.add(type);
                expandStates.put(type, false);  // 初始折叠状态
            }
            groupedArticles.get(type).add(article);
        }

        // 对每个分组内的法条按照法条号排序
        for (String type : legalTypes) {
            Collections.sort(groupedArticles.get(type), (a1, a2) -> {
                // 提取法条号中的数字进行比较
                int num1 = parseNumberSafely(extractNumber(a1.getArticleNumber()));
                int num2 = parseNumberSafely(extractNumber(a2.getArticleNumber()));
                return Integer.compare(num1, num2);
            });
        }
    }

    private String extractNumber(String articleNumber) {
        if (articleNumber == null) {
            return "0"; // 默认返回"0"而不是空字符串
        }
        // 提取"第X条"中的数字X
        String numberStr = articleNumber.replaceAll("[^0-9]", "");
        return numberStr.isEmpty() ? "0" : numberStr; // 如果没有数字，也返回"0"
    }

    // 安全解析整数，出错时返回默认值0
    private int parseNumberSafely(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0; // 解析失败时返回0
        }
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_legal_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        String legalType = legalTypes.get(position);
        List<LegalArticle> articlesInGroup = groupedArticles.get(legalType);
        boolean isExpanded = expandStates.get(legalType);

        holder.groupTitle.setText(legalType);
        holder.expandIcon.setImageResource(isExpanded ?
                android.R.drawable.arrow_up_float :
                android.R.drawable.arrow_down_float);

        // 设置组项的点击事件（展开/折叠）
        holder.groupHeader.setOnClickListener(v -> {
            expandStates.put(legalType, !isExpanded);
            notifyItemChanged(position);
        });

        // 设置子项列表
        if (isExpanded) {
            holder.articleList.setVisibility(View.VISIBLE);
            LegalArticleItemAdapter itemAdapter = new LegalArticleItemAdapter(articlesInGroup, article -> {
                if (listener != null) {
                    listener.onArticleClick(article);
                }
            });
            holder.articleList.setAdapter(itemAdapter);
        } else {
            holder.articleList.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return legalTypes.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        View groupHeader;
        TextView groupTitle;
        ImageView expandIcon;
        RecyclerView articleList;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupHeader = itemView.findViewById(R.id.group_header);
            groupTitle = itemView.findViewById(R.id.group_title);
            expandIcon = itemView.findViewById(R.id.expand_icon);
            articleList = itemView.findViewById(R.id.article_list);
            articleList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }

    // 法条项适配器（嵌套适配器）
    private static class LegalArticleItemAdapter extends RecyclerView.Adapter<LegalArticleItemAdapter.ArticleViewHolder> {

        private List<LegalArticle> articles;
        private OnArticleClickListener listener;

        public LegalArticleItemAdapter(List<LegalArticle> articles, OnArticleClickListener listener) {
            this.articles = articles;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_legal_article, parent, false);
            return new ArticleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
            LegalArticle article = articles.get(position);
            holder.articleTitle.setText(article.getTitle());

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArticleClick(article);
                }
            });
        }

        @Override
        public int getItemCount() {
            return articles.size();
        }

        static class ArticleViewHolder extends RecyclerView.ViewHolder {
            TextView articleTitle;

            public ArticleViewHolder(@NonNull View itemView) {
                super(itemView);
                articleTitle = itemView.findViewById(R.id.article_title);
            }
        }
    }
}