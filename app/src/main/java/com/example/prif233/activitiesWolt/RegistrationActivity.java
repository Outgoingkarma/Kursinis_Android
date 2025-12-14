package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.CREATE_BASIC_USER_URL;
import static com.example.prif233.Utils.Constants.CREATE_DRIVER_URL;
import static com.example.prif233.Utils.Constants.VALIDATE_USER_URL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prif233.R;
import com.example.prif233.Utils.RestOperations;
import com.example.prif233.model.BasicUser;
import com.example.prif233.model.Driver;
import com.example.prif233.model.DriverVehicleType;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegistrationActivity extends AppCompatActivity {

    private Spinner vehicleTypeSpinner;
    private DriverVehicleType selectedVehicleType;
    private View addressField;
    private View licensePlateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup vehicle type spinner
        vehicleTypeSpinner = findViewById(R.id.regVehicleTypeSpinner);
        ArrayAdapter<DriverVehicleType> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                DriverVehicleType.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(adapter);
        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVehicleType = (DriverVehicleType) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedVehicleType = DriverVehicleType.CAR;
            }
        });
        selectedVehicleType = DriverVehicleType.CAR;

        // Setup checkbox listener to toggle fields
        CheckBox isDriverCheckbox = findViewById(R.id.regIsDriver);
        addressField = findViewById(R.id.regAddressField);
        licensePlateField = findViewById(R.id.regLicensePlateField);

        isDriverCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show driver fields, hide address field
                addressField.setVisibility(View.GONE);
                licensePlateField.setVisibility(View.VISIBLE);
                vehicleTypeSpinner.setVisibility(View.VISIBLE);
            } else {
                // Show address field, hide driver fields
                addressField.setVisibility(View.VISIBLE);
                licensePlateField.setVisibility(View.GONE);
                vehicleTypeSpinner.setVisibility(View.GONE);
            }
        });
    }

    public void createAccount(View view) {

        TextView login = findViewById(R.id.regLoginField);
        TextView psw = findViewById(R.id.regPasswordField);
        TextView name = findViewById(R.id.regNameField);
        TextView surname = findViewById(R.id.regSurnameField);
        TextView phone = findViewById(R.id.regPhoneField);
        TextView email = findViewById(R.id.regEmailField);
        TextView address = findViewById(R.id.regAddressField);
        CheckBox isDriverCheckbox = findViewById(R.id.regIsDriver);
        TextView licensePlate = findViewById(R.id.regLicensePlateField);

        //Patikrinti, ar buvo pasirinktas driver ar ne
        String userInfo = "{}";
        String url;
        boolean isDriver = isDriverCheckbox.isChecked();

        if (isDriver) {
            Driver driver = new Driver(
                    login.getText().toString(),
                    psw.getText().toString(),
                    name.getText().toString(),
                    surname.getText().toString(),
                    phone.getText().toString(),
                    email.getText().toString(),
                    licensePlate.getText().toString(),
                    selectedVehicleType,
                    true,  // isAvailable - default to true for new drivers
                    0      // totalDeliveries - default to 0 for new drivers
            );
            Gson gson = new Gson();
            userInfo = gson.toJson(driver, Driver.class);
            url = CREATE_DRIVER_URL;
            System.out.println(userInfo);
        } else {
            BasicUser basicUser = new BasicUser(
                    login.getText().toString(),
                    psw.getText().toString(),
                    name.getText().toString(),
                    surname.getText().toString(),
                    phone.getText().toString(),
                    email.getText().toString(),
                    address.getText().toString()
            );
            Gson gson = new Gson();
            userInfo = gson.toJson(basicUser, BasicUser.class);
            url = CREATE_BASIC_USER_URL;
            System.out.println(userInfo);
        }

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String finalUserInfo = userInfo;
        String finalUrl = url;
        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(finalUrl, finalUserInfo);
                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty()) {
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            } catch (IOException e) {
                //Toast reikes
            }

        });

    }
    public void loadLogWindow(View view) {
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(intent);
    }
}