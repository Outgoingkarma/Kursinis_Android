package com.example.prif233.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.prif233.model.OrderItem;

import java.util.List;

public class OrderItemsAdapter extends BaseAdapter {
    private Context context;
    private List<OrderItem> items;

    public OrderItemsAdapter(Context context, List<OrderItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return items != null ? items.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        OrderItem item = items.get(position);
        if (item != null) {
            text1.setText(item.getName());
            if (item.getQuantity() > 1) {
                text2.setText(String.format("Quantity: %d × €%.2f = €%.2f", 
                    item.getQuantity(), item.getPrice(), item.getQuantity() * item.getPrice()));
            } else {
                text2.setText(String.format("€%.2f", item.getPrice()));
            }
        }

        return convertView;
    }
}

