package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.DELETE_USER;
import static com.example.prif233.Utils.Constants.GET_USER_BY_ID;
import static com.example.prif233.Utils.Constants.UPDATE_USER;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prif233.R;
import com.example.prif233.Utils.LocalDateTimeAdapter;
import com.example.prif233.Utils.RestOperations;
import com.example.prif233.model.BasicUser;
import com.example.prif233.model.Driver;
import com.example.prif233.model.DriverVehicleType;
import com.example.prif233.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EditAccountActivity extends AppCompatActivity {
    private User currentUser;
    private EditText usernameEdit;
    private EditText nameEdit;
    private EditText surnameEdit;
    private EditText phoneEdit;
    private EditText addressEdit;
    private EditText licenseEdit;
    private TextView licenseLabel;
    private String userType;
    private EditText newPasswordEdit;
    private Spinner vehicleTypeSpinner;
    private TextView vehicleTypeLabel;
    private DriverVehicleType selectedVehicleType;
    private Button deleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_account);
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        Intent intent = getIntent();
        String userJson = intent.getStringExtra("userJson");
        userType = intent.getStringExtra("userType");
        System.out.println("EditAccountActivity userType = " + userType);
        System.out.println("EditAccountActivity userJson = " + userJson);
        if (userJson != null && !userJson.isEmpty()) {
            try {
                Gson gson = new Gson();
                User base = gson.fromJson(userJson, User.class);
                int userId = base.getId();
                loadUserFromServer(userId);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading account information", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "User information not available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupUI() {
        usernameEdit = findViewById(R.id.usernameEdit);
        nameEdit = findViewById(R.id.nameEdit);
        surnameEdit = findViewById(R.id.surnameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        addressEdit = findViewById(R.id.addressEdit);
        licenseEdit = findViewById(R.id.licenseEdit);
        licenseLabel = findViewById(R.id.licenseLabel);
        newPasswordEdit = findViewById(R.id.newPasswordEdit);

        Button saveButton = findViewById(R.id.saveButton);

        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> showDeleteConfirmDialog());

        if (saveButton != null) {
            saveButton.setOnClickListener(v -> saveAccountChanges());
        }

        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner);
        vehicleTypeLabel = findViewById(R.id.vehicleTypeLabel);

// Spinner adapter
        ArrayAdapter<DriverVehicleType> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                DriverVehicleType.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(adapter);

// save selected value
        vehicleTypeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedVehicleType = (DriverVehicleType) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedVehicleType = DriverVehicleType.CAR; // fallback
            }
        });
        usernameEdit.setText(currentUser.getLogin());
        nameEdit.setText(currentUser.getName());
        surnameEdit.setText(currentUser.getSurname());
        phoneEdit.setText(currentUser.getPhone_number());
        // Populate fields
        boolean isDriver = (currentUser instanceof Driver);

        if (isDriver) {
            Driver driver = (Driver) currentUser;

            // show driver fields
            licenseEdit.setVisibility(View.VISIBLE);
            licenseLabel.setVisibility(View.VISIBLE);
            vehicleTypeSpinner.setVisibility(View.VISIBLE);
            vehicleTypeLabel.setVisibility(View.VISIBLE);

            // hide address for driver
            if (addressEdit != null) addressEdit.setVisibility(View.GONE);

            // fill license
            licenseEdit.setText(driver.getLicensePlate());

            // preselect vehicle type
            DriverVehicleType current = driver.getDriverVehicleType();
            if (current != null) {
                selectedVehicleType = current;
                int index = java.util.Arrays.asList(DriverVehicleType.values()).indexOf(current);
                if (index >= 0) vehicleTypeSpinner.setSelection(index);
            } else {
                selectedVehicleType = DriverVehicleType.CAR;
                vehicleTypeSpinner.setSelection(0);
            }

        } else {
            // basic user fields
            licenseEdit.setVisibility(View.GONE);
            licenseLabel.setVisibility(View.GONE);
            vehicleTypeSpinner.setVisibility(View.GONE);
            vehicleTypeLabel.setVisibility(View.GONE);

            if (addressEdit != null) addressEdit.setVisibility(View.VISIBLE);

            // fill address
            BasicUser bu = (BasicUser) currentUser;
            if (addressEdit != null) addressEdit.setText(bu.getAddress());
        }

        if (saveButton != null) {
            saveButton.setOnClickListener(v -> saveAccountChanges());
        }
    }

    private void saveAccountChanges() {
        if (currentUser == null) {
            Toast.makeText(this, "User information not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate required fields
        String username = usernameEdit.getText().toString().trim();
        String name = nameEdit.getText().toString().trim();
        String surname = surnameEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();

        if (username.isEmpty() || name.isEmpty() || surname.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update user object
        currentUser.setLogin(username);
        currentUser.setName(name);
        currentUser.setSurname(surname);
        currentUser.setPhone_number(phone);
        String newPass = newPasswordEdit.getText().toString().trim();
        if (!newPass.isEmpty()) {
            currentUser.setPassword(newPass);
        }

        if (currentUser instanceof BasicUser) {
            BasicUser basicUser = (BasicUser) currentUser;
            String address = addressEdit != null ? addressEdit.getText().toString().trim() : "";
            basicUser.setAddress(address);
        }

        if (currentUser instanceof Driver) {
            Driver driver = (Driver) currentUser;
            String license = licenseEdit != null ? licenseEdit.getText().toString().trim() : "";
            driver.setLicensePlate(license);
            if (selectedVehicleType != null) {
                driver.setDriverVehicleType(selectedVehicleType);
            }
        }


        // Send update to backend
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .create();

                String userJsonOut = gson.toJson(currentUser);
                System.out.println("PUT body = " + userJsonOut);

                if (currentUser instanceof Driver) {
                    userJsonOut = gson.toJson((Driver) currentUser);
                } else {
                    userJsonOut = gson.toJson((BasicUser) currentUser);
                }
                System.out.println("PUT body = " + userJsonOut);
                String response = RestOperations.sendPut(UPDATE_USER, userJsonOut);

                handler.post(() -> {
                    if (response != null && !response.startsWith("Error") && !response.isEmpty()) {
                        try {
                            Gson responseGson = new GsonBuilder()
                                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                    .create();
                            System.out.println("UPDATE response: " + response);

                            User updatedUser;
                            if (currentUser instanceof Driver) {
                                updatedUser = responseGson.fromJson(response, Driver.class);
                            } else {
                                updatedUser = responseGson.fromJson(response, BasicUser.class);
                            }
                            Intent resultIntent = new Intent();
                            String updatedUserJson = responseGson.toJson(updatedUser);
                            resultIntent.putExtra("userJson", updatedUserJson);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing updated user", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to update account", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadUserFromServer(int userId) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_USER_BY_ID + userId);

                handler.post(() -> {
                    if (response == null || response.startsWith("Error") || response.isEmpty()) {
                        Toast.makeText(this, "Failed to load user", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Detect user type from response
                    if (response.contains("licensePlate")) userType = "DRIVER";
                    else userType = "BASIC";

                    Gson g = new GsonBuilder()
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                            .create();

                    if ("DRIVER".equals(userType)) {
                        currentUser = g.fromJson(response, Driver.class);
                    } else {
                        currentUser = g.fromJson(response, BasicUser.class);
                    }

                    setupUI();
                });

            } catch (IOException e) {
                handler.post(() -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void deleteAccount() {
        if (currentUser == null) {
            Toast.makeText(this, "User not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = currentUser.getId();

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String resp = RestOperations.sendDelete(DELETE_USER + id);

                handler.post(() -> {
                    if (resp != null && !resp.startsWith("Error")) {
                        Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();

                        // Go back to login and clear back stack
                        Intent i = new Intent(EditAccountActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(this, "Delete failed: " + resp, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete account?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
