package com.example.dingpointcontract;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {

    private final List<Template> templates;
    private final OnTemplateClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnTemplateClickListener {
        void onTemplateClick(Template template);
    }

    public TemplateAdapter(List<Template> templates, OnTemplateClickListener listener) {
        this.templates = templates;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_template, parent, false);
        return new TemplateViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int position) {
        Template template = templates.get(position);

        // 设置显示文本
        holder.templateName.setText(template.getDisplayName());

        // 处理选中状态
        holder.itemView.setSelected(position == selectedPosition);

        // 设置点击监听
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // 局部刷新提高性能
            if (previousSelected != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousSelected);
            }
            notifyItemChanged(selectedPosition);

            listener.onTemplateClick(template);
        });

        // 设置长按效果（可选）
        holder.itemView.setOnLongClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    // 获取当前选中项
    public Template getSelectedTemplate() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            return templates.get(selectedPosition);
        }
        return null;
    }

    // 清除选中状态
    public void clearSelection() {
        int previousSelected = selectedPosition;
        selectedPosition = RecyclerView.NO_POSITION;
        notifyItemChanged(previousSelected);
    }

    static class TemplateViewHolder extends RecyclerView.ViewHolder {
        TextView templateName;

        public TemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            templateName = itemView.findViewById(R.id.templateName);

            // 初始化视图状态（可选）
            itemView.setClickable(true);
            itemView.setFocusable(true);
        }
    }
}
