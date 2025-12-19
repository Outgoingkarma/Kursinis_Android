package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.GET_MESSAGES_BY_ORDER;
import static com.example.prif233.Utils.Constants.SEND_MESSAGE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prif233.R;
import com.example.prif233.Utils.ChatMessageAdapter;
import com.example.prif233.Utils.LocalDateTimeAdapter;
import com.example.prif233.Utils.RestOperations;
import com.example.prif233.model.ChatMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatSystem extends AppCompatActivity {

    private int orderId;
    private int userId;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private static final long REFRESH_MS = 5000L;

    private final Runnable refreshRunnable = new Runnable() {
        @Override public void run() {
            loadMessages();
            refreshHandler.postDelayed(this, REFRESH_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_system);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Noriu uzkrauti zinutes konkreciam klientui

        Intent intent = getIntent();
        orderId = intent.getIntExtra("orderId", 0);
        userId = intent.getIntExtra("userId", 0);


    }



    @Override
    protected void onResume() {
        super.onResume();
        refreshHandler.removeCallbacks(refreshRunnable);
        refreshHandler.post(refreshRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshHandler.removeCallbacks(refreshRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }






    private void loadMessages() {
        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_MESSAGES_BY_ORDER + orderId);
                System.out.println(response);
                uiHandler.post(() -> {
                    try {
                        if (response == null || response.startsWith("Error") || response.isEmpty()) return;
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
                            Gson gsonMessages = gsonBuilder.setPrettyPrinting().create();
                            Type messagesListType = new TypeToken<List<ChatMessage>>() {
                            }.getType();
                            List<ChatMessage> messagesListFromJson = gsonMessages.fromJson(response, messagesListType);
                            ListView messagesListElement = findViewById(R.id.messageList);
                            ChatMessageAdapter adapter = new ChatMessageAdapter(this, messagesListFromJson);
                            messagesListElement.setAdapter(adapter);
//                            ArrayAdapter<ChatMessage> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messagesListFromJson);
//                            messagesListElement.setAdapter(adapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                uiHandler.post(() -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });

    }

    public void sendMessage(View view) {
        TextView messageBody = findViewById(R.id.bodyField);

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("orderId", orderId);
        jsonObject.addProperty("messageText", messageBody.getText().toString());

        String message = gson.toJson(jsonObject);

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(SEND_MESSAGE, message);
                System.out.println(response);
                uiHandler.post(() -> {
                    try {
                        if (response != null && !response.startsWith("Error") && !response.isEmpty()) {
                            messageBody.setText("");
                            loadMessages();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                uiHandler.post(() -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });


    }
}

