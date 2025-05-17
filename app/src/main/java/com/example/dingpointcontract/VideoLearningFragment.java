package com.example.dingpointcontract;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class VideoLearningFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_learning, container, false);

        // 为标题栏添加返回箭头
        TextView titleText = view.findViewById(R.id.title_text);
        titleText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_chevron_right_24, 0, 0, 0);
        titleText.setCompoundDrawablePadding(8);
        titleText.setOnClickListener(v -> {
            // 返回上一页
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 初始化视图和设置监听器
    }
}