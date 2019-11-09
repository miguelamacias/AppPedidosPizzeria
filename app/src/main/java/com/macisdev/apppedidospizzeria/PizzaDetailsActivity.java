package com.macisdev.apppedidospizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PizzaDetailsActivity extends AppCompatActivity {
    public static final String PIZZA_ID = "pizzaId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_details);

        Intent intent = getIntent();

        int pizzaId = intent.getIntExtra(PIZZA_ID, 0);

        TextView tvPizzaId = findViewById(R.id.pizzaId);

        tvPizzaId.setText(String.valueOf(pizzaId));

    }
}
