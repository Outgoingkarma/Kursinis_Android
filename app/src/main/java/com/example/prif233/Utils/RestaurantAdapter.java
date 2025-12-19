package com.example.prif233.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prif233.R;
import com.example.prif233.model.Restaurant;

import java.util.List;

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

    public RestaurantAdapter(@NonNull Context context, @NonNull List<Restaurant> restaurants) {
        super(context, 0, restaurants);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_restaurant, parent, false);
        }

        Restaurant restaurant = getItem(position);
        if (restaurant == null) return convertView;

        TextView nameTextView = convertView.findViewById(R.id.restaurantName);
        TextView addressTextView = convertView.findViewById(R.id.restaurantAddress);
        TextView phoneTextView = convertView.findViewById(R.id.restaurantPhone);


        String displayName = safe(restaurant.getRestaurantName());
        if (displayName.isEmpty()) {
            String n = safe(restaurant.getName());
            String s = safe(restaurant.getSurname());
            displayName = (n + " " + s).trim();
        }
        if (displayName.isEmpty()) displayName = safe(restaurant.getLogin());
        if (displayName.isEmpty()) displayName = "Restaurant";
        nameTextView.setText(displayName);


        String addr = safe(restaurant.getAddress());
        if (addr.isEmpty()) addr = safe(restaurant.getAddress());
        addressTextView.setText(addr.isEmpty() ? "📍 Address not available" : "📍 " + addr);


        String phone = safe(restaurant.getPhone_number());
        if (phone.isEmpty()) phone = safe(restaurant.getPhone_number());
        phoneTextView.setText(phone.isEmpty() ? "📞 Phone not available" : "📞 " + phone);

        return convertView;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}

