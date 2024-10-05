package com.example.secondplatform;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProductActivity extends AppCompatActivity {

    private Button btnAddProduct, btnDeleteProduct, btnViewPublishedProducts,   btnSaveProducts;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);
        btnViewPublishedProducts = findViewById(R.id.btnViewPublishedProducts);
        btnSaveProducts = findViewById(R.id.btnSaveProducts);
        // 新增商品按钮点击事件
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到新增商品页面
                Intent intent = new Intent(ProductActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        // 更新商品状态按钮点击事件


        // 删除商品按钮点击事件
        btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到删除商品页面
               Intent intent = new Intent(ProductActivity.this, DeleteProductActivity.class);
               startActivity(intent);
            }
        });

        // 查看已发布商品按钮点击事件
        btnViewPublishedProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到查看已发布商品页面
               Intent intent = new Intent(ProductActivity.this, ViewPublishedProductsActivity.class);
                startActivity(intent);
            }
        });

        btnSaveProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(ProductActivity.this,SaveProductActivity.class);
                        startActivity(intent);
            }
        });
    }
}
