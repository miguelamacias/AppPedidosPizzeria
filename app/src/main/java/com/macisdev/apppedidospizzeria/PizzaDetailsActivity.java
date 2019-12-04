package com.macisdev.apppedidospizzeria;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class PizzaDetailsActivity extends AppCompatActivity {
    public static final String PIZZA_ID_KEY = "pizzaIdKey";
    public static final String PIZZA_EXTRA_INGREDIENTS_KEY = "pizzaExtraIngredientsKey";
    public static final String PIZZA_EXTRA_TYPE_KEY = "pizzaExtraTypeKey";
    public static final String PIZZA_EXTRA_NUMBER_KEY = "pizzaExtraNumberKey";


    private NumberPicker quantityPicker;
    private SQLiteDatabase db;
    private Cursor cursorNameDetails;
    //Fields needed to get the info about the item ordered
    private int pizzaId, numberOfExtras;
    private String pizzaName, pizzaSize, pizzaExtrasAdded, pizzaExtrasRemoved;
    private double pizzaPrice;
    private static String pizzaExtras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_details);
        pizzaExtras = getString(R.string.no);
        pizzaExtrasRemoved = "Sin: ";
        pizzaExtrasAdded = "Extra: ";

        //Loads the views from the layout
        TextView tvPizzaName = findViewById(R.id.tvIPizzaName);
        TextView tvPizzaIngredients = findViewById(R.id.tvPizzaIngredients);

        //Loads the pizza _id that was selected
        Intent intent = getIntent();
        int selectedPizzaId = intent.getIntExtra(PIZZA_ID_KEY, 0);
        pizzaId = selectedPizzaId;

        //Loads the extra ingredients if they have been added
        int extraModeUsed = getIntent().getIntExtra(PIZZA_EXTRA_TYPE_KEY, 0);
        if (extraModeUsed != 0) {
            loadsExtraIngredients(extraModeUsed);
        }

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
            pizzaName = cursorNameDetails.getString(0);
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

        //ViewBinder used to format the price correctly
        adapterSizePrice.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 2) { //price is the column #2 of the cursor
                    double price = cursor.getDouble(columnIndex); //getting the price as is stored in the db
                    pizzaPrice = price;
                    TextView textView = (TextView) view; //making the generic view a textView

                    textView.setText(String.format(Locale.getDefault(), "%.2f%c", price, '€')); //formatting the price to show the currency sign.
                    return true;
                }
                return false;
            }
        });

        ListView sizesList = findViewById(R.id.sizes_list);
        sizesList.setAdapter(adapterSizePrice);

        final TextView tvChoosenSize = findViewById(R.id.tv_choosen_size);
        final LinearLayout layoutAddOrder = findViewById(R.id.layout_add_order);
        final Button btnCustomizePizza = findViewById(R.id.btn_customize_pizza_add);


        sizesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    tvChoosenSize.setText(R.string.size_big);
                    pizzaSize = getString(R.string.size_big);
                }

                if (position == 1) {
                    tvChoosenSize.setText(R.string.size_medium);
                    pizzaSize = getString(R.string.size_medium);
                }
                 layoutAddOrder.setVisibility(View.VISIBLE);
                btnCustomizePizza.setVisibility(View.VISIBLE);

            }

        });

        quantityPicker = findViewById(R.id.quantity_picker);
        quantityPicker.setMinValue(1);
        quantityPicker.setMaxValue(10);
    }

    //Method that triggers when one of the Customize pizza buttons is pressed
    public void customizePizza(View v) {
        Intent customizePizzaIntent = new Intent(this, CustomizePizzaActivity.class);

        switch (v.getId()) {
            case R.id.btn_customize_pizza_add:
                customizePizzaIntent.putExtra(CustomizePizzaActivity.EXTRA_MODE_KEY, CustomizePizzaActivity.ADD_MODE);
                break;
            case R.id.btn_customize_pizza_remove:
                customizePizzaIntent.putExtra(CustomizePizzaActivity.EXTRA_MODE_KEY, CustomizePizzaActivity.REMOVE_MODE);
                break;
        }
        customizePizzaIntent.putExtra(CustomizePizzaActivity.PIZZA_ID_KEY, pizzaId);
        startActivity(customizePizzaIntent);
    }

    //Method when add button is pressed
    public void addPizzaToOrder(View v) {
        //Retrieve the price of the selected pizza

        Cursor cursorPrice = db.rawQuery("SELECT price FROM pizzas_sizes WHERE size_id = ? AND pizza_id = ?",
                new String[]{pizzaSize, String.valueOf(pizzaId)});
        if (cursorPrice.moveToFirst()) {
            pizzaPrice = cursorPrice.getDouble(0);
        }

        //Creates the element to be added with the right information
        OrderElement elementTobeAdded = new OrderElement(pizzaId, pizzaName, pizzaSize,
                pizzaExtrasAdded + "" + pizzaExtrasRemoved, pizzaPrice);
        for (int i = 0; i < quantityPicker.getValue(); i++) {
            MainActivity.ORDER_ELEMENTS.add(elementTobeAdded);
        }

        Toast.makeText(this, R.string.element_added, Toast.LENGTH_SHORT).show();
        cursorPrice.close();
        startActivity(this.getParentActivityIntent());
    }

    //Method that loads the extra ingredients selected
    private void loadsExtraIngredients(int mode) {
        if (mode == CustomizePizzaActivity.REMOVE_MODE) {
            pizzaExtrasRemoved += getIntent().getStringExtra(PIZZA_EXTRA_INGREDIENTS_KEY);
        }

        if (mode == CustomizePizzaActivity.ADD_MODE) {
            //TODO get the number of extras from the intent.
            pizzaExtrasAdded += getIntent().getStringExtra(PIZZA_EXTRA_TYPE_KEY);
        }
        Log.d("INGREDIENTES_EXTRAS", pizzaExtrasAdded + " - " + pizzaExtrasRemoved);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursorNameDetails.close();
        db.close();
    }
}