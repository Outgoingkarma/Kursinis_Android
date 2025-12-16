package com.example.prif233.model;


import com.google.gson.annotations.SerializedName;

public class FoodOrder {
    private int id;
    private boolean isDelivered;
    @SerializedName("orderPrice")
    private double price;
    private FoodOrderStatus orderStatus;

    public FoodOrder(int id, boolean isDelivered, double price, FoodOrderStatus orderStatus) {
        this.id = id;
        this.isDelivered = isDelivered;
        this.price = price;
        this.orderStatus = orderStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public double getPrice() { return price; }

    public void setPrice(double orderPrice) {
        this.price = orderPrice;
    }

    public FoodOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(FoodOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
