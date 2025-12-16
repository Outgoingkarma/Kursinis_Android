package com.example.prif233.activitiesWolt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prif233.R;

/**
 * Shows details for a single order and lets the user open the chat for that order.
 */
public class OrderDetailsActivity extends AppCompatActivity {

    private int orderId;
    private int userId;
    private double orderPrice;
    private String orderStatus;
    private boolean isDelivered;
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
        orderPrice = intent.getDoubleExtra("orderPrice", 0.0);
        orderStatus = intent.getStringExtra("orderStatus");
        isDelivered = intent.getBooleanExtra("isDelivered", false);
        userJson = intent.getStringExtra("userJson");

        TextView orderIdView = findViewById(R.id.orderDetailsId);
        TextView orderPriceView = findViewById(R.id.orderDetailsPrice);
        TextView orderStatusView = findViewById(R.id.orderDetailsStatus);
        TextView orderDeliveredView = findViewById(R.id.orderDetailsDelivered);

        orderIdView.setText("Order #" + orderId);
        orderPriceView.setText(String.format("Price: €%.2f", orderPrice));
        orderStatusView.setText("Status: " + (orderStatus != null ? orderStatus : "Unknown"));
        orderDeliveredView.setText("Delivered: " + (isDelivered ? "Yes" : "No"));
    }

    /**
     * Called from the "Open chat" button in the layout.
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


