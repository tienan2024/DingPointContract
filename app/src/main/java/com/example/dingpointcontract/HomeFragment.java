package com.example.dingpointcontract;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 topBanner;
    private ViewPager2 bottomBanner;
    private Handler handler;
    private Runnable topBannerRunnable;
    private Runnable bottomBannerRunnable;
    private DatabaseHelper databaseHelper;

    // 浏览器式搜索相关组件
    private View browserSearchLayout;
    private EditText browserSearchEditText;
    private ImageView clearSearchButton;
    private CardView searchResultsCard;
    private RecyclerView browserSearchResults;
    private View homeContentView;  // 主页内容（六宫格和广告栏）

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 初始化视图
        topBanner = view.findViewById(R.id.top_banner);
        bottomBanner = view.findViewById(R.id.bottom_banner);

        // 添加浏览器式搜索布局
        ViewGroup rootView = (ViewGroup) view;
        browserSearchLayout = inflater.inflate(R.layout.layout_browser_search, rootView, false);
        rootView.addView(browserSearchLayout, 0); // 添加到顶部

        // 隐藏原有搜索栏
        View oldSearchBar = (View) view.findViewById(R.id.search_edit_text).getParent().getParent();
        oldSearchBar.setVisibility(View.GONE);

        // 初始化浏览器式搜索相关组件
        browserSearchEditText = browserSearchLayout.findViewById(R.id.browser_search_edit_text);
        clearSearchButton = browserSearchLayout.findViewById(R.id.clear_search_button);
        searchResultsCard = browserSearchLayout.findViewById(R.id.search_results_card);
        browserSearchResults = browserSearchLayout.findViewById(R.id.browser_search_results);
        browserSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 设置主页内容视图（六宫格和广告栏）
        homeContentView = (View) view.findViewById(R.id.grid_expert).getParent().getParent(); // GridLayout和广告栏的父布局

        // 初始化Handler
        handler = new Handler(Looper.getMainLooper());

        // 设置广告数据
        setupBanners();

        // 设置浏览器式搜索
        setupBrowserSearch();

        // 设置六宫格点击事件
        setupGridClickListeners(view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(requireContext());
    }

    private void setupBanners() {
        // 创建广告数据
        List<Integer> topBannerImages = new ArrayList<>();
        topBannerImages.add(R.drawable.home_ad_up_1); // 这里需要替换为实际的广告图片资源
        topBannerImages.add(R.drawable.home_ad_up_2);
        topBannerImages.add(R.drawable.home_ad_up_3);

        List<Integer> bottomBannerImages = new ArrayList<>();
        bottomBannerImages.add(R.drawable.home_ad_under_1);
        bottomBannerImages.add(R.drawable.home_ad_under_2);
        bottomBannerImages.add(R.drawable.home_ad_under_3);

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

    private void setupBrowserSearch() {
        // 设置文本变化监听器，实现实时搜索
        browserSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                clearSearchButton.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);

                if (query.isEmpty()) {
                    // 如果搜索框为空，隐藏搜索结果，显示主页内容
                    searchResultsCard.setVisibility(View.GONE);
                    homeContentView.setVisibility(View.VISIBLE);
                } else {
                    // 否则，显示搜索结果，隐藏主页内容
                    performBrowserSearch(query);
                    searchResultsCard.setVisibility(View.VISIBLE);
                    homeContentView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 设置回车键搜索功能
        browserSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performBrowserSearch(browserSearchEditText.getText().toString().trim());
                return true;
            }
            return false;
        });

        // 设置清除按钮点击事件
        clearSearchButton.setOnClickListener(v -> {
            browserSearchEditText.setText("");
            searchResultsCard.setVisibility(View.GONE);
            homeContentView.setVisibility(View.VISIBLE);
        });
    }

    private void performBrowserSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            // 清空搜索结果，恢复主页
            searchResultsCard.setVisibility(View.GONE);
            homeContentView.setVisibility(View.VISIBLE);
            return;
        }

        try {
            List<DatabaseHelper.SearchResult> searchResults = databaseHelper.search(query);

            // 添加调试信息，显示找到的结果数量
            Toast.makeText(requireContext(), "找到 " + searchResults.size() + " 个结果", Toast.LENGTH_SHORT).show();
            Log.d("HomeFragment", "搜索查询: " + query + ", 找到结果: " + searchResults.size());

            // 打印搜索结果详情
            for (DatabaseHelper.SearchResult result : searchResults) {
                String fileName = result.getFileName() != null ? result.getFileName() : "N/A";
                String articleNumber = result.getArticleNumber() != null ? result.getArticleNumber() : "N/A";
                String type = result.getType() != null ? result.getType() : "未知";
                String title = result.getTitle() != null ? result.getTitle() : "无标题";

                Log.d("SearchResult", "类型: " + type +
                        ", 标题: " + title +
                        ", 文件名: " + fileName +
                        ", 法条: " + articleNumber);
            }

            // 创建适配器，传递搜索关键词用于高亮显示
            SearchResultAdapter adapter = new SearchResultAdapter(searchResults, result -> {
                // 处理搜索结果点击事件
                if (result.getType() == null) {
                    Toast.makeText(requireContext(), "无效的结果类型", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (result.getType()) {
                    case "template":
                        // 跳转到合同模板详情
                        if (result.getFileName() != null) {
                            navigateToTemplateDetail(result.getFileName());
                        } else {
                            Toast.makeText(requireContext(), "文件名不存在", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "contract":
                        // 跳转到已创建合同详情
                        if (result.getFileName() != null) {
                            navigateToContractDetail(result.getFileName());
                        } else {
                            Toast.makeText(requireContext(), "文件名不存在", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "article":
                        // 跳转到法条详情
                        if (result.getArticleNumber() != null) {
                            navigateToArticleDetail(result.getArticleNumber());
                        } else {
                            Toast.makeText(requireContext(), "法条号不存在", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        Toast.makeText(requireContext(), "未知的结果类型", Toast.LENGTH_SHORT).show();
                        break;
                }

                // 点击后清空搜索，恢复主页
                browserSearchEditText.setText("");
                searchResultsCard.setVisibility(View.GONE);
                homeContentView.setVisibility(View.VISIBLE);
            }, query);

            browserSearchResults.setAdapter(adapter);

            // 显示搜索结果，隐藏主页内容
            searchResultsCard.setVisibility(View.VISIBLE);
            homeContentView.setVisibility(View.GONE);

        } catch (Exception e) {
            Log.e("HomeFragment", "搜索过程中出错: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(requireContext(), "搜索出错：" + e.getMessage(), Toast.LENGTH_SHORT).show();

            // 出错时恢复主页内容
            searchResultsCard.setVisibility(View.GONE);
            homeContentView.setVisibility(View.VISIBLE);
        }
    }

    private void navigateToTemplateDetail(String fileName) {
        // TODO: 实现跳转到合同模板详情页面的逻辑
        Toast.makeText(requireContext(), "查看模板: " + fileName, Toast.LENGTH_SHORT).show();
    }

    private void navigateToContractDetail(String fileName) {
        // TODO: 实现跳转到已创建合同详情页面的逻辑
        Toast.makeText(requireContext(), "查看合同: " + fileName, Toast.LENGTH_SHORT).show();
    }

    private void navigateToArticleDetail(String articleNumber) {
        Log.d("HomeFragment", "跳转到法条详情，法条号: " + articleNumber);
        // 从数据库获取完整的法条信息
        DatabaseHelper.LegalArticleData articleData = databaseHelper.getLegalArticleByNumber(articleNumber);

        if (articleData != null) {
            // 将DatabaseHelper.LegalArticleData转换为LegalArticle
            LegalArticle article = new LegalArticle(
                    articleData.getArticleNumber(),
                    articleData.getTitle(),
                    articleData.getContent(),
                    articleData.getType()
            );

            // 创建法条详情Fragment并传递数据
            LegalArticleDetailFragment detailFragment = LegalArticleDetailFragment.newInstance(article);

            // 跳转到详情页
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
            // 如果数据库中找不到，使用硬编码的备用数据（基于articleNumber）
            LegalArticle backupArticle = findBackupArticleByNumber(articleNumber);

            if (backupArticle != null) {
                // 找到了备用数据，跳转到详情页
                detailFragment = LegalArticleDetailFragment.newInstance(backupArticle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                // 实在找不到任何数据，显示错误信息
                Toast.makeText(requireContext(), "未找到法条: " + articleNumber, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 获取备用的法条数据
     */
    private LegalArticle findBackupArticleByNumber(String articleNumber) {
        // 预设的备用法条数据
        List<LegalArticle> backupArticles = new ArrayList<>();

        // 劳动法相关法条
        backupArticles.add(new LegalArticle(
                "第七条",
                "《中华人民共和国劳动合同法》第七条",
                "用人单位自用工之日起即与劳动者建立劳动关系。用人单位应当建立职工名册备查。",
                "劳动法"));

        backupArticles.add(new LegalArticle(
                "第十条",
                "《中华人民共和国劳动合同法》第十条",
                "建立劳动关系，应当订立书面劳动合同。\n" +
                        "已建立劳动关系，未同时订立书面劳动合同的，应当自用工之日起一个月内订立书面劳动合同。\n" +
                        "用人单位与劳动者在用工前订立劳动合同的，劳动关系自用工之日起建立。",
                "劳动法"));

        backupArticles.add(new LegalArticle(
                "第三十八条",
                "《中华人民共和国劳动合同法》第三十八条",
                "用人单位有下列情形之一的，劳动者可以解除劳动合同：\n" +
                        "（一）未按照劳动合同约定提供劳动保护或者劳动条件的；\n" +
                        "（二）未及时足额支付劳动报酬的；\n" +
                        "（三）未依法为劳动者缴纳社会保险费的；\n" +
                        "（四）用人单位的规章制度违反法律、法规的规定，损害劳动者权益的；\n" +
                        "（五）因本法第二十六条第一款规定的情形致使劳动合同无效的；\n" +
                        "（六）法律、行政法规规定劳动者可以解除劳动合同的其他情形。\n" +
                        "用人单位以暴力、威胁或者非法限制人身自由的手段强迫劳动者劳动的，或者用人单位违章指挥、强令冒险作业危及劳动者人身安全的，劳动者可以立即解除劳动合同，不需事先告知用人单位。",
                "劳动法"));

        // 民法典相关法条
        backupArticles.add(new LegalArticle(
                "第二百一十二条",
                "《中华人民共和国民法典》第二百一十二条",
                "转让财产的所有权的，依照约定或者交付标的物时转移所有权，但是法律另有规定或者当事人另有约定的除外。",
                "民法典"));

        backupArticles.add(new LegalArticle(
                "第二百二十二条",
                "《中华人民共和国民法典》第二百二十二条",
                "当事人互有债权债务，该债权债务种类相同的，任何一方可以将自己的债权与对方的到期债务抵销；但是，根据债权债务性质、按照当事人约定或者依照法律规定不得抵销的除外。\n" +
                        "当事人主张抵销的，应当通知对方。通知自到达对方时生效。抵销不得附条件或者附期限。",
                "民法典"));

        // 在备用数据中查找匹配的法条
        for (LegalArticle article : backupArticles) {
            if (article.getArticleNumber().equals(articleNumber)) {
                return article;
            }
        }

        // 如果备用数据中也没有，可能是搜索结果标题包含法条号，尝试部分匹配
        for (LegalArticle article : backupArticles) {
            if (article.getArticleNumber().contains(articleNumber) ||
                    articleNumber.contains(article.getArticleNumber())) {
                return article;
            }
        }

        return null;
    }

    private void setupGridClickListeners(View view) {
        // 专家定制
        view.findViewById(R.id.grid_expert).setOnClickListener(v -> {
            navigateToFragment(new ExpertCustomFragment());
        });

        // 法条解读
        view.findViewById(R.id.grid_interpretation_legal).setOnClickListener(v -> {
            navigateToFragment(new LegalInterpretationFragment());
        });

        // 合同模板
        view.findViewById(R.id.grid_contract_template).setOnClickListener(v -> {
            navigateToFragment(new ContractTemplateFragment());
        });

        // 合同创建
        view.findViewById(R.id.grid_contract_creation).setOnClickListener(v -> {
            navigateToFragment(new ContractCreateFragment());
        });

        // 案例分析
        view.findViewById(R.id.grid_case_analysis).setOnClickListener(v -> {
            navigateToFragment(new CaseAnalysisFragment());
        });

        // 视频学习
        view.findViewById(R.id.grid_video_learn).setOnClickListener(v -> {
            navigateToFragment(new VideoLearningFragment());
        });
    }

    private void navigateToFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
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

    private static class BannerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
        private List<Integer> images;

        public BannerAdapter(List<Integer> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_banner, parent, false);
            return new BannerViewHolder(view);
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
                imageView = itemView.findViewById(R.id.banner_image);
            }
        }
    }
}
