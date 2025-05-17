package com.example.dingpointcontract;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private HomeFragment mHomeFragment;
    private ForumFragment mForumFragment;
    private RiskEvaluateFragment mRiskEvaluateFragment;
    private ProfileFragment mProfileFragment;

    private BottomNavigationView mBottomNavigationView;
    private Fragment mCurrentFragment;

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 Fragment 实例
        mHomeFragment = new HomeFragment();
        mForumFragment = new ForumFragment();
        mRiskEvaluateFragment = new RiskEvaluateFragment();
        mProfileFragment = new ProfileFragment();

        // 初始化控件
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        // 设置点击事件
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment targetFragment = null;
                if (item.getItemId() == R.id.nav_home) {
                    targetFragment = mHomeFragment;
                } else if (item.getItemId() == R.id.nav_forum) {
                    targetFragment = mForumFragment;
                } else if (item.getItemId() == R.id.nav_risk_evaluate) {
                    targetFragment = mRiskEvaluateFragment;
                } else if (item.getItemId() == R.id.nav_profile) {
                    targetFragment = mProfileFragment;
                }

                if (targetFragment != null) {
                    switchFragment(targetFragment);
                }
                return true;
            }
        });

        // 默认加载"首页"Fragment
        mCurrentFragment = mHomeFragment;
        loadFragment(mHomeFragment);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    private void switchFragment(Fragment targetFragment) {
        // 清除后退栈中的所有子Fragment
        clearBackStack();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction.add(R.id.fragment_container, targetFragment);
        }
        transaction.hide(mCurrentFragment);
        transaction.show(targetFragment);
        transaction.commit();
        mCurrentFragment = targetFragment;
    }

    /**
     * 清除后退栈中所有Fragment
     */
    private void clearBackStack() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    /**
     * 处理返回键逻辑
     */
    @Override
    public void onBackPressed() {
        // 如果后退栈中有Fragment，优先弹出
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}