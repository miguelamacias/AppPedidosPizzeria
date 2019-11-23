package com.macisdev.apppedidospizzeria;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class OrderSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        ListView summaryList = findViewById(R.id.summary_list);

        summaryList.setAdapter(new ArrayAdapter<String>(this, R.layout.layout_listview_order_summary,
                R.id.text1, OrderElement.getSummaryStrings()));

        TextView tvTotalPrice = findViewById(R.id.tv_total_price);

        double totalPrice = 0;
        for (OrderElement element:MainActivity.orderelements) {
            totalPrice += element.getPrice();
        }

        tvTotalPrice.setText(String.format(Locale.getDefault(), "%.2f€", totalPrice));
    }

    public void deleteFromSummary(View v) {
        Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show();
    }
}
