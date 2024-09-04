package com.example.secondplatform;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PurchaseRecordAdapter extends RecyclerView.Adapter<PurchaseRecordAdapter.ViewHolder> {

    private JSONArray records;

    public PurchaseRecordAdapter(JSONArray records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject record = records.getJSONObject(position);
            String imageUrl = record.getJSONArray("imageUrlList").optString(0, "");
            String productName = record.getString("goodsDescription");
            int price = record.getInt("price");
            String productAddress = "地址: " + record.optString("goodsAddress", "无地址");

            holder.tvProductName.setText(productName);
            holder.tvProductPrice.setText("价格: " + price);
            holder.tvProductDescription.setText("描述: " + productName);
            holder.tvProductAddress.setText(productAddress);

            if (!imageUrl.isEmpty()) {
                Picasso.get().load(imageUrl).into(holder.ivProductImage);
            } else {
             //   holder.ivProductImage.setImageResource(R.drawable.placeholder_image); // 默认图片
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return records.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductDescription, tvProductAddress;
        ImageView ivProductImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
            tvProductAddress = itemView.findViewById(R.id.tvProductAddress);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }
    }
}
