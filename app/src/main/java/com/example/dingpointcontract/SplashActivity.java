package com.example.dingpointcontract;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3000; // 3秒后自动关闭
    private Handler handler;
    private Runnable closeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 隐藏状态栏
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // 初始化Handler
        handler = new Handler(Looper.getMainLooper());

        // 设置关闭按钮点击事件
        ImageButton closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> closeSplash());

        // 设置定时关闭
        closeRunnable = this::closeSplash;
        handler.postDelayed(closeRunnable, SPLASH_DELAY);
    }

    private void closeSplash() {
        // 移除所有待执行的回调
        handler.removeCallbacks(closeRunnable);

        // 启动主Activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        // 关闭当前Activity
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 确保在Activity销毁时移除所有待执行的回调
        if (handler != null) {
            handler.removeCallbacks(closeRunnable);
        }
    }
}