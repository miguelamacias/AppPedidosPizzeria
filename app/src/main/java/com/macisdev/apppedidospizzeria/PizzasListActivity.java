package com.macisdev.apppedidospizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class PizzasListActivity extends AppCompatActivity implements PizzasListFragment.PizzaListInterface {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizzas);
    }

    //launch the summary activity from the floating button
    public void goToSummary(View v) {
        startActivity(new Intent(this, OrderSummaryActivity.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void pizzaClicked(int id) {
        Intent intent = new Intent(PizzasListActivity.this, ProductDetailsActivity.class);
        intent.putExtra(ProductDetailsActivity.PRODUCT_ID_KEY, id);
        startActivity(intent);
    }
}
