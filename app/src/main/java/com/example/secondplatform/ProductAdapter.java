package com.example.secondplatform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<SelfProduct> {

    public ProductAdapter(Context context, List<SelfProduct> products) {
        super(context, 0, products);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取数据项
        SelfProduct product = getItem(position);

        // 检查是否需要创建新的视图
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
        }

        // 获取视图中的组件
        TextView tvContent = convertView.findViewById(R.id.tvContent);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);
        TextView tvAddr = convertView.findViewById(R.id.tvAddr);
        TextView tvTypeName = convertView.findViewById(R.id.tvTypeName);

        // 设置数据
        tvContent.setText(product.getContent());
        tvPrice.setText("价格: " + product.getPrice());
        tvAddr.setText("地址: " + product.getAddr());
        tvTypeName.setText("类型: " + product.getTypeName());

        return convertView;
    }
}
