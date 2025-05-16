package com.example.dingpointcontract;

public class Template {
    private final String displayName; // 显示名称
    private final String fileName;    // 实际文件名

    public Template(String displayName, String fileName) {
        this.displayName = displayName;
        this.fileName = fileName;
    }

    public String getDisplayName() { return displayName; }
    public String getFileName() { return fileName; }
}

