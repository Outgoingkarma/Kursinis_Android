package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.GET_ORDERS_BY_USER;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prif233.R;
import com.example.prif233.Utils.RestOperations;
import com.example.prif233.model.FoodOrder;
import com.example.prif233.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyOrders extends AppCompatActivity {

    private int userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_orders);
        View mainVIew = findViewById(R.id.main);
        if(mainVIew != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainVIew, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }


        //Noriu uzkrauti orderius konkreciam klientui

        Intent intent = getIntent();
        userId = intent.getIntExtra("id", 0);
        String userJson = intent.getStringExtra("userJson");

        if (userJson != null) {
            try {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                currentUser = gson.fromJson(userJson, User.class);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_ORDERS_BY_USER + userId);
                System.out.println(response);
                handler.post(() -> {
                    try {
                        if (response != null && !response.startsWith("Error") && !response.isEmpty()) {
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();
                            Type ordersListType = new TypeToken<List<FoodOrder>>() {
                            }.getType();
                            List<FoodOrder> ordersListFromJson = gson.fromJson(response, ordersListType);

                            if (ordersListFromJson != null && !ordersListFromJson.isEmpty()) {
                                ListView ordersListElement = findViewById(R.id.myOrderList);
                                MyOrdersAdapter adapter = new MyOrdersAdapter(this, ordersListFromJson);
                                ordersListElement.setAdapter(adapter);

                                ordersListElement.setOnItemClickListener((parent, view, position, id) -> {
                                    FoodOrder selectedOrder = ordersListFromJson.get(position);
                                    Intent orderDetailsIntent = new Intent(MyOrders.this, ChatSystem.class);
                                    orderDetailsIntent.putExtra("orderId", selectedOrder.getId());
                                    orderDetailsIntent.putExtra("userId", userId);
                                    if(currentUser != null){
                                        try {
                                            GsonBuilder userGsonBuilder = new GsonBuilder();
                                            Gson userGson = userGsonBuilder.create();
                                            String userJsonString = userGson.toJson(currentUser);
                                            orderDetailsIntent.putExtra("userJson", userJsonString);
                                        }catch (Exception e){
                                            e.printStackTrace();

                                        }
                                    }
                                    startActivity(orderDetailsIntent);
                                });

                            }else{
                                android.widget.Toast.makeText(MyOrders.this, "No orders found", android.widget.Toast.LENGTH_SHORT).show();
                            }

                        }
                        else {
                            String errorMessage = (response == null || response.isEmpty()) ? "Failed to load orders " : response;
                            android.widget.Toast.makeText(MyOrders.this, errorMessage, android.widget.Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        android.widget.Toast.makeText(MyOrders.this, "Error loading orders: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(()->{
                    e.printStackTrace();
                    android.widget.Toast.makeText(MyOrders.this, "Error loading orders", android.widget.Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}