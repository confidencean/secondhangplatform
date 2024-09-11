package com.example.secondplatform;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendMessageActivity extends AppCompatActivity {

    private EditText etMessageContent;
    private EditText etToUserId;
    private Button btnSend;
    private String userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        etMessageContent = findViewById(R.id.etMessageContent);
        etToUserId = findViewById(R.id.etToUserId);
        btnSend = findViewById(R.id.btnSend);

        // 从 SharedPreferences 获取 userId
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        btnSend.setOnClickListener(v -> {
            String content = etMessageContent.getText().toString().trim();
            String toUserId = etToUserId.getText().toString().trim();

            if (!content.isEmpty() && !toUserId.isEmpty()) {
                new SendMessageTask().execute(content, toUserId);
            } else {
                Toast.makeText(SendMessageActivity.this, "请输入消息内容和接收者ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class SendMessageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String content = params[0];
            String toUserId = params[1];
            try {
                URL url = new URL("https://api-store.openguet.cn/api/member/tran/chat");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("appId", "1db7ee6c102c40e892e8e75d2b8b40b7");
                conn.setRequestProperty("appSecret", "27868ea89849efca5477b8d32b055187b2068");
                conn.setDoOutput(true);

                // 创建 JSON 参数
                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("content", content);
                    jsonParam.put("toUserId", toUserId);
                    jsonParam.put("userId", userId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 发送请求
                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int code = jsonResponse.getInt("code");
                if (code == 200) {
                    Toast.makeText(SendMessageActivity.this, "消息发送成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SendMessageActivity.this, "发送失败: " + jsonResponse.getString("msg"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(SendMessageActivity.this, "解析错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
