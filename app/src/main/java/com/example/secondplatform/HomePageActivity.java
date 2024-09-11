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
import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {
    private Button  mybutton;
    private  Button transaction;
    private  Button mysell;
    private  Button message;
    private ListView listViewCategories;
    private TextView tvCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mybutton =findViewById(R.id.btnMyProfile);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "未登录");
        int money = sharedPreferences.getInt("money", 0);
        String avatar = ((SharedPreferences) sharedPreferences).getString("avatar", "null");
        mybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, MyselfActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("money", money);
                intent.putExtra("avatar", avatar);
                startActivity(intent);
            }
        });
        transaction=findViewById(R.id.btnTransactionRecord);
        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, TransactionRecordActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    mysell=findViewById(R.id.btnSell);
    mysell.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    });

    message=findViewById(R.id.btnMessages);
    message.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(HomePageActivity.this, MessageActivity.class);
            startActivity(intent);
        }
    });





        listViewCategories = findViewById(R.id.listViewProducts);
        tvCategories = findViewById(R.id.tvCategories);

        // Load product categories and set click listener
        new FetchProductCategoriesTask().execute();

        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = (String) parent.getItemAtPosition(position);

                Intent intent = new Intent(HomePageActivity.this, ProductListActivity.class);
                intent.putExtra("typeName", selectedCategory);  // 正确传递 typeName
                startActivity(intent);
            }
        });

    }

    private class FetchProductCategoriesTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://api-store.openguet.cn/api/member/tran/goods/type");
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
                    JSONArray jsonArray = jsonResponse.getJSONArray("data");
                    ArrayList<String> categoryList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String typeName = jsonObject.getString("type");
                        categoryList.add(typeName);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(HomePageActivity.this, android.R.layout.simple_list_item_1, categoryList);
                    listViewCategories.setAdapter(adapter);
                } else {
                    Toast.makeText(HomePageActivity.this, "获取分类失败: " + result, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(HomePageActivity.this, "解析错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


}
