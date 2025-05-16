package com.example.dingpointcontract;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ChatHistoryViewHolder> {
    private List<RiskEvaluateFragment.ChatMessage> chatMessages;
    private OnItemClickListener onItemClickListener;

    public ChatHistoryAdapter(List<RiskEvaluateFragment.ChatMessage> chatMessages, OnItemClickListener onItemClickListener) {
        this.chatMessages = chatMessages;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ChatHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_history, parent, false);
        return new ChatHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHistoryViewHolder holder, int position) {
        RiskEvaluateFragment.ChatMessage message = chatMessages.get(position);

        // 显示用户消息
        holder.userMessage.setText(message.getUserMessage());
        holder.aiResponse.setText(message.getAiResponse());

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(message));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public interface OnItemClickListener {
        void onItemClick(RiskEvaluateFragment.ChatMessage message);
    }

    public static class ChatHistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView userMessage;
        public TextView aiResponse;

        public ChatHistoryViewHolder(View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.user_message);
            aiResponse = itemView.findViewById(R.id.ai_response);
        }
    }
}
