package com.example.secondplatform;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ViewPublishedProductsActivity extends AppCompatActivity {

    private ListView listView;
    private ProductAdapter productAdapter;
    private List<SelfProduct> productList;
    private Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_published_products);

        listView = findViewById(R.id.listView);
        btnRefresh = findViewById(R.id.btnRefresh);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);

        listView.setAdapter(productAdapter);

        // 读取 userId
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            new FetchPublishedProductsTask().execute(userId);
        } else {
            Toast.makeText(this, "User ID 不存在，请重新登录", Toast.LENGTH_LONG).show();
        }

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != null) {
                    new FetchPublishedProductsTask().execute(userId);
                } else {
                    Toast.makeText(ViewPublishedProductsActivity.this, "User ID 不存在，请重新登录", Toast.LENGTH_LONG).show();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelfProduct product = productList.get(position);
                Intent intent = new Intent(ViewPublishedProductsActivity.this, ProductDetailActivity.class);
                intent.putExtra("productId", product.getId());
                startActivity(intent);
            }
        });
    }

    private class FetchPublishedProductsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String userId = params[0];
                URL url = new URL("https://api-store.openguet.cn/api/member/tran/goods/myself?userId=" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("appId", "1db7ee6c102c40e892e8e75d2b8b40b7");
                conn.setRequestProperty("appSecret", "27868ea89849efca5477b8d32b055187b2068");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "错误：" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray jsonArray = jsonResponse.getJSONObject("data").getJSONArray("records");

                productList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String content = jsonObject.getString("content");
                    String price = jsonObject.getString("price");
                    String addr = jsonObject.getString("addr");
                    String typeId = jsonObject.getString("typeId");
                    String typeName = jsonObject.getString("typeName");

                    SelfProduct product = new SelfProduct(id, content, price, addr, typeId, typeName);
                    productList.add(product);
                }

                productAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ViewPublishedProductsActivity.this, "数据解析错误", Toast.LENGTH_LONG).show();
            }
        }
    }
}
