package com.example.dingpointcontract;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContractManageFragment extends Fragment {

    private static final String TAG = "ContractManageFragment";
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private final List<File> allFiles = new ArrayList(); // 完整数据源
    private final List<File> displayedFiles = new ArrayList(); // 当前显示数据

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable filterRunnable;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contract_manage2, container, false);

        // 初始化RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true); // 只有当所有列表项高度固定时才启用
        recyclerView.setItemViewCacheSize(20); // 可选优化
        fileAdapter = new FileAdapter(displayedFiles);
        recyclerView.setAdapter(fileAdapter);

        // 初始化上传按钮
        Button uploadButton = view.findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(v -> {
            if (checkPermissions()) showFileChooser();
        });

        // 初始化搜索框

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false); // 确保搜索框是展开的
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE); // 改变IME选项
        searchView.setQueryHint(getString(R.string.selecthint));  // 设置提示语
        searchView.setFocusable(true);  // 确保可以获得焦点
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // 如果你不需要处理提交操作
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 防抖处理
                if (filterRunnable != null) {
                    handler.removeCallbacks(filterRunnable);
                }
                filterRunnable = () -> filterFiles(newText);
                handler.postDelayed(filterRunnable, 300); // 延迟300毫秒后执行过滤操作
                return true;
            }
        });
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        loadLocalFiles();
        return view;
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        String[] mimeTypes = {
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        try {
            fileChooserLauncher.launch(Intent.createChooser(intent, "选择合同文件"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "未找到文件选择器", Toast.LENGTH_SHORT).show();
        }
    }

    // 权限检查方法
    private boolean checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(requireContext(), "需要存储管理权限", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
                return false;
            }
            return true;
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
            return true;
        }
    }

    // 文件选择逻辑
    private final ActivityResultLauncher<Intent> fileChooserLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        saveFileToInternalStorage(uri);
                        loadLocalFiles();

                    }
                }
            }

    );

    // 保存文件方法
    private void saveFileToInternalStorage(Uri uri) {
        File outputFile = null;
        try {
            ContentResolver resolver = requireContext().getContentResolver();
            InputStream input = resolver.openInputStream(uri);

            // 修改保存路径到合同库目录
            File outputDir = FileUtils.getContractLibraryDir(requireContext());
            String fileName = getFileNameFromUri(uri);
            outputFile = new File(outputDir, fileName);


            FileOutputStream output = new FileOutputStream(outputFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            input.close();
            output.close();
            Toast.makeText(requireContext(), "文件保存成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // 显示详细错误信息
            String errorMsg = "保存失败: " + e.getClass().getSimpleName();
            if (e instanceof FileNotFoundException) {
                errorMsg += " - 文件不存在";
            } else if (e instanceof SecurityException) {
                errorMsg += " - 无权限访问";
            }
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();

            // 删除无效文件
            if (outputFile != null && outputFile.exists()) {
                outputFile.delete();
            }
        }

    }

    // 文件名获取方法
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    // 安全获取列索引
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 备用方案：从URI路径解析
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf(File.separator);
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }

        // 最终备用方案
        if (result == null) {
            result = "unknown_file_" + System.currentTimeMillis();
        }

        return result;
    }

    // 刷新列表
    private void loadLocalFiles() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            File libraryDir = FileUtils.getContractLibraryDir(requireContext());

            // 添加目录检查
            if (!libraryDir.exists()) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "合同库目录不存在", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            File[] files = libraryDir.listFiles();

            requireActivity().runOnUiThread(() -> {
                allFiles.clear();
                if (files != null) {
                    // 过滤非文件项
                    for (File file : files) {
                        if (file.isFile()) {
                            allFiles.add(file);
                        }
                    }
                }
                filterFiles("");  // 初始时显示所有文件
                fileAdapter.updateFiles(new ArrayList<>(displayedFiles));
            });
        });
    }



    // 文件过滤方法
    private void filterFiles(String query) {
        try {
            displayedFiles.clear();

            if (TextUtils.isEmpty(query)) {
                // 显示所有文件
                displayedFiles.addAll(allFiles);
            } else {
                // 根据查询过滤文件
                String lowerQuery = query.toLowerCase();
                for (File file : allFiles) {
                    if (file.getName().toLowerCase().contains(lowerQuery)) {
                        displayedFiles.add(file);
                    }
                }
            }

            Log.d(TAG, "过滤后的文件数: " + displayedFiles.size());

            // 更新 RecyclerView 数据
            fileAdapter.updateFiles(new ArrayList<>(displayedFiles));
        } catch (Exception e) {
            Log.e(TAG, "过滤文件时发生错误", e); // 记录详细错误
        }
    }

}
