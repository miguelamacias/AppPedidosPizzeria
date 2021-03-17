package com.macisdev.apppedidospizzeria.model;

import androidx.annotation.NonNull;

import java.util.Locale;

public class OrderElement {
    private final int code;
    private final String name;
    private final String size;
    private final String extras;
    private final double price;

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