package com.macisdev.apppedidospizzeria;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class PizzasActivity extends AppCompatActivity {
    //the cursor and Database are declared her so they can be closed from onDestroy
    private Cursor cursor;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizzas);

        //loads pizza list from DB
        ListView pizzaList = findViewById(R.id.pizza_list);
        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        cursor = db.query("pizzas", new String[]{"_id", "name", "ingredients"}, null, null, null, null, null);

        pizzaList.setAdapter(new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                cursor,
                new String[]{"name", "ingredients"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0));

        pizzaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PizzasActivity.this, PizzaDetailsActivity.class);
                intent.putExtra(PizzaDetailsActivity.PIZZA_ID, (int) id); //this ID contains the _id of the pizza in the DB table
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cursor.close();
        db.close();
    }
}
