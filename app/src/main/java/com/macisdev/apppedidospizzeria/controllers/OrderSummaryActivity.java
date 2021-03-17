package com.macisdev.apppedidospizzeria.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.macisdev.apppedidospizzeria.R;
import com.macisdev.apppedidospizzeria.model.OrderElement;
import com.macisdev.apppedidospizzeria.model.OrderSingleton;

import java.util.Locale;

public class OrderSummaryActivity extends AppCompatActivity {
    ListView summaryList;
    ArrayAdapter<OrderElement> summaryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        //Creates and populates the listView that represents the order
        summaryList = findViewById(R.id.summary_list);
        summaryListAdapter = new ArrayAdapter<>(this, R.layout.layout_listview_order_summary,
                R.id.text1, OrderSingleton.getInstance().getOrderElementsList());
        summaryList.setAdapter(summaryListAdapter);

        //Calculate and shows the total price of the order
        updateTotalPrice();
    }

    //Deletes an element from the order.
    public void deleteFromSummary(View v) {
        //Gets the position of the button clicked
        int position = summaryList.getPositionForView((View) v.getParent());

        //Gets the element represented in that row and the deletes it
        OrderElement deletedElement = OrderSingleton.getInstance().getOrderElementsList().get(position);
        OrderSingleton.getInstance().getOrderElementsList().remove(position);

        //Updates the listView
        summaryListAdapter.notifyDataSetChanged();
        updateTotalPrice();

        //Creates a snackbar with a message and an action to undo the deletion of the order element
        Snackbar snackbar = Snackbar.make(findViewById(R.id.summary_parent_layout),
                R.string.deleted_element, Snackbar.LENGTH_LONG);
        snackbar.setAction(getText(R.string.undo), view -> {
            OrderSingleton.getInstance().getOrderElementsList().add(position, deletedElement);
            summaryListAdapter.notifyDataSetChanged();
            updateTotalPrice();
        });

        snackbar.show();

    }

    public void updateTotalPrice() {
        TextView tvTotalPrice = findViewById(R.id.tv_total_price);
        double totalPrice = 0;
        for (OrderElement element : OrderSingleton.getInstance().getOrderElementsList()) {
            totalPrice += element.getPrice();
        }
        tvTotalPrice.setText(String.format(Locale.getDefault(), "%.2f€", totalPrice));
    }

    public void continueToPlaceOrder(View v) {
        if (OrderSingleton.getInstance().getOrderElementsList().size() > 0) {
            startActivity(new Intent(this, PlaceOrderActivity.class));
        } else {
            Toast.makeText(this, R.string.error_empty_order, Toast.LENGTH_SHORT).show();
        }

    }
}