package com.example.secondplatform;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReceivedMessagesActivity extends AppCompatActivity {

    private TextView messageTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_messages);

        // 获取 TextView 用于显示消息
        messageTextView = findViewById(R.id.messageTextView);

        // 从 SharedPreferences 中获取 userId 和 fromUserId
        SharedPreferences sharedPreferencesUser = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferencesUser.getString("userId", null);

        SharedPreferences sharedPreferencesFromUser = getSharedPreferences("FromUserId", MODE_PRIVATE);
        String fromUserId = sharedPreferencesFromUser.getString("fromUserId", null);

        if (userId != null && fromUserId != null) {
            // 执行异步任务获取消息
            new GetMessagesTask().execute(userId, fromUserId);
        } else {
            Toast.makeText(this, "UserId 或 FromUserId 不存在", Toast.LENGTH_LONG).show();
        }
    }

    // 异步任务类，用于从服务器获取消息
    private class GetMessagesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String userId = params[0];
            String fromUserId = params[1];
            String urlString = "https://api-store.openguet.cn/api/member/tran/chat/message?fromUserId=" + fromUserId + "&userId=" + userId;

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                return result.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // 解析返回的 JSON 数据
                    JSONObject jsonResponse = new JSONObject(result);
                    int code = jsonResponse.getInt("code");

                    if (code == 200) {
                        JSONObject data = jsonResponse.getJSONObject("data");
                        JSONArray records = data.getJSONArray("records");

                        if (records.length() > 0) {
                            JSONObject firstMessage = records.getJSONObject(0);
                            String fromUsername = firstMessage.getString("fromUsername");
                            String content = firstMessage.getString("content");

                            // 显示消息内容
                            messageTextView.setText("From: " + fromUsername + "\nMessage: " + content);
                        } else {
                            messageTextView.setText("没有收到的消息");
                        }

                    } else {
                        Toast.makeText(ReceivedMessagesActivity.this, "加载失败: " + jsonResponse.getString("msg"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ReceivedMessagesActivity.this, "解析错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ReceivedMessagesActivity.this, "网络请求失败", Toast.LENGTH_LONG).show();
            }
        }
    }
}
