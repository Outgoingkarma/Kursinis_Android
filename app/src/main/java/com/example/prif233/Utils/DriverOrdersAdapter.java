package com.example.prif233.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.prif233.R;
import com.example.prif233.DTO.DriverOrderDto;

import java.util.List;

public class DriverOrdersAdapter extends ArrayAdapter<DriverOrderDto> {
    private int selectedPos = -1;

    public DriverOrdersAdapter(Context ctx, List<DriverOrderDto> data) {
        super(ctx, 0, data);
    }

    public void setSelectedPos(int pos) {
        selectedPos = pos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_driver_order, parent, false);
        }

        DriverOrderDto o = getItem(position);

        TextView tvRestaurant = v.findViewById(R.id.tvRestaurant);
        TextView tvOrderId = v.findViewById(R.id.tvOrderId);
        TextView tvAddress = v.findViewById(R.id.tvAddress);
        TextView tvPrice = v.findViewById(R.id.tvPrice);

        if (o != null) {
            tvRestaurant.setText("Restaurant: " + (o.restaurantName != null ? o.restaurantName : "N/A"));
            tvOrderId.setText("Order #" + o.id + " (" + o.orderStatus + ")");
            tvAddress.setText("Address: " + (o.deliveryAddress != null ? o.deliveryAddress : "N/A"));
            tvPrice.setText(String.format("€%.2f", o.orderPrice));
        }

        // highlight selected row
        v.setAlpha(position == selectedPos ? 0.6f : 1.0f);

        return v;
    }
}
