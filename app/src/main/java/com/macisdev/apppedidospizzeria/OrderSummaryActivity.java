package com.macisdev.apppedidospizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class OrderSummaryActivity extends AppCompatActivity {
    boolean orderIsEmpty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        //Creates and populates the listView that represents the order
        ListView summaryList = findViewById(R.id.summary_list);
        summaryList.setAdapter(new ArrayAdapter<>(this, R.layout.layout_listview_order_summary,
                R.id.text1, OrderElement.getSummaryStrings()));

        //Calculate and shows the total price of the order
        TextView tvTotalPrice = findViewById(R.id.tv_total_price);
        double totalPrice = 0;
        for (OrderElement element:MainActivity.ORDER_ELEMENTS) {
            totalPrice += element.getPrice();
        }
        if (totalPrice != 0) {
            orderIsEmpty = false;
        }
        tvTotalPrice.setText(String.format(Locale.getDefault(), "%.2f€", totalPrice));
    }

    public void deleteFromSummary(View v) {
        Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show();
    }

    public void continueToMakeOrder(View v) {
        if (!orderIsEmpty) {
            startActivity(new Intent(this, PlaceOrderActivity.class));
        } else {
            Toast.makeText(this, R.string.error_empty_order, Toast.LENGTH_SHORT).show();
        }

    }
}
