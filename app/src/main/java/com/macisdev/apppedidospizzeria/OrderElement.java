package com.macisdev.apppedidospizzeria;

import java.util.ArrayList;
import java.util.Locale;

public class OrderElement {
    private int code;
    private String name;
    private String size;
    private String extras;
    private double price;

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

    public static ArrayList<String> getSummaryStrings() {
        ArrayList<String> stringArrayList = new ArrayList<>();

        for (OrderElement element : MainActivity.orderelements) {
            stringArrayList.add(element.toString());
        }

        return stringArrayList;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s \t %s \t %.2f",getName(), getSize(), getPrice());
    }
}
