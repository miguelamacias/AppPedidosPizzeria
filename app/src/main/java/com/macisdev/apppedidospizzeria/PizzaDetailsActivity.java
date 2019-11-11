package com.macisdev.apppedidospizzeria;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class PizzaDetailsActivity extends AppCompatActivity {
    public static final String PIZZA_ID = "pizzaId";
    public static final String MEDIUM_SIZE = "~26cms";
    public static final String BIG_SIZE = "~40cms";


    SQLiteDatabase db;
    Cursor cursorNameDetails;

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
        db = dbHelper.getReadableDatabase();

        cursorNameDetails = db.query("pizzas",
                new String[]{"name", "ingredients"},
                "_id = ?",
                new String[]{String.valueOf(selectedPizzaId)},
                null, null, null);

        if (cursorNameDetails.moveToFirst()) {
            tvPizzaName.setText(cursorNameDetails.getString(0));
            tvPizzaIngredients.setText(cursorNameDetails.getString(1));
        }

        cursorNameDetails.close();


        //we also need the sizes and prices, its easier getting them in a separate query
        final Cursor cursorSizePrice = db.query("pizzas_sizes",
                new String[]{"_id", "size_id", "price"},
                "pizza_id = ?",
                new String[]{String.valueOf(selectedPizzaId)},
                null,
                null,
                null);

        SimpleCursorAdapter adapterSizePrice = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                cursorSizePrice,
                new String[]{"size_id", "price"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0);

        //VewBinder used to format the price correctly
        adapterSizePrice.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 2) { //price is the column #2 of the cursor
                    double price = cursor.getDouble(columnIndex); //getting the price as is stored in the db
                    TextView textView = (TextView) view; //making the generic view a textView

                    textView.setText(String.format(Locale.getDefault(), "%.2f%c", price, '€')); //formatting the price to show the currency sign.
                    return true;
                }
                return false;
            }
        });

        ListView sizes_list = findViewById(R.id.sizes_list);
        sizes_list.setAdapter(adapterSizePrice);




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursorNameDetails.close();
        db.close();
    }
}
