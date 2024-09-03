package com.example.secondplatform;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvProductName, tvProductPrice, tvProductDescription, tvProductAddress;
    private ImageView ivProductImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductAddress = findViewById(R.id.tvProductAddress);
        ivProductImage = findViewById(R.id.ivProductImage);

        // 获取从 ProductListActivity 传递过来的商品 goodsId
        int goodsId = getIntent().getIntExtra("goodsId", -1);

        if (goodsId != -1) {
            new FetchProductDetailTask().execute(goodsId);
        } else {
            Toast.makeText(this, "Invalid Product ID", Toast.LENGTH_LONG).show();
        }
    }

    private class FetchProductDetailTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            int goodsId = params[0];
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
                    JSONObject jsonObject = jsonResponse.getJSONObject("data");
                    String productName = jsonObject.getString("typeName");
                    String productDescription = jsonObject.getString("content");
                    String productAddress = jsonObject.getString("addr");
                    String imageUrl = jsonObject.getJSONArray("imageUrlList").getString(0);
                    int price =jsonObject.getInt("price");
                    tvProductName.setText(productName);
                    tvProductDescription.setText("描述: " + productDescription);
                    tvProductAddress.setText("地址: " + productAddress);
                    tvProductPrice.setText("价格"+price);
                    // 使用 Picasso 加载图片
                    Picasso.get().load(imageUrl).into(ivProductImage);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "获取商品详情失败: " + result, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ProductDetailActivity.this, "解析错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
