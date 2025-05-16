// FileViewer.java
package com.example.dingpointcontract;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import java.io.File;
import java.util.List;

public class FileViewer {
    private final Context context;

    public FileViewer(Context context) {
        this.context = context;
    }

    // 打开文件
    public void openFile(File file) {
        try {
            Log.d("FileViewer", "尝试打开文件: " + file.getAbsolutePath());
            Log.d("FileViewer", "文件存在: " + file.exists());
            Log.d("FileViewer", "文件大小: " + file.length() + " bytes");

            Uri uri = FileProvider.getUriForFile(
                    context,
                    "com.example.dingpointcontract.fileprovider",
                    file
            );
            Log.d("FileViewer", "生成的URI: " + uri);

            String mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setDataAndType(uri, mimeType)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
            Log.d("FileViewer", "找到 " + activities.size() + " 个可处理应用");
            for (ResolveInfo info : activities) {
                Log.d("FileViewer", "支持应用: " + info.activityInfo.packageName);
            }

            if (!activities.isEmpty()) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "请安装办公软件（如WPS Office）", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("FileViewer", "打开失败: " + e.getMessage(), e);
            Toast.makeText(context, "打开失败：" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }


    // 根据文件扩展名获取 MIME 类型
    private String getMimeType(File file) {
        String name = file.getName();
        if (name.endsWith(".pdf")) {
            return "application/pdf";
        } else if (name.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (name.endsWith(".doc")) {
            return "application/msword";
        }
        return "*/*";
    }
}
