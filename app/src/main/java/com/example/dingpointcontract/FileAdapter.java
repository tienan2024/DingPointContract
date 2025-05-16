package com.example.dingpointcontract;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private final List<File> fileList;

    public FileAdapter(List<File> fileList) {
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_item, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        File file = fileList.get(position);
        holder.fileName.setText(file.getName());



        // 打开文件
        holder.openButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            try {
                Uri uri = FileProvider.getUriForFile(context,
                        "com.example.dingpointcontract.fileprovider", file);

                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(file.getName());
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                if (mimeType == null) mimeType = "*/*"; // 如果无法获取MIME类型，则使用通配符

                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(uri, mimeType)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(holder.itemView.getContext(), "没有找到可以打开此文件的应用", Toast.LENGTH_SHORT).show();
            }
        });

        // 删除文件
        holder.deleteButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                File fileToDelete = fileList.get(pos);
                if (fileToDelete.delete()) {
                    fileList.remove(pos);
                    notifyItemRemoved(pos);
                    Toast.makeText(holder.itemView.getContext(), "文件已删除", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    // 更新文件列表
    public void updateFiles(List<File> newFiles) {
        // 创建新列表避免数据引用问题
        List<File> updatedList = new ArrayList<>(newFiles);

        // 计算差异更新
        int oldSize = fileList.size();
        fileList.clear();
        notifyItemRangeRemoved(0, oldSize);

        fileList.addAll(updatedList);
        notifyItemRangeInserted(0, updatedList.size());

        // 调试日志
        Log.d("FileAdapter", "列表已更新，新项目数: " + updatedList.size());
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        Button openButton;
        Button deleteButton;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            openButton = itemView.findViewById(R.id.open_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
