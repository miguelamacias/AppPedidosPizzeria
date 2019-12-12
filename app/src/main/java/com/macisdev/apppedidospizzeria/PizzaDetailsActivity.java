package com.macisdev.apppedidospizzeria;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class PizzaDetailsActivity extends AppCompatActivity {
    public static final String PIZZA_ID_KEY = "pizzaIdKey";
    public static final String PIZZA_EXTRA_INGREDIENTS_KEY = "pizzaExtraIngredientsKey";
    public static final String PIZZA_EXTRA_TYPE_KEY = "pizzaExtraTypeKey";
    public static final String PIZZA_EXTRA_NUMBER_KEY = "pizzaExtraNumberKey";


    private Spinner spinnerQuantity;
    private SQLiteDatabase db;
    private Cursor cursorNameDetails;
    //Fields needed to get the info about the item ordered
    private int pizzaId, numberOfExtras;
    private String pizzaName, pizzaSize;
    private double pizzaPrice;
    private String pizzaExtras;
    private double totalPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_details);
        pizzaExtras = getString(R.string.no);
        pizzaSize = getString(R.string.size_medium);

        //Loads the views from the layout
        TextView tvPizzaName = findViewById(R.id.tvIPizzaName);
        TextView tvPizzaIngredients = findViewById(R.id.tvPizzaIngredients);
        spinnerQuantity = findViewById(R.id.spinner_quantity);
        TextView tvTotalPrice = findViewById(R.id.tv_total_price);

        //Loads the pizza _id that was selected
        Intent intent = getIntent();
        int selectedPizzaId = intent.getIntExtra(PIZZA_ID_KEY, 0);
        pizzaId = selectedPizzaId;

        //Loads the extra ingredients info if they have been added
        int extraModeUsed = getIntent().getIntExtra(PIZZA_EXTRA_TYPE_KEY, 0);
        if (extraModeUsed != 0) {
            pizzaExtras = getIntent().getStringExtra(PIZZA_EXTRA_INGREDIENTS_KEY);
            TextView tvExtras = findViewById(R.id.tv_extras);
            tvExtras.setText(pizzaExtras);
            numberOfExtras = getIntent().getIntExtra(PIZZA_EXTRA_NUMBER_KEY, 0);
            //TODO change the price of the extra ingredient according to the size
            totalPrice = numberOfExtras * 0.5;
            tvTotalPrice.setText(String.valueOf(totalPrice));
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
                "size_id DESC");

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

        //loads the spinner with the available sizes
        Spinner spinnerSizes = findViewById(R.id.spinner_size);
        spinnerSizes.setAdapter(adapterSizePrice);

        //attach a listener to the spinner to get the selected size
        spinnerSizes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    pizzaSize = getString(R.string.size_medium);
                }
                if (position == 1) {
                    pizzaSize = getString(R.string.size_big);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //Method that triggers when the Customize pizza buttons is pressed
    public void customizePizza(View v) {
        //Starts the customize activity in addition mode
        startActivity(CustomizePizzaActivity.newIntentAddIngredients(this, pizzaId));
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
                pizzaExtras, pizzaPrice);
        int quantity = Integer.parseInt((String)spinnerQuantity.getSelectedItem());
        for (int i = 0; i < quantity; i++) {
            MainActivity.ORDER_ELEMENTS.add(elementTobeAdded);
        }

        Toast.makeText(this, R.string.element_added, Toast.LENGTH_SHORT).show();
        cursorPrice.close();
        startActivity(this.getParentActivityIntent());
    }

    //creates a new intent properly configured to open this activity after a customization has been done
    public static Intent newIntentFromCustomize(Context context, int pizzaId, int extraType, String extraIngredients, int numberOfExtras) {
        Intent intent = new Intent(context, PizzaDetailsActivity.class);
        intent.putExtra(PIZZA_ID_KEY, pizzaId);
        intent.putExtra(PIZZA_EXTRA_TYPE_KEY, extraType);
        intent.putExtra(PIZZA_EXTRA_INGREDIENTS_KEY, extraIngredients);
        intent.putExtra(PIZZA_EXTRA_NUMBER_KEY, numberOfExtras);

        return intent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursorNameDetails.close();
        db.close();
    }
}