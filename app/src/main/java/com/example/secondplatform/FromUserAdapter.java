package com.example.secondplatform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class FromUserAdapter extends BaseAdapter {

    private Context context;
    private List<FromUserMessage> messageList;

    public FromUserAdapter(Context context, List<FromUserMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.received_fromuser_item, parent, false);
        }

        TextView tvFromUserId = convertView.findViewById(R.id.tvFromUserId);
        TextView tvUsername = convertView.findViewById(R.id.tvUsername);
        TextView tvUnReadNum = convertView.findViewById(R.id.tvUnReadNum);

        FromUserMessage message = messageList.get(position);
        tvFromUserId.setText("From User ID: " + message.getFromUserId());
        tvUsername.setText("Username: " + message.getUsername());
        tvUnReadNum.setText("Unread Messages: " + message.getUnReadNum());

        return convertView;
    }
}
