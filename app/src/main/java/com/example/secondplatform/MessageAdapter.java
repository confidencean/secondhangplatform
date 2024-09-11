package com.example.secondplatform;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<CustomMessage> messageList;

    public MessageAdapter(List<CustomMessage> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        CustomMessage message = messageList.get(position);
        holder.tvFromUsername.setText(message.getFromUsername());
        holder.tvContent.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView tvFromUsername;
        TextView tvContent;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFromUsername = itemView.findViewById(R.id.tvFromUsername);
            tvContent = itemView.findViewById(R.id.tvContent);
        }
    }
}
