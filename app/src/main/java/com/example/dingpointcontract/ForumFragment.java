package com.example.dingpointcontract;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;

public class ForumFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostAdapter postAdapter;
    private List<Post> posts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        // 初始化视图
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts);
        recyclerView.setAdapter(postAdapter);

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(this::refreshPosts);

        // 加载初始数据
        loadPosts();

        return view;
    }

    private void loadPosts() {
        // 模拟加载数据
        posts.clear();
        posts.add(new Post("张三", "2小时前",
                "最近遇到一个合同纠纷，想请教一下大家...", null, 128, 32, 16));
        posts.add(new Post("李四", "3小时前",
                "分享一个实用的合同模板，希望对大家有帮助...", null, 256, 64, 32));
        posts.add(new Post("王五", "5小时前",
                "关于劳动合同的一些常见问题解答...", null, 512, 128, 64));
        postAdapter.notifyDataSetChanged();
    }

    private void refreshPosts() {
        // 模拟网络请求延迟
        new Handler().postDelayed(() -> {
            loadPosts();
            swipeRefreshLayout.setRefreshing(false);
        }, 1500);
    }
}