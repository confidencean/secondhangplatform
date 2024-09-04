package com.example.secondplatform;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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
        Button btnBuyrecord=findViewById(R.id.Buyrecord);
        btnEditUserInfo.setOnClickListener(v -> {
            // 跳转到修改用户信息页面，传递 userId
            Intent intent = new Intent(MyselfActivity.this, EditUserInfoActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnRecharge.setOnClickListener(v -> {
            // 跳转到充值页面
            Intent intent = new Intent(MyselfActivity.this, RechargeActivity.class);
            // No need to pass money if not used in RechargeActivity
            startActivity(intent);
        });

        btnBuyrecord.setOnClickListener(view -> {
            Intent intent = new Intent(MyselfActivity.this,BuyRecordActivity.class);
            startActivity(intent);

        });
    }
}
