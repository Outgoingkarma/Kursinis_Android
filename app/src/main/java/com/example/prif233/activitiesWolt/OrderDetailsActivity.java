package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.GET_ORDER_BY_ID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prif233.R;
import com.example.prif233.Utils.OrderItemsAdapter;
import com.example.prif233.Utils.RestOperations;
import com.example.prif233.model.OrderItem;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Shows details for a single order and lets the user open the chat for that order.
 */
public class OrderDetailsActivity extends AppCompatActivity {

    private int orderId;
    private int userId;
    private String userJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_details);
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        Intent intent = getIntent();
        orderId = intent.getIntExtra("orderId", 0);
        userId = intent.getIntExtra("userId", 0);
        userJson = intent.getStringExtra("userJson");

        // Fetch full order details from backend
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_ORDER_BY_ID + orderId);
                handler.post(() -> {
                    try {
                        if (response != null && !response.startsWith("Error") && !response.isEmpty()) {
                            parseAndDisplayOrder(response);
                        } else {
                            // Fallback to basic info from intent if API fails
                            displayBasicOrderInfo();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        displayBasicOrderInfo();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    e.printStackTrace();
                    displayBasicOrderInfo();
                });
            }
        });
    }

    private void parseAndDisplayOrder(String jsonResponse) {
        Gson gson = new Gson();
        JsonObject orderJson = gson.fromJson(jsonResponse, JsonObject.class);

        TextView orderIdView = findViewById(R.id.orderDetailsId);
        TextView orderStatusView = findViewById(R.id.orderDetailsStatus);
        TextView orderPriceView = findViewById(R.id.orderDetailsPrice);
        TextView orderRestaurantView = findViewById(R.id.orderDetailsRestaurant);
        TextView orderCustomerView = findViewById(R.id.orderDetailsCustomer);
        ListView orderItemsList = findViewById(R.id.orderItemsList);

        // Order ID
        if (orderJson.has("id")) {
            orderIdView.setText("Order ID: #" + orderJson.get("id").getAsInt());
        }

        // Status
        String status = orderJson.has("orderStatus") ? orderJson.get("orderStatus").getAsString() : "Unknown";
        orderStatusView.setText("Status: " + status);

        // Total Price
        if (orderJson.has("orderPrice")) {
            double price = orderJson.get("orderPrice").getAsDouble();
            orderPriceView.setText(String.format("Total: €%.2f", price));
        }

        // Restaurant
        String restaurantName = orderJson.has("restaurantName") 
            ? orderJson.get("restaurantName").getAsString() 
            : "N/A";
        orderRestaurantView.setText("Restaurant: " + restaurantName);

        // Customer
        String customerName = orderJson.has("customerName") 
            ? orderJson.get("customerName").getAsString() 
            : "N/A";
        orderCustomerView.setText("Customer: " + customerName);

        // Order Items
        if (orderJson.has("items") && orderJson.get("items").isJsonArray()) {
            JsonArray itemsArray = orderJson.getAsJsonArray("items");
            List<OrderItem> orderItems = parseOrderItems(itemsArray);
            
            if (!orderItems.isEmpty()) {
                OrderItemsAdapter adapter = new OrderItemsAdapter(this, orderItems);
                orderItemsList.setAdapter(adapter);
            }
        }
    }

    private List<OrderItem> parseOrderItems(JsonArray itemsArray) {
        List<OrderItem> orderItems = new ArrayList<>();
        Map<Integer, OrderItem> itemMap = new HashMap<>();

        // Count quantities for each dish
        for (JsonElement element : itemsArray) {
            JsonObject itemObj = element.getAsJsonObject();
            int id = itemObj.get("id").getAsInt();
            String name = itemObj.has("name") ? itemObj.get("name").getAsString() : "Unknown";
            double price = itemObj.has("price") ? itemObj.get("price").getAsDouble() : 0.0;

            if (itemMap.containsKey(id)) {
                // Increment quantity
                OrderItem existing = itemMap.get(id);
                existing.setQuantity(existing.getQuantity() + 1);
            } else {
                // Create new item
                OrderItem item = new OrderItem(id, name, price);
                itemMap.put(id, item);
            }
        }

        orderItems.addAll(itemMap.values());
        return orderItems;
    }

    private void displayBasicOrderInfo() {
        // Fallback to basic info from intent
        Intent intent = getIntent();
        double orderPrice = intent.getDoubleExtra("orderPrice", 0.0);
        String orderStatus = intent.getStringExtra("orderStatus");
        boolean isDelivered = intent.getBooleanExtra("isDelivered", false);

        TextView orderIdView = findViewById(R.id.orderDetailsId);
        TextView orderStatusView = findViewById(R.id.orderDetailsStatus);
        TextView orderPriceView = findViewById(R.id.orderDetailsPrice);

        orderIdView.setText("Order ID: #" + orderId);
        orderStatusView.setText("Status: " + (orderStatus != null ? orderStatus : "Unknown"));
        orderPriceView.setText(String.format("Total: €%.2f", orderPrice));
    }

    /**
     * Called from the "Open Chat" button in the layout.
     */
    public void openChat(View view) {
        Intent chatIntent = new Intent(OrderDetailsActivity.this, ChatSystem.class);
        chatIntent.putExtra("orderId", orderId);
        chatIntent.putExtra("userId", userId);
        if (userJson != null) {
            chatIntent.putExtra("userJson", userJson);
        }
        startActivity(chatIntent);
    }
}
