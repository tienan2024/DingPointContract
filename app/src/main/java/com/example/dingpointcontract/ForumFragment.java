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
        posts.add(new Post("韩梅梅", "2小时前",
                "最近遇到一个合同纠纷，想请教一下大家...", null, 128, 32, 16,R.drawable.forum_user_1));
        posts.add(new Post("张三", "3小时前",
                "分享一个实用的合同模板，希望对大家有帮助...", null, 256, 64, 32,R.drawable.forum_user_2));
        posts.add(new Post("李四", "5小时前",
                "关于劳动合同的一些常见问题解答...", null, 512, 128, 64,R.drawable.forum_user_3));
        posts.add(new Post("王五", "7小时前",
                "最近遇到一个合同纠纷，想请教一下大家...", null, 11, 45, 14,R.drawable.forum_user_4));
        posts.add(new Post("马冬梅", "7小时前",
                "最近遇到一个合同纠纷，想请教一下大家...", null, 235, 145, 24,R.drawable.forum_user_5));
        posts.add(new Post("刘亦菲", "2小时前",
                "最近遇到一个合同纠纷，想请教一下大家...", null, 128, 32, 16,R.drawable.forum_user_6));
        posts.add(new Post("猪猪侠", "3小时前",
                "分享一个实用的合同模板，希望对大家有帮助...", null, 256, 64, 32,R.drawable.forum_user_7));
        posts.add(new Post("和淑妃", "5小时前",
                "关于劳动合同的一些常见问题解答...", null, 512, 128, 64,R.drawable.forum_user_8));
        posts.add(new Post("雷军", "7小时前",
                "感谢这个软件，老人家我差点就酿成大祸了...", null, 11, 45, 14,R.drawable.forum_user_9));
        posts.add(new Post("鲁大师", "7小时前",
                "坏人也太多了，咱这种老年人可真要小心啊...", null, 235, 145, 24,R.drawable.forum_user_10));
        posts.add(new Post("李奶奶", "7小时前",
                "感谢这个软件，老人家我差点就酿成大祸了...", null, 11, 45, 14,R.drawable.forum_user_11));
        posts.add(new Post("赵大爷", "7小时前",
                "坏人也太多了，咱这种老年人可真要小心啊...", null, 235, 145, 24,R.drawable.forum_user_12));

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