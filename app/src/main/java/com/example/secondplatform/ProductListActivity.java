package com.example.secondplatform;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class ProductListActivity extends AppCompatActivity {

    private ListView listViewProducts;
    private String typeName;
    private ArrayList<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        listViewProducts = findViewById(R.id.listViewProducts);

        // Get typeName from intent
        Intent intent = getIntent();
        typeName = intent.getStringExtra("typeName");

        if (typeName != null) {
            new FetchProductsTask().execute();
        }
    }

    private class FetchProductsTask extends AsyncTask<Void, Void, ArrayList<Product>> {
        @Override
        protected ArrayList<Product> doInBackground(Void... voids) {
            ArrayList<Product> productList = new ArrayList<>();
            for (int goodsId = 410; goodsId <= 1156; goodsId++) {
                try {
                    URL url = new URL("https://api-store.openguet.cn/api/member/tran/goods/details?goodsId=" + goodsId);
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

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        int code = jsonResponse.getInt("code");
                        if (code == 200) {
                            JSONObject jsonData = jsonResponse.getJSONObject("data");
                            String productTypeName = jsonData.getString("typeName");
                            if (productTypeName.equals(typeName)) {
                                int id = jsonData.getInt("id");
                                String content = jsonData.getString("content");
                                String imageUrl = jsonData.getJSONArray("imageUrlList").getString(0);

                                Product product = new Product(id, content, imageUrl);
                                productList.add(product);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return productList;
        }

        @Override
        protected void onPostExecute(ArrayList<Product> productList) {
            if (!productList.isEmpty()) {
                ProductListActivity.this.productList = productList;
                ArrayAdapter<Product> adapter = new ArrayAdapter<>(ProductListActivity.this, android.R.layout.simple_list_item_1, productList);
                listViewProducts.setAdapter(adapter);

                listViewProducts.setOnItemClickListener((parent, view, position, id) -> {
                    Product selectedProduct = productList.get(position);
                    Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                    intent.putExtra("goodsId", selectedProduct.getId()); // 传递 goodsId
                    startActivity(intent);
                });
            } else {
                Toast.makeText(ProductListActivity.this, "No products found for this category.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
