package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.DRIVER_FINISH_ORDER;
import static com.example.prif233.Utils.Constants.GET_ALL_RESTAURANTS_URL;
import static com.example.prif233.Utils.Constants.GET_DRIVER_IN_PROGRESS_LITE;
import static com.example.prif233.Utils.Constants.GET_USER_BY_ID;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prif233.DTO.DriverOrderDto;
import com.example.prif233.R;
import com.example.prif233.Utils.DriverOrdersAdapter;
import com.example.prif233.Utils.LocalDateAdapter;
import com.example.prif233.Utils.LocalDateTimeAdapter;
import com.example.prif233.Utils.RestOperations;
import com.example.prif233.Utils.RestaurantAdapter;
import com.example.prif233.model.BasicUser;
import com.example.prif233.model.Driver;
import com.example.prif233.model.Restaurant;
import com.example.prif233.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WoltRestaurants extends AppCompatActivity {

    private List<DriverOrderDto> inProgressOrders;
    private DriverOrdersAdapter inProgressAdapter;
    private DriverOrderDto selectedInProgress;
    private int driverId;

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wolt_restaurants);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Priejimas prie duomenu is praeitos Activity

        Intent intent = getIntent();
        String userInfo = intent.getStringExtra("userJsonObject");

        if (userInfo != null && isRestaurantJson(userInfo)) {
            goToLogin();
            return;
        }

        GsonBuilder build = new GsonBuilder();
        build.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        Gson gson = build.setPrettyPrinting().create();
        currentUser = gson.fromJson(userInfo, User.class);
        int userId = currentUser.getId();

        Button btnNewOrders = findViewById(R.id.btnNewOrders);
        if (btnNewOrders != null) btnNewOrders.setVisibility(View.GONE);

        ListView lvDriver = findViewById(R.id.driverInProgressList);
        if (lvDriver != null) lvDriver.setVisibility(View.GONE);

        Button btnFinish = findViewById(R.id.btnFinishInProgress);
        if (btnFinish != null) btnFinish.setVisibility(View.GONE);

        ListView lvRestaurants = findViewById(R.id.restaurantList);
        if (lvRestaurants != null) lvRestaurants.setVisibility(View.GONE);

        if (currentUser instanceof Restaurant) {
            finish();
            return;
        }

        setupDriverButtonFromServer(userId);





    }

    public void viewPurchaseHistory(View view) {

        if(currentUser != null)
        try{
            GsonBuilder gsonBuilder = new GsonBuilder();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
            }
            Gson gson = gsonBuilder.create();
            String userJson = gson.toJson(currentUser);
            Intent intent = new Intent(WoltRestaurants.this, MyOrders.class);
            intent.putExtra("id", currentUser.getId());
            intent.putExtra("userJson", userJson);
            startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
            Intent intent = new Intent(WoltRestaurants.this, MyOrders.class);
            intent.putExtra("id", currentUser.getId());
            startActivity(intent);

        } else{
            Toast.makeText(this, "User information is not available", Toast.LENGTH_SHORT).show();
        }



    }

    public void viewMyAccount(View view) {
        if(currentUser != null)
            try{
                GsonBuilder gsonBuilder = new GsonBuilder();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
                }
                Gson gson = gsonBuilder.create();
                String userJson = gson.toJson(currentUser);
                Intent intent = new Intent(WoltRestaurants.this, EditAccountActivity.class);
                intent.putExtra("id", currentUser.getId());
                intent.putExtra("userJson", userJson);
                startActivity(intent);
            } catch (Exception e){
                e.printStackTrace();
                Intent intent = new Intent(WoltRestaurants.this, EditAccountActivity.class);
                intent.putExtra("id", currentUser.getId());
                startActivity(intent);

            } else{
            Toast.makeText(this, "User information is not available", Toast.LENGTH_SHORT).show();
        }

    }

    private void setupDriverButtonFromServer(int userId) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_USER_BY_ID + userId);
                android.util.Log.d("WoltRestaurants", "GET_USER_BY_ID resp=" + response);

                handler.post(() -> {
                    Button btnNewOrders = findViewById(R.id.btnNewOrders);
                    ListView lvRestaurants = findViewById(R.id.restaurantList);
                    ListView lvDriver = findViewById(R.id.driverInProgressList);
                    Button btnFinish = findViewById(R.id.btnFinishInProgress);

                    if (response == null || response.startsWith("Error") || response.isEmpty()) {
                        Toast.makeText(this, "Failed to verify user type", Toast.LENGTH_SHORT).show();
                        goToLogin();
                        return;
                    }

                    if (isRestaurantJson(response)) {
                        goToLogin();
                        return;
                    }

                    boolean isDriver = isDriverJson(response);
                    if (btnNewOrders != null) btnNewOrders.setVisibility(isDriver ? View.VISIBLE : View.GONE);

                    if (isDriver) {
                        driverId = userId;
                        if (lvRestaurants != null) lvRestaurants.setVisibility(View.GONE);
                        if (lvDriver != null) lvDriver.setVisibility(View.VISIBLE);
                        if (btnFinish != null) btnFinish.setVisibility(View.VISIBLE);

                        if (btnNewOrders != null) {
                            btnNewOrders.setOnClickListener(v -> {
                                Intent i = new Intent(WoltRestaurants.this, DriverNewOrdersActivity.class);
                                i.putExtra("driverId", driverId);
                                startActivity(i);
                            });
                        }

                        if (lvDriver != null) {
                            lvDriver.setOnItemClickListener((parent, view, position, id) -> {
                                if (inProgressOrders == null || inProgressOrders.isEmpty()) return;
                                selectedInProgress = inProgressOrders.get(position);
                                if (inProgressAdapter != null) inProgressAdapter.setSelectedPos(position);
                            });
                        }

                        if (btnFinish != null) btnFinish.setOnClickListener(v -> finishSelectedInProgress());

                        loadDriverInProgressOrders(driverId);
                    } else {
                        if (lvDriver != null) lvDriver.setVisibility(View.GONE);
                        if (btnFinish != null) btnFinish.setVisibility(View.GONE);
                        if (lvRestaurants != null) lvRestaurants.setVisibility(View.VISIBLE);
                        loadRestaurants();
                    }
                });

            } catch (IOException e) {
                handler.post(() -> {
                    Button btnNewOrders = findViewById(R.id.btnNewOrders);
                    if (btnNewOrders != null) btnNewOrders.setVisibility(View.GONE);

                    ListView lvRestaurants = findViewById(R.id.restaurantList);
                    if (lvRestaurants != null) lvRestaurants.setVisibility(View.VISIBLE);

                    loadRestaurants();
                });
            }
        });
    }


    private void loadDriverInProgressOrders(int driverId) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_DRIVER_IN_PROGRESS_LITE + driverId);

                handler.post(() -> {
                    if (response == null || response.startsWith("Error") || response.isEmpty()) {
                        Toast.makeText(this, "Failed to load IN_PROGRESS orders", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Type listType = new TypeToken<List<DriverOrderDto>>(){}.getType();
                    inProgressOrders = new Gson().fromJson(response, listType);

                    ListView lvDriver = findViewById(R.id.driverInProgressList);
                    if (lvDriver != null) {
                        inProgressAdapter = new DriverOrdersAdapter(this, inProgressOrders);
                        lvDriver.setAdapter(inProgressAdapter);
                    }

                    selectedInProgress = null;
                });

            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    goToLogin();
                });
            }
        });
    }

    private void finishSelectedInProgress() {
        if (selectedInProgress == null) {
            Toast.makeText(this, "Select an order first", Toast.LENGTH_SHORT).show();
            return;
        }

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String body = "{\"orderId\":" + selectedInProgress.id + ",\"driverId\":" + driverId + "}";
                String response = RestOperations.sendPut(DRIVER_FINISH_ORDER, body);

                handler.post(() -> {
                    if (response == null || response.startsWith("Error") || response.isEmpty()) {
                        Toast.makeText(this, "Failed to finish order", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(this, "Order finished", Toast.LENGTH_SHORT).show();
                    loadDriverInProgressOrders(driverId);
                });

            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    goToLogin();
                });
            }
        });
    }
    private void loadRestaurants(){
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            System.out.println("executorius");
            System.out.println("executorius");
            System.out.println("executorius");
            System.out.println("executorius");
            System.out.println("executorius");
            try {
                String response = RestOperations.sendGet(GET_ALL_RESTAURANTS_URL);
                System.out.println(response);
                handler.post(() -> {
                    try {
                        System.out.println("executorius");
                        System.out.println("executorius");
                        System.out.println("executorius");
                        System.out.println("executorius");
                        System.out.println("executorius");
                        if (response != null && !response.startsWith("Error")) {
                            //Cia yra dalis, kaip is json, kuriame yra [{},{}, {},...] paversti i List is Restoranu

                            GsonBuilder gsonBuilder = new GsonBuilder();
                            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
                            Gson gsonRestaurants = gsonBuilder.setPrettyPrinting().create();
                            Type restaurantListType = new TypeToken<List<Restaurant>>() {
                            }.getType();
                            List<Restaurant> restaurantListFromJson = gsonRestaurants.fromJson(response, restaurantListType);
                            //Json parse end

                            //Reikia tuos duomenis, kuriuos ka tik isparsinau is json, atvaizduoti grafiniam elemente
                            ListView restaurantListElement = findViewById(R.id.restaurantList);
                            if (restaurantListFromJson != null && !restaurantListFromJson.isEmpty()) {
                                RestaurantAdapter adapter = new RestaurantAdapter(this, restaurantListFromJson);
                                restaurantListElement.setAdapter(adapter);

                                restaurantListElement.setOnItemClickListener((parent, view, position, id) -> {
                                    Restaurant selectedRestaurant = restaurantListFromJson.get(position);
                                    Intent intentMenu = new Intent(WoltRestaurants.this, MenuActivity.class);
                                    intentMenu.putExtra("restaurantId", selectedRestaurant.getId());
                                    intentMenu.putExtra("userId", currentUser.getId());
                                    startActivity(intentMenu);
                                });
                            } else {
                                Toast.makeText(this, "No restaurants available", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(this, "Error loading restaurants", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    goToLogin();
                });
            }
        });
    }



    private boolean isRestaurantJson(String response) {
        try {
            JsonObject o = JsonParser.parseString(response).getAsJsonObject();
            String userType = o.has("userType") ? o.get("userType").getAsString() : "";
            return userType.endsWith(".Restaurant") || userType.contains("Restaurant");
        } catch (Exception e) {
            return false;
        }
    }


    private boolean isDriverJson(String response) {
        try {
            JsonObject o = JsonParser.parseString(response).getAsJsonObject();
            String userType = o.has("userType") ? o.get("userType").getAsString() : "";
            return userType.endsWith(".Driver") || userType.contains("Driver") || o.has("licensePlate");
        } catch (Exception e) {
            return false;
        }
    }


    private void goToLogin() {
        Toast.makeText(this, "Restaurant accounts cannot login here", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(WoltRestaurants.this, MainActivity.class); // <-- your login screen
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if driver list visible, refresh
        ListView lvDriver = findViewById(R.id.driverInProgressList);
        if (lvDriver != null && lvDriver.getVisibility() == View.VISIBLE) {
            loadDriverInProgressOrders(driverId);
        }
    }




}