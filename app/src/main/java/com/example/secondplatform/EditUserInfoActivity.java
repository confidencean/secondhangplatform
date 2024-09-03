package com.example.secondplatform;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditUserInfoActivity extends AppCompatActivity {

    private EditText etAvatarUrl;
    private Button btnUpdate;
    private String userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        etAvatarUrl = findViewById(R.id.etAvatarUrl);
        btnUpdate = findViewById(R.id.btnUpdate);

        // 从 SharedPreferences 获取 userId
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        btnUpdate.setOnClickListener(v -> {
            String avatarUrl = etAvatarUrl.getText().toString();
            if (!avatarUrl.isEmpty() && userId != null) {
                new UpdateUserInfoTask().execute(avatarUrl);
            } else {
                Toast.makeText(EditUserInfoActivity.this, "请输入头像 URL 或登录信息有误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class UpdateUserInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String avatarUrl = params[0];
            try {
                URL url = new URL("https://api-store.openguet.cn/api/member/tran/user/update");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("appId", "1db7ee6c102c40e892e8e75d2b8b40b7");
                conn.setRequestProperty("appSecret", "27868ea89849efca5477b8d32b055187b2068");
                conn.setDoOutput(true);

                // 构建请求体
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", userId); // 添加 userId 参数
                jsonObject.put("avatar", avatarUrl); // 添加 avatar 参数

                // 发送请求
                OutputStream os = conn.getOutputStream();
                PrintWriter writer = new PrintWriter(os);
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                // 处理响应
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
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
            super.onPostExecute(result);
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int code = jsonResponse.getInt("code");
                if (code == 200) {
                    Toast.makeText(EditUserInfoActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditUserInfoActivity.this, "修改失败: " + jsonResponse.getString("msg"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditUserInfoActivity.this, "解析错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
