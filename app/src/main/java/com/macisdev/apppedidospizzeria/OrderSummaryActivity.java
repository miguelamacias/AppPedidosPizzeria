package com.macisdev.apppedidospizzeria;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class OrderSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        ListView SummaryList = findViewById(R.id.summary_list);

        SummaryList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                OrderElement.getSummaryStrings()));

        TextView tvTotalPrice = findViewById(R.id.tv_total_price);

        double totalPrice = 0;
        for (OrderElement element:MainActivity.orderelements) {
            totalPrice += element.getPrice();
        }

        tvTotalPrice.setText(String.format(Locale.getDefault(), "%.2f €", totalPrice));
    }
}
