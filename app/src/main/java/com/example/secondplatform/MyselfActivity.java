package com.example.secondplatform;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyselfActivity extends AppCompatActivity {
    private String userId;
    private int money;
    private String avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself);

        // 获取保存的信息
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null); // 从 SharedPreferences 获取 userId
        money = sharedPreferences.getInt("money", 0); // 从 SharedPreferences 获取 money
        avatar = sharedPreferences.getString("avatar", null); // 从 SharedPreferences 获取 avatar

        Button btnEditUserInfo = findViewById(R.id.UserInfo);
        Button btnRecharge = findViewById(R.id.Recharge);
        Button btnBuyrecord = findViewById(R.id.Buyrecord);
        Button btnMoneyrecord = findViewById(R.id.Moneyrecord);

        btnEditUserInfo.setOnClickListener(v -> {
            // 跳转到修改用户信息页面，传递 userId
            Intent intent = new Intent(MyselfActivity.this, EditUserInfoActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnRecharge.setOnClickListener(v -> {
            // 跳转到充值页面
            Intent intent = new Intent(MyselfActivity.this, RechargeActivity.class);
            startActivity(intent);
        });

        btnBuyrecord.setOnClickListener(view -> {
            Intent intent = new Intent(MyselfActivity.this, BuyRecordActivity.class);
            startActivity(intent);
        });

        btnMoneyrecord.setOnClickListener(view -> {
            // 触发获取总收入和总支出的网络请求
            if (userId != null && !userId.isEmpty()) {
                new FetchMoneyRecordTask().execute(userId);
            } else {
                Toast.makeText(MyselfActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 异步任务获取总收入和总支出
    private class FetchMoneyRecordTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String userId = params[0];
            try {
                URL url = new URL("https://api-store.openguet.cn/api/member/tran/trading/allMoney?userId=" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("appId", "1db7ee6c102c40e892e8e75d2b8b40b7");
                conn.setRequestProperty("appSecret", "27868ea89849efca5477b8d32b055187b2068");

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
            super.onPostExecute(result);
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int code = jsonResponse.getInt("code");
                if (code == 200) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    double totalSpending = data.optDouble("totalSpending", 0);
                    double totalRevenue = data.optDouble("totalRevenue", 0);

                    // 显示总支出与总收入
                    String message = "总支出: " + totalSpending + "\n总收入: " + totalRevenue;
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "请求失败: " + jsonResponse.getString("msg"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "解析错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
