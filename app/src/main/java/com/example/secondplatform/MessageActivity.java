package com.example.secondplatform;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MessageActivity extends AppCompatActivity {

    private Button btnSendMessage;
    private Button btnReceivedMessages;
    private Button btnUserList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        btnSendMessage=findViewById(R.id.btnSendMessage);
        btnReceivedMessages = findViewById(R.id.btnReceivedMessages);
        btnUserList = findViewById(R.id.btnUserList);

        // 跳转到发送消息页面
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MessageActivity.this, SendMessageActivity.class);
               startActivity(intent);
            }
        });

        // 跳转到查看收到的消息页面
        btnReceivedMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MessageActivity.this, ReceivedMessagesActivity.class);
                startActivity(intent);
            }
        });

        // 跳转到用户列表页面
        btnUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, FromUserActivity.class);
                startActivity(intent);
            }
        });
    }
}
