package com.example.dingpointcontract;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class SettingsFragment extends Fragment {
    private Spinner languageSpinner;
    private static final String PREFERENCES_NAME = "com.example.dingpointcontract.PREFERENCES";
    private static final String LANGUAGE_KEY = "language";
    private boolean isUserInteraction = true; // 防止初始化时触发

    private static final String THEME_MODE_KEY = "theme_mode";
    private static final String FOLLOW_SYSTEM_KEY = "follow_system";
    private SwitchCompat themeSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        languageSpinner = view.findViewById(R.id.language_spinner);

        // 定义语言选项
        String[] languages = {"English", "中文"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // 读取已保存的语言
        SharedPreferences preferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String savedLanguage = preferences.getString(LANGUAGE_KEY, Locale.getDefault().getLanguage());

        // 根据存储的语言设置 Spinner 默认选项
        if ("en".equals(savedLanguage)) {
            languageSpinner.setSelection(0, false);
        } else if ("zh".equals(savedLanguage)) {
            languageSpinner.setSelection(1, false);
        }

        // 监听语言选择
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isUserInteraction) {
                    isUserInteraction = true; // 忽略初始化触发的事件
                    return;
                }

                String newLang = position == 0 ? "en" : "zh";

                if (!newLang.equals(savedLanguage)) { // 仅在语言不同的情况下更新
                    setLocale(newLang);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 添加“关于”点击监听
        TextView aboutText = view.findViewById(R.id.about_text);
        aboutText.setOnClickListener(v -> showAboutDialog());

        return view;
    }

    /**
     * 更新语言，并刷新当前 Fragment 以应用新语言
     */
    private void setLocale(String lang) {
        // 更新 SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE_KEY, lang);
        editor.apply();

        // 刷新当前 Fragment 以应用新语言
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .detach(this)
                .attach(this)
                .commit();

        // 显示提示信息
        Toast.makeText(requireContext(), getString(R.string.language_changed), Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出关于对话框，显示应用相关信息
     */
    private void showAboutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.about))
                .setMessage(getString(R.string.about_message))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
