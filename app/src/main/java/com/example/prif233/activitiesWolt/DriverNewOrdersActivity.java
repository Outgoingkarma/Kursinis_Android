package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.DRIVER_ACCEPT_ORDER;
import static com.example.prif233.Utils.Constants.GET_NEW_ORDERS_LITE;
import static com.example.prif233.Utils.Constants.UPDATE_ORDER_STATUS;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prif233.R;
import com.example.prif233.Utils.DriverOrdersAdapter;
import com.example.prif233.Utils.RestOperations;
import com.example.prif233.DTO.DriverOrderDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriverNewOrdersActivity extends AppCompatActivity {

    private List<DriverOrderDto> orders;
    private DriverOrdersAdapter adapter;
    private DriverOrderDto selected;
    private int driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        driverId = getIntent().getIntExtra("driverId", -1);
        if (driverId <= 0) {
            Toast.makeText(this, "Driver ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_driver_new_orders);

        ListView lv = findViewById(R.id.newOrdersList);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            if (orders == null || orders.isEmpty()) return;
            selected = orders.get(position);
            if (adapter != null) adapter.setSelectedPos(position);
        });

        findViewById(R.id.btnAccept).setOnClickListener(v -> acceptSelected());
        findViewById(R.id.btnFinish).setVisibility(View.GONE); // finish happens in WoltRestaurants now


        loadNewOrders();
    }

    private void loadNewOrders() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_NEW_ORDERS_LITE);

                handler.post(() -> {
                    if (response == null || response.startsWith("Error") || response.isEmpty()) {
                        Toast.makeText(this, "Failed to load NEW orders", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Type listType = new TypeToken<List<DriverOrderDto>>(){}.getType();
                    orders = new Gson().fromJson(response, listType);

                    adapter = new DriverOrdersAdapter(this, orders);
                    ((ListView)findViewById(R.id.newOrdersList)).setAdapter(adapter);
                });

            } catch (IOException e) {
                handler.post(() -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateSelected(String newStatus) {
        if (selected == null) {
            Toast.makeText(this, "Select an order first", Toast.LENGTH_SHORT).show();
            return;
        }

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String body = "{\"id\":" + selected.id + ",\"orderStatus\":\"" + newStatus + "\"}";

                String response = RestOperations.sendPut(UPDATE_ORDER_STATUS, body);

                handler.post(() -> {
                    if (response == null || response.startsWith("Error") || response.isEmpty()) {
                        Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(this, "Updated to " + newStatus, Toast.LENGTH_SHORT).show();
                    loadNewOrders(); // refresh list (IN_PROGRESS / FINISHED should disappear from NEW)
                    selected = null;
                });

            } catch (IOException e) {
                handler.post(() -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void acceptSelected() {
        if (selected == null) {
            Toast.makeText(this, "Select an order first", Toast.LENGTH_SHORT).show();
            return;
        }

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String body = "{\"orderId\":" + selected.id + ",\"driverId\":" + driverId + "}";
                String response = RestOperations.sendPut(DRIVER_ACCEPT_ORDER, body);

                handler.post(() -> {
                    if (response == null || response.startsWith("Error") || response.isEmpty()) {
                        Toast.makeText(this, "Failed to accept order", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(this, "Order accepted", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (IOException e) {
                handler.post(() -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });
    }

}