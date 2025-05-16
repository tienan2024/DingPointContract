package com.example.dingpointcontract;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        // 调用 LocaleHelper 类的 onAttach 方法来应用用户选择的语言
        Context context = LocaleHelper.onAttach(base);
        super.attachBaseContext(context);
    }
}