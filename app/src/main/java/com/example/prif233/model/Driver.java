package com.example.prif233.model;


import java.time.LocalDateTime;


public class Driver extends User {
    private String licensePlate;
    private DriverVehicleType driverVehicleType;
    private boolean isAvailable;
    private int totalDeliveries;


    public Driver(int id, String login, String password, String name, String surname, String phone_number, String email, LocalDateTime dateCreated, LocalDateTime dateModified, boolean isAdmin, String licensePlate, DriverVehicleType driverVehicleType, boolean isAvailable, int totalDeliveries) {
        super(id, login, password, name, surname, phone_number, email, dateCreated, dateModified, isAdmin);
        this.licensePlate = licensePlate;
        this.driverVehicleType = driverVehicleType;
        this.isAvailable = isAvailable;
        this.totalDeliveries = totalDeliveries;
    }

    public Driver(String login, String password, String name, String surname, String phone_number, String email, String licensePlate, DriverVehicleType driverVehicleType, boolean isAvailable, int totalDeliveries) {
        super(login, password, name, surname, phone_number, email);
        this.licensePlate = licensePlate;
        this.driverVehicleType = driverVehicleType;
        this.isAvailable = isAvailable;
        this.totalDeliveries = totalDeliveries;
    }

    public Driver(String licensePlate, DriverVehicleType driverVehicleType, boolean isAvailable, int totalDeliveries) {
        this.licensePlate = licensePlate;
        this.driverVehicleType = driverVehicleType;
        this.isAvailable = isAvailable;
        this.totalDeliveries = totalDeliveries;
    }

    public Driver() {
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public DriverVehicleType getDriverVehicleType() {
        return driverVehicleType;
    }

    public void setDriverVehicleType(DriverVehicleType driverVehicleType) {
        this.driverVehicleType = driverVehicleType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getTotalDeliveries() {
        return totalDeliveries;
    }

    public void setTotalDeliveries(int totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }
}














