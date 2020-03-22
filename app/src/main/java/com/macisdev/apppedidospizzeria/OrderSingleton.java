package com.macisdev.apppedidospizzeria;

import java.util.ArrayList;

//Class that represent the current order of the customer.
//Singleton 'cause customer can only place one order at a time
public class OrderSingleton {
    private ArrayList<OrderElement> orderElementsList = new ArrayList<>();

    public ArrayList<OrderElement> getOrderElementsList() {
        return orderElementsList;
    }

    private static final OrderSingleton instance = new OrderSingleton();
    public static OrderSingleton getInstance() {
        return instance;
    }
}
