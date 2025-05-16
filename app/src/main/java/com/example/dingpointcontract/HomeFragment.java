package com.example.dingpointcontract;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 topBanner;
    private ViewPager2 bottomBanner;
    private Handler handler;
    private Runnable topBannerRunnable;
    private Runnable bottomBannerRunnable;
    private EditText searchEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 初始化视图
        topBanner = view.findViewById(R.id.top_banner);
        bottomBanner = view.findViewById(R.id.bottom_banner);
        searchEditText = view.findViewById(R.id.search_edit_text);

        // 初始化Handler
        handler = new Handler(Looper.getMainLooper());

        // 设置广告数据
        setupBanners();

        // 设置搜索框点击事件
        setupSearchBar();

        // 设置六宫格点击事件
        setupGridClickListeners(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化视图
        topBanner = view.findViewById(R.id.top_banner);
        bottomBanner = view.findViewById(R.id.bottom_banner);
        searchEditText = view.findViewById(R.id.search_edit_text);

        // 初始化Handler
        handler = new Handler(Looper.getMainLooper());

        // 设置广告数据
        setupBanners();

        // 设置搜索框点击事件
        setupSearchBar();
    }

    private void setupBanners() {
        // 创建广告数据
        List<Integer> topBannerImages = new ArrayList<>();
        topBannerImages.add(R.drawable.home_ad_up_1); // 这里需要替换为实际的广告图片资源
        topBannerImages.add(R.drawable.baseline_home_24);
        topBannerImages.add(R.drawable.baseline_home_24);

        List<Integer> bottomBannerImages = new ArrayList<>();
        bottomBannerImages.add(R.drawable.baseline_home_24);
        bottomBannerImages.add(R.drawable.baseline_home_24);
        bottomBannerImages.add(R.drawable.baseline_home_24);

        // 设置适配器
        BannerAdapter topAdapter = new BannerAdapter(topBannerImages);
        BannerAdapter bottomAdapter = new BannerAdapter(bottomBannerImages);

        topBanner.setAdapter(topAdapter);
        bottomBanner.setAdapter(bottomAdapter);

        // 设置自动轮播
        setupAutoScroll(topBanner, 3000); // 3秒切换一次
        setupAutoScroll(bottomBanner, 4000); // 4秒切换一次
    }

    private void setupAutoScroll(ViewPager2 viewPager, long delayMillis) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int itemCount = viewPager.getAdapter().getItemCount();
                viewPager.setCurrentItem((currentItem + 1) % itemCount, true);
                handler.postDelayed(this, delayMillis);
            }
        };

        if (viewPager == topBanner) {
            topBannerRunnable = runnable;
        } else {
            bottomBannerRunnable = runnable;
        }

        handler.postDelayed(runnable, delayMillis);
    }

    private void setupSearchBar() {
        searchEditText.setOnClickListener(v -> {
            // 处理搜索框点击事件
            // TODO: 实现搜索功能
        });
    }


    private void setupGridClickListeners(View view) {
        // 专家定制
        view.findViewById(R.id.grid_expert).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "专家定制", Toast.LENGTH_SHORT).show();
            // TODO: 跳转到专家定制页面
        });

        // 法条解读
        view.findViewById(R.id.grid_interpretation_legal).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "法条解读", Toast.LENGTH_SHORT).show();
            // TODO: 跳转到法条解读页面
        });

        // 合同模板
        view.findViewById(R.id.grid_contract_template).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "合同模板", Toast.LENGTH_SHORT).show();
            // TODO: 跳转到合同模板页面
        });

        // 合同创建
        view.findViewById(R.id.grid_contract_creation).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "合同创建", Toast.LENGTH_SHORT).show();
            // TODO: 跳转到合同创建页面
        });

        // 案例分析
        view.findViewById(R.id.grid_case_analysis).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "案例分析", Toast.LENGTH_SHORT).show();
            // TODO: 跳转到案例分析页面
        });

        // 视频学习
        view.findViewById(R.id.grid_video_learn).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "视频学习", Toast.LENGTH_SHORT).show();
            // TODO: 跳转到视频学习页面
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止自动轮播
        if (handler != null) {
            handler.removeCallbacks(topBannerRunnable);
            handler.removeCallbacks(bottomBannerRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 恢复自动轮播
        if (handler != null) {
            handler.postDelayed(topBannerRunnable, 3000);
            handler.postDelayed(bottomBannerRunnable, 4000);
        }
    }

    // 广告适配器
    private static class BannerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
        private List<Integer> images;

        public BannerAdapter(List<Integer> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new BannerViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
            holder.imageView.setImageResource(images.get(position));
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        static class BannerViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            ImageView imageView;

            public BannerViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = (ImageView) itemView;
            }
        }
    }
}