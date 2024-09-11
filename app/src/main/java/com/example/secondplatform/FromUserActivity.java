package com.example.secondplatform;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FromUserActivity extends AppCompatActivity {

    private ListView listView;
    private FromUserAdapter adapter;
    private List<FromUserMessage> receivedMessageList;
    private String userId;
    private int currentPage = 1; // 当前分页页码
    private boolean isLoading = false; // 是否正在加载

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fromuser_list);

        listView = findViewById(R.id.listView);
        receivedMessageList = new ArrayList<>();
        adapter = new FromUserAdapter(this, receivedMessageList);
        listView.setAdapter(adapter);

        // 从 SharedPreferences 获取 userId
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            // 加载第一页数据
            loadReceivedMessages(currentPage);
        } else {
            Toast.makeText(this, "UserId 不存在", Toast.LENGTH_LONG).show();
        }
    }

    private void loadReceivedMessages(int page) {
        if (isLoading) return; // 防止多次加载
        isLoading = true;

        new GetReceivedMessagesTask().execute(page);
    }

    private class GetReceivedMessagesTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            int page = params[0];
            try {
                String apiUrl = "https://api-store.openguet.cn/api/member/tran/chat/user?userId=" + userId + "&page=" + page;
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("appId", "1db7ee6c102c40e892e8e75d2b8b40b7");
                conn.setRequestProperty("appSecret", "27868ea89849efca5477b8d32b055187b2068");

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
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
            isLoading = false; // 加载完成后重置加载状态

            try {
                JSONObject jsonResponse = new JSONObject(result);
                int code = jsonResponse.getInt("code");
                if (code == 200) {
                    JSONArray data = jsonResponse.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject messageObj = data.getJSONObject(i);
                        String fromUserId = messageObj.getString("fromUserId");
                        String username = messageObj.getString("username");
                        int unReadNum = messageObj.getInt("unReadNum");

                        // 保存 fromUserId 到 SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("FromUserIdPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor from = sharedPreferences.edit();
                        Set<String> fromUserIds = sharedPreferences.getStringSet("fromUserIds", new HashSet<>());
                        fromUserIds.add(fromUserId);
                        from.putStringSet("fromUserIds", fromUserIds);
                        from.apply();

                        // 添加到消息列表
                        receivedMessageList.add(new FromUserMessage(fromUserId, username, unReadNum));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FromUserActivity.this, "加载失败: " + jsonResponse.getString("msg"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(FromUserActivity.this, "解析错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
