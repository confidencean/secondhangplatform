package com.example.secondplatform;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private EditText etContent, etPrice, etAddr, etImageCode;
    private Spinner spinnerType;
    private Button btnAddProduct;
    private Button saveProduct; // New save button
    private String userId; // Stores the userId retrieved from SharedPreferences

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Retrieve userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        if (userId == null) {
            Toast.makeText(this, "未登录，请先登录", Toast.LENGTH_SHORT).show();
            finish(); // End the activity if user is not logged in
            return;
        }

        // Initialize view components
        etContent = findViewById(R.id.etContent);
        etPrice = findViewById(R.id.etPrice);
        etAddr = findViewById(R.id.etAddr);
        etImageCode = findViewById(R.id.etImageCode);
        spinnerType = findViewById(R.id.spinnerType);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        saveProduct = findViewById(R.id.SaveProduct); // New save button initialization

        // Setup product type dropdown
        setupProductTypeSpinner();

        // Add product button click listener
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });

        // Save product button click listener
        saveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });
    }

    private void setupProductTypeSpinner() {
        // Define product types with corresponding IDs
        Map<Integer, String> productTypes = new HashMap<>();
        productTypes.put(1, "手机");
        productTypes.put(2, "奢品");
        productTypes.put(3, "潮品");
        productTypes.put(4, "美妆");
        productTypes.put(5, "数码");
        productTypes.put(6, "潮玩");
        productTypes.put(7, "游戏");
        productTypes.put(8, "图书");
        productTypes.put(9, "美食");
        productTypes.put(10, "文玩");
        productTypes.put(11, "母婴");
        productTypes.put(12, "家居");
        productTypes.put(13, "乐器");
        productTypes.put(14, "其他");

        // Set product types in the dropdown spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, productTypes.values().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    // Add product function (original)
    private void addProduct() {
        // Gather inputs
        String content = etContent.getText().toString();
        String price = etPrice.getText().toString();
        String addr = etAddr.getText().toString();
        String imageCode = etImageCode.getText().toString();
        int typeId = spinnerType.getSelectedItemPosition() + 1; // Product type ID
        String typeName = (String) spinnerType.getSelectedItem();

        // Check for empty inputs
        if (content.isEmpty() || price.isEmpty() || addr.isEmpty() || imageCode.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create product data map
        Map<String, String> productData = new HashMap<>();
        productData.put("content", content);
        productData.put("price", price);
        productData.put("addr", addr);
        productData.put("imageCode", imageCode);
        productData.put("typeId", String.valueOf(typeId));
        productData.put("typeName", typeName);
        productData.put("userId", userId);

        // Execute AddProductTask
        new AddProductTask().execute(productData);
    }
    private class AddProductTask extends AsyncTask<Map<String, String>, Void, String> {

        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> productData = params[0];
            try {
                // API URL
                URL url = new URL("https://api-store.openguet.cn/api/member/tran/goods/add");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("appId", "1db7ee6c102c40e892e8e75d2b8b40b7");
                conn.setRequestProperty("appSecret", "27868ea89849efca5477b8d32b055187b2068");
                conn.setDoOutput(true);

                // 创建 JSON 参数
                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("content", productData.get("content"));
                    jsonParam.put("price", productData.get("price"));
                    jsonParam.put("addr", productData.get("addr"));
                    jsonParam.put("imageCode", productData.get("imageCode"));
                    jsonParam.put("typeId", productData.get("typeId"));
                    jsonParam.put("typeName", productData.get("typeName"));
                    jsonParam.put("userId", productData.get("userId"));
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // 处理服务器的响应
            if (result.startsWith("Error") || result.startsWith("Exception")) {
                Toast.makeText(AddProductActivity.this, "添加商品失败: " + result, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddProductActivity.this, "商品添加成功！", Toast.LENGTH_SHORT).show();
                // 可以在这里进行其他操作，如清空输入字段
            }
        }

    }



    // Save product function (new)
    private void saveProduct() {
        // Gather inputs (same as addProduct)
        String content = etContent.getText().toString();
        String price = etPrice.getText().toString();
        String addr = etAddr.getText().toString();
        String imageCode = etImageCode.getText().toString();
        int typeId = spinnerType.getSelectedItemPosition() + 1; // Product type ID
        String typeName = (String) spinnerType.getSelectedItem();

        // Check for empty inputs
        if (content.isEmpty() || price.isEmpty() || addr.isEmpty() || imageCode.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create product data map
        Map<String, String> productData = new HashMap<>();
        productData.put("content", content);
        productData.put("price", price);
        productData.put("addr", addr);
        productData.put("imageCode", imageCode);
        productData.put("typeId", String.valueOf(typeId));
        productData.put("typeName", typeName);
        productData.put("userId", userId);

        // Execute SaveProductTask
        new SaveProductTask().execute(productData);
    }

    // AsyncTask for saving product data
    private class SaveProductTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> productData = params[0];
            try {
                // API URL for saving product
                URL url = new URL("https://api-store.openguet.cn/api/member/tran/goods/save");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("appId", "1db7ee6c102c40e892e8e75d2b8b40b7");
                conn.setRequestProperty("appSecret", "27868ea89849efca5477b8d32b055187b2068");
                conn.setDoOutput(true);

                // Create JSON parameters
                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("content", productData.get("content"));
                    jsonParam.put("price", productData.get("price"));
                    jsonParam.put("addr", productData.get("addr"));
                    jsonParam.put("imageCode", productData.get("imageCode"));
                    jsonParam.put("typeId", productData.get("typeId"));
                    jsonParam.put("typeName", productData.get("typeName"));
                    jsonParam.put("userId", productData.get("userId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Send request
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
            super.onPostExecute(result);
            // Handle server response
            if (result.startsWith("Error") || result.startsWith("Exception")) {
                Toast.makeText(AddProductActivity.this, "保存商品失败: " + result, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddProductActivity.this, "商品保存成功！", Toast.LENGTH_SHORT).show();
                // You can clear input fields or perform other actions here
            }
        }
    }
}
