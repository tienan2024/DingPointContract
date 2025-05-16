package com.example.dingpointcontract;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private static final String PREFERENCES_NAME = "com.example.dingpointcontract.PREFERENCES";
    private static final String LANGUAGE_KEY = "language";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 设置点击事件
        setupClickListeners(view);

        return view;
    }

    private void setupClickListeners(View view) {
        // 语言设置
        view.findViewById(R.id.language_item).setOnClickListener(v -> {
            // 显示语言选择对话框
            showLanguageDialog();
        });

        // 关于我们
        view.findViewById(R.id.about_item).setOnClickListener(v -> {
            // TODO: 跳转到关于我们页面
            //Toast.makeText(requireContext(), getString(R.string.profile_about), Toast.LENGTH_SHORT).show();
            showAboutDialog();
        });

        // 意见反馈
        view.findViewById(R.id.feedback_item).setOnClickListener(v -> {
            // TODO: 跳转到意见反馈页面
            Toast.makeText(requireContext(), getString(R.string.profile_feedback), Toast.LENGTH_SHORT).show();
        });
    }

    private void showLanguageDialog() {
        // 创建语言选择对话框
        String[] languages = {"English", "中文"};
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.profile_language)
                .setItems(languages, (dialog, which) -> {
                    String lang = which == 0 ? "en" : "zh";
                    setLocale(lang);
                })
                .show();
    }

    private void setLocale(String lang) {
        LocaleHelper.setLocale(requireContext(), lang);
        // 保存语言选择到SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE_KEY, lang);
        editor.apply();

        Toast.makeText(requireContext(), getString(R.string.tips), Toast.LENGTH_SHORT).show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.about))
                .setMessage(getString(R.string.about_message))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}