package com.example.dingpointcontract;

public class LegalArticle {
    private String articleNumber;  // 法条号码
    private String title;          // 法条标题
    private String content;        // 法条内容
    private String type;           // 法律类型

    public LegalArticle(String articleNumber, String title, String content, String type) {
        this.articleNumber = articleNumber;
        this.title = title;
        this.content = content;
        this.type = type;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }
}