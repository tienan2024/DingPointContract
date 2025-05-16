package com.example.dingpointcontract;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.Log;

import java.util.Locale;

public class LocaleHelper {
    private static final String TAG = "LocaleHelper";

    public static Context setLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
        } else {
            config.setLocale(locale);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context = context.createConfigurationContext(config);
        } else {
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        // 输出日志，确认配置更新
        Log.d(TAG, "Updated locale: " + locale.getLanguage());
        Log.d(TAG, "Current configuration locale: " + config.getLocales().get(0).getLanguage());

        return context;
    }

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context);
        return setLocale(context, lang);
    }

    private static String getPersistedData(Context context) {
        String PREFERENCES_NAME = "com.example.dingpointcontract.PREFERENCES";
        String LANGUAGE_KEY = "language";
        android.content.SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(LANGUAGE_KEY, Locale.getDefault().getLanguage());
    }
}