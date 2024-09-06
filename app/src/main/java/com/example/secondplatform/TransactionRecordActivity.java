package com.example.secondplatform;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TransactionRecordActivity extends AppCompatActivity {
    private ListView listViewRecords;
    private ArrayList<String> recordsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_record);

        listViewRecords = findViewById(R.id.listViewRecords);
        recordsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recordsList);
        listViewRecords.setAdapter(adapter);

        // 获取当前登录的用户ID
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        // 请求交易记录
        new FetchTransactionRecordsTask().execute(userId);
    }

    private class FetchTransactionRecordsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String userId = params[0];
            try {
                // 请求URL
                URL url = new URL("https://api-store.openguet.cn/api/member/tran/trading/records?userId=" + userId);
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
                // 解析JSON数据
                JSONObject jsonResponse = new JSONObject(result);
                int code = jsonResponse.getInt("code");
                if (code == 200) {
                    JSONArray recordsArray = jsonResponse.getJSONObject("data").getJSONArray("records");

                    for (int i = 0; i < recordsArray.length(); i++) {
                        JSONObject record = recordsArray.getJSONObject(i);
                        String goodsId = record.getString("goodsId");
                        String buyerId = record.optString("buyerId", "无买家ID");
                        String sellerId = record.getString("sellerId");
                        String sellerName = record.getString("sellerName");
                        String goodsDescription = record.getString("goodsDescription");
                        double price = record.getDouble("price");

                        // 将需要的字段加入列表
                        String recordText = "商品ID: " + goodsId + "\n" +
                                "买家ID: " + buyerId + "\n" +
                                "卖家ID: " + sellerId + "\n" +
                                "卖家名: " + sellerName + "\n" +
                                "商品描述: " + goodsDescription + "\n" +
                                "价格: " + price;

                        recordsList.add(recordText);
                    }

                    // 通知适配器数据已更改
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
