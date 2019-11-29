package com.macisdev.apppedidospizzeria;

import java.util.ArrayList;
import java.util.Locale;

class OrderElement {
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

    private String getName() {
        return name;
    }

    private String getSize() {
        return size;
    }

    private String getExtras() {
        return extras;
    }

    public double getPrice() {
        return price;
    }

    public static ArrayList<String> getSummaryStrings() {
        ArrayList<String> stringArrayList = new ArrayList<>();

        for (OrderElement element : MainActivity.ORDER_ELEMENTS) {
            stringArrayList.add(element.toString());
        }

        return stringArrayList;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s  -  %s  -  %.2f€\n%s",getName(), getSize(), getPrice(), getExtras());
    }
}
