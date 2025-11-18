package com.example.prv.model;

import com.google.gson.annotations.SerializedName;

public class Car {
    @SerializedName("id")
    private int id;

    @SerializedName("model")
    private String model;

    @SerializedName("year")
    private int year;

    @SerializedName("price")
    private double price;

    @SerializedName("status")
    private String status;

    @SerializedName("image_url")
    private String imageUrl;

    public Car() {}

    // Конструктор для тестовых данных
    public Car(String model, int year, double price, String status, String imageUrl) {
        this.model = model;
        this.year = year;
        this.price = price;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    // Геттеры
    public int getId() { return id; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }

    public String getImageUrl() {
        if (imageUrl != null && imageUrl.startsWith("DEFAULT ")) {
            return imageUrl.substring(8);
        }
        return imageUrl;
    }


    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setModel(String model) { this.model = model; }
    public void setYear(int year) { this.year = year; }
    public void setPrice(double price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}