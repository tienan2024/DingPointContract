package com.example.dingpointcontract;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LegalArticleDetailFragment extends Fragment {

    private static final String ARG_ARTICLE_NUMBER = "article_number";
    private static final String ARG_ARTICLE_TITLE = "article_title";
    private static final String ARG_ARTICLE_CONTENT = "article_content";
    private static final String ARG_ARTICLE_TYPE = "article_type";

    private String articleNumber;
    private String articleTitle;
    private String articleContent;
    private String articleType;

    public static LegalArticleDetailFragment newInstance(LegalArticle article) {
        LegalArticleDetailFragment fragment = new LegalArticleDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_NUMBER, article.getArticleNumber());
        args.putString(ARG_ARTICLE_TITLE, article.getTitle());
        args.putString(ARG_ARTICLE_CONTENT, article.getContent());
        args.putString(ARG_ARTICLE_TYPE, article.getType());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articleNumber = getArguments().getString(ARG_ARTICLE_NUMBER);
            articleTitle = getArguments().getString(ARG_ARTICLE_TITLE);
            articleContent = getArguments().getString(ARG_ARTICLE_CONTENT);
            articleType = getArguments().getString(ARG_ARTICLE_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_legal_article_detail, container, false);

        // 为标题栏添加返回箭头
        TextView titleText = view.findViewById(R.id.title_text);
        titleText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_chevron_right_24, 0, 0, 0);
        titleText.setCompoundDrawablePadding(8);
        titleText.setOnClickListener(v -> {
            // 返回上一页
            getParentFragmentManager().popBackStack();
        });

        // 设置法条详情
        TextView articleTitleText = view.findViewById(R.id.article_title);
        TextView articleContentText = view.findViewById(R.id.article_content);
        TextView articleTypeText = view.findViewById(R.id.article_type);

        articleTitleText.setText(articleTitle);
        articleContentText.setText(articleContent);
        articleTypeText.setText("类别：" + articleType);

        return view;
    }
}