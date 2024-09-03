package com.example.secondplatform;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.Registerusername);
        etPassword = findViewById(R.id.Registerpassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validateInput(username, password, confirmPassword)) {
                new RegisterTask().execute(username, password);
            }
        });

        tvLogin.setOnClickListener(v -> {
            finish();  // 关闭当前注册页面并返回到登录页面
        });
    }

    private boolean validateInput(String username, String password, String confirmPassword) {
        if (username.isEmpty()) {
            etUsername.setError("请输入账号");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("请输入密码");
            return false;
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("请确认密码");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("两次输入的密码不一致");
            return false;
        }
        return true;
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                URL url = new URL("https://api-store.openguet.cn/api/member/tran/user/register");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("appId", "1db7ee6c102c40e892e8e75d2b8b40b7");
                conn.setRequestProperty("appSecret", "27868ea89849efca5477b8d32b055187b2068");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("username", username);
                    jsonParam.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
            // 处理服务器返回的结果
            Toast.makeText(RegisterActivity.this, "服务器响应: " + result, Toast.LENGTH_LONG).show();
            // 根据服务器响应进一步处理，例如解析JSON
            if (result.contains("注册成功")) { // 假设返回中包含“注册成功”表示注册成功
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // 关闭注册页面
            } else {
                // 根据具体的错误信息进行处理
                Toast.makeText(RegisterActivity.this, "注册失败: " + result, Toast.LENGTH_LONG).show();
            }
        }
    }
}
