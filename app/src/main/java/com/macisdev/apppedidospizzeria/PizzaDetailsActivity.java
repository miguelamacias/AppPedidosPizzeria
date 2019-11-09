package com.macisdev.apppedidospizzeria;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PizzaDetailsActivity extends AppCompatActivity {
    public static final String PIZZA_ID = "pizzaId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_details);
        //Loads the views from the layout
        TextView tvPizzaName = findViewById(R.id.tvIPizzaName);
        TextView tvPizzaIngredients = findViewById(R.id.tvPizzaIngredients);

        //Loads the pizza _id that was selected
        Intent intent = getIntent();
        int selectedPizzaId = intent.getIntExtra(PIZZA_ID, 0);

        //Another cursor is needed so we create all the needed stuffs
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("pizzas",
                new String[]{"name", "ingredients"},
                "_id = ?",
                new String[]{String.valueOf(selectedPizzaId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            tvPizzaName.setText(cursor.getString(0));
            tvPizzaIngredients.setText(cursor.getString(1));
        }

        cursor.close();
        db.close();

    }
}
