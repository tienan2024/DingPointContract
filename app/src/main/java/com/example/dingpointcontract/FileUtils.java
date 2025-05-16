package com.example.dingpointcontract;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    // 从 assets 目录复制文件到 Downloads 目录
    public static File copyAssetToDownloads(Context context, String fileName) throws IOException {
        // 保存到应用私有目录的 files 文件夹
        File outputFile = new File(context.getFilesDir(), fileName);

        try (InputStream in = context.getAssets().open(fileName);
             FileOutputStream out = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
        return outputFile;
    }


    // 修改 getFileFromDownloads 方法
    public static File getFileFromDownloads(Context context, String fileName) {
        // 检查应用私有目录
        File internalFile = new File(context.getFilesDir(), fileName);
        if (internalFile.exists()) {
            return internalFile;
        }

        // 检查公共下载目录（可选）
        File publicFile = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
        );
        return publicFile.exists() ? publicFile : null;
    }


    public static File getContractLibraryDir(Context context) {
        File libraryDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "已上传合同库");
        if (!libraryDir.exists()) {
            libraryDir.mkdirs();
        }
        return libraryDir;
    }

}
