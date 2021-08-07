package com.macisdev.apppedidospizzeria.model;

import androidx.annotation.NonNull;

import java.util.Locale;

public class OrderElement {
    private int code;
    private String name;
    private String size;
    private String extras;
    private double price;

    public OrderElement() {
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public OrderElement(int code, String name, String size, String extras, double price) {
        this.code = code;
        this.name = name;
        this.size = size;
        this.extras = extras;
        this.price = price;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getExtras() {
        return extras;
    }

    public double getPrice() {
        return price;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s%n%s  -  %.2f€\n%s",getName(), getSize(), getPrice(), getExtras());
    }
}