package com.example.secondplatform;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteProductActivity extends AppCompatActivity {

    private EditText etGoodsId;
    private Button btnDelete;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);

        etGoodsId = findViewById(R.id.etGoodsId);
        btnDelete = findViewById(R.id.btnDelete);

        // 通过 SharedPreferences 获取 userId
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        if (userId == null) {
            Toast.makeText(this, "无法获取用户ID，请重新登录", Toast.LENGTH_SHORT).show();
            finish(); // 关闭当前活动，因为没有用户ID
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goodsId = etGoodsId.getText().toString();

                if (!goodsId.isEmpty()) {
                    new DeleteProductTask().execute(goodsId, userId); // 使用从 SharedPreferences 中获取的 userId
                } else {
                    Toast.makeText(DeleteProductActivity.this, "请输入商品ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class DeleteProductTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String goodsId = params[0];
            String userId = params[1];
            try {
                URL url = new URL("https://api-store.openguet.cn/api/member/tran/goods/delete");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("appId", "1db7ee6c102c40e892e8e75d2b8b40b7");
                conn.setRequestProperty("appSecret", "27868ea89849efca5477b8d32b055187b2068");

                // 构建 JSON 数据
                String jsonInputString = "{\"goodsId\":\"" + goodsId + "\", \"userId\":\"" + userId + "\"}";
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "商品删除成功";
                } else {
                    return "商品删除失败，错误代码: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "请求失败: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(DeleteProductActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
}
