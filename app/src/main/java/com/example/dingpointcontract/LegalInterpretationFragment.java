package com.example.dingpointcontract;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LegalInterpretationFragment extends Fragment {

    private static final String TAG = "LegalInterpretFragment";
    private RecyclerView recyclerViewLegal;
    private DatabaseHelper databaseHelper;
    private List<LegalArticle> legalArticles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_legal_interpretation, container, false);

        // 为标题栏添加返回箭头
        TextView titleText = view.findViewById(R.id.title_text);
        titleText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_chevron_right_24, 0, 0, 0);
        titleText.setCompoundDrawablePadding(8);
        titleText.setOnClickListener(v -> {
            // 返回上一页
            getParentFragmentManager().popBackStack();
        });

        // 初始化RecyclerView
        recyclerViewLegal = view.findViewById(R.id.recycler_view_legal);
        recyclerViewLegal.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化数据库帮助类
        databaseHelper = new DatabaseHelper(requireContext());

        // 加载法条数据
        loadLegalArticles();
    }

    private void loadLegalArticles() {
        try {
            Log.d(TAG, "开始加载法条数据");

            // 从数据库获取法条数据
            legalArticles = getAllLegalArticles();

            Log.d(TAG, "获取到法条数据: " + (legalArticles != null ? legalArticles.size() : 0) + "条");

            // 如果没有获取到数据，直接显示本地模拟数据
            if (legalArticles == null || legalArticles.isEmpty()) {
                Log.d(TAG, "数据库中没有法条数据，使用硬编码模拟数据");
                legalArticles = getHardcodedLegalArticles();
            }

            // 设置适配器
            LegalArticleAdapter adapter = new LegalArticleAdapter(requireContext(), legalArticles, article -> {
                // 点击法条时的处理
                navigateToArticleDetail(article);
            });

            recyclerViewLegal.setAdapter(adapter);

        } catch (Exception e) {
            Log.e(TAG, "加载法条数据失败", e);
            Toast.makeText(requireContext(), "加载法条数据失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            // 出错时显示硬编码的模拟数据
            legalArticles = getHardcodedLegalArticles();

            LegalArticleAdapter adapter = new LegalArticleAdapter(requireContext(), legalArticles, article -> {
                navigateToArticleDetail(article);
            });

            recyclerViewLegal.setAdapter(adapter);
        }
    }

    private List<LegalArticle> getAllLegalArticles() {
        List<LegalArticle> articles = new ArrayList<>();

        try {
            Log.d(TAG, "尝试从数据库获取法条数据");

            // 从数据库获取法条数据
            List<DatabaseHelper.LegalArticleData> articleDataList = databaseHelper.getAllLegalArticles();

            if (articleDataList != null) {
                Log.d(TAG, "成功从数据库获取到: " + articleDataList.size() + "条法条数据");

                // 转换为LegalArticle对象
                for (DatabaseHelper.LegalArticleData data : articleDataList) {
                    articles.add(new LegalArticle(
                            data.getArticleNumber(),
                            data.getTitle(),
                            data.getContent(),
                            data.getType()
                    ));
                }
            } else {
                Log.w(TAG, "从数据库获取的法条数据为null");
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "数据库查询错误", e);
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "获取法条数据时发生未知错误", e);
            throw e;
        }

        return articles;
    }

    /**
     * 获取硬编码的法条模拟数据，作为备用方案
     */
    private List<LegalArticle> getHardcodedLegalArticles() {
        List<LegalArticle> articles = new ArrayList<>();

        // 劳动法相关法条
        articles.add(new LegalArticle(
                "第七条",
                "《中华人民共和国劳动合同法》第七条",
                "用人单位自用工之日起即与劳动者建立劳动关系。用人单位应当建立职工名册备查。",
                "劳动法"));

        articles.add(new LegalArticle(
                "第十条",
                "《中华人民共和国劳动合同法》第十条",
                "建立劳动关系，应当订立书面劳动合同。\n" +
                        "已建立劳动关系，未同时订立书面劳动合同的，应当自用工之日起一个月内订立书面劳动合同。\n" +
                        "用人单位与劳动者在用工前订立劳动合同的，劳动关系自用工之日起建立。",
                "劳动法"));

        articles.add(new LegalArticle(
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
        articles.add(new LegalArticle(
                "第二百一十二条",
                "《中华人民共和国民法典》第二百一十二条",
                "转让财产的所有权的，依照约定或者交付标的物时转移所有权，但是法律另有规定或者当事人另有约定的除外。",
                "民法典"));

        articles.add(new LegalArticle(
                "第二百二十二条",
                "《中华人民共和国民法典》第二百二十二条",
                "当事人互有债权债务，该债权债务种类相同的，任何一方可以将自己的债权与对方的到期债务抵销；但是，根据债权债务性质、按照当事人约定或者依照法律规定不得抵销的除外。\n" +
                        "当事人主张抵销的，应当通知对方。通知自到达对方时生效。抵销不得附条件或者附期限。",
                "民法典"));

        return articles;
    }

    private void navigateToArticleDetail(LegalArticle article) {
        // 创建并显示法条详情页面
        LegalArticleDetailFragment detailFragment = LegalArticleDetailFragment.newInstance(article);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}