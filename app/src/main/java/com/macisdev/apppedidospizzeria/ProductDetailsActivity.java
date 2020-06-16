package com.macisdev.apppedidospizzeria;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {
    public static final String PRODUCT_ID_KEY = "productIdKey";
    public static final String PRODUCT_EXTRA_INGREDIENTS_KEY = "productExtraIngredientsKey";
    public static final String PRODUCT_EXTRA_TYPE_KEY = "productExtraTypeKey";
    public static final String PRODUCT_EXTRA_NUMBER_KEY = "productExtraNumberKey";

    private static final Double MEDIUM_EXTRA_PRICE = 0.5;
    private static final Double BIG_EXTRA_PRICE = 1.0;


    private Spinner spinnerQuantity;
    private SQLiteDatabase db;
    private Cursor cursorNameDetails;
    private TextView tvTotalPrice;
    //Fields needed to get the info about the item ordered
    private int productID, numberOfExtras;
    private String productName, productSize;
    private double productPrice;
    private String productExtras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        productExtras = "";
        productSize = getString(R.string.size_medium_pizza);

        //Loads the views from the layout
        TextView tvProductName = findViewById(R.id.tv_product_name);
        TextView tvProductDescription = findViewById(R.id.tv_product_description);
        spinnerQuantity = findViewById(R.id.spinner_quantity);
        tvTotalPrice = findViewById(R.id.tv_total_price);

        //Loads the pizza _id that was selected
        Intent intent = getIntent();
        int selectedProductId = intent.getIntExtra(PRODUCT_ID_KEY, 0);
        productID = selectedProductId;

        //checks if the customize button should be visible (no extra ingredients available means no button shown)
        Button btnCustomize = findViewById(R.id.btn_customize);
        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();
        Cursor ingredientsCursor = db.rawQuery("SELECT _id, ingredient FROM products_ingredients WHERE product_id = ?",
                new String[]{String.valueOf(productID)});
        if (ingredientsCursor.getCount() == 0) {
            btnCustomize.setVisibility(View.INVISIBLE);
        }
        ingredientsCursor.close();

        //Loads the extra ingredients info if they have been added
        int extraModeUsed = getIntent().getIntExtra(PRODUCT_EXTRA_TYPE_KEY, 0);
        if (extraModeUsed != 0) {
            productExtras = getIntent().getStringExtra(PRODUCT_EXTRA_INGREDIENTS_KEY);
            TextView tvExtras = findViewById(R.id.tv_extras);
            tvExtras.setText(productExtras);
            numberOfExtras = getIntent().getIntExtra(PRODUCT_EXTRA_NUMBER_KEY, 0);
        }


        //A cursor is needed to get the name and description of the selected product
        cursorNameDetails = db.query("products",
                new String[]{"name", "description"},
                "_id = ?",
                new String[]{String.valueOf(selectedProductId)},
                null, null, null);

        if (cursorNameDetails.moveToFirst()) {
            tvProductName.setText(cursorNameDetails.getString(0));
            productName = cursorNameDetails.getString(0);
            tvProductDescription.setText(cursorNameDetails.getString(1));
        }

        cursorNameDetails.close();


        //we also need the sizes and prices, its easier getting them in a separate query
        final Cursor cursorSizePrice = db.query("products_sizes",
                new String[]{"_id", "size_id", "price"},
                "product_id = ?",
                new String[]{String.valueOf(selectedProductId)},
                null,
                null,
                "size_id DESC");

        SimpleCursorAdapter adapterSizePrice = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                cursorSizePrice,
                new String[]{"size_id", "price"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0);

        //ViewBinder used to format the price shown in the Spinner correctly
        adapterSizePrice.setViewBinder((view, cursor, columnIndex) -> {
            if (columnIndex == 2) { //price is the column #2 of the cursor
                double price = cursor.getDouble(columnIndex); //getting the price as is stored in the db
                TextView textView = (TextView) view; //making the generic view a textView

                textView.setText(String.format(Locale.getDefault(), "%.2f%c", price, '€')); //formatting the price to show the currency sign.
                return true;
            }
            return false;
        });

        //loads the spinner with the available sizes
        Spinner spinnerSizes = findViewById(R.id.spinner_size);
        spinnerSizes.setAdapter(adapterSizePrice);

        //attach a listener to the spinner to get the selected size
        spinnerSizes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tvSize = view.findViewById(android.R.id.text1);
                productSize = tvSize.getText().toString();
                refreshPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //shows the current price
        refreshPrice();
    }

    //Method that triggers when the Customize pizza buttons is pressed
    public void customizeProduct(View v) {
        if (productID > 100) { //extra ingredients can be added only to certain products, but they can be usually removed
            startActivity(CustomizeProductActivity.newIntentDeleteIngredients(this, "", productID, 0));
        } else {
            startActivity(CustomizeProductActivity.newIntentAddIngredients(this, productID));
        }
    }

    //Method when add button is pressed
    public void addProductToOrder(View v) {
        //Creates the element to be added with the right information
        OrderElement elementTobeAdded = new OrderElement(productID, productName, productSize,
                productExtras, refreshPrice());
        int quantity = Integer.parseInt((String)spinnerQuantity.getSelectedItem());
        for (int i = 0; i < quantity; i++) {
            OrderSingleton.getInstance().getOrderElementsList().add(elementTobeAdded);
        }

        Toast.makeText(this, R.string.element_added, Toast.LENGTH_SHORT).show();
        startActivity(this.getParentActivityIntent());
    }

    //creates a new intent properly configured to open this activity after a customization has been done
    public static Intent newIntentFromCustomize(Context context, int productId, int extraType, String extraIngredients, int numberOfExtras) {
        Intent intent = new Intent(context, ProductDetailsActivity.class);
        intent.putExtra(PRODUCT_ID_KEY, productId);
        intent.putExtra(PRODUCT_EXTRA_TYPE_KEY, extraType);
        intent.putExtra(PRODUCT_EXTRA_INGREDIENTS_KEY, extraIngredients);
        intent.putExtra(PRODUCT_EXTRA_NUMBER_KEY, numberOfExtras);

        return intent;
    }

    //updates and show the current price
    private double refreshPrice() {
        //Retrieve the price of the selected pizza
        Cursor cursorPrice = db.rawQuery("SELECT price FROM products_sizes WHERE size_id = ? AND product_id = ?",
                new String[]{productSize, String.valueOf(productID)});
        if (cursorPrice.moveToFirst()) {
            productPrice = cursorPrice.getDouble(0);
        }
        cursorPrice.close();
        //Calculates the full price with the extras added
        double extraIngredientPrice = (productSize.equals(getString(R.string.size_big_pizza)) ? BIG_EXTRA_PRICE : MEDIUM_EXTRA_PRICE);
        double totalPrice = productPrice + extraIngredientPrice * numberOfExtras;
        int quantity = Integer.parseInt((String)spinnerQuantity.getSelectedItem());
        tvTotalPrice.setText(String.format(Locale.getDefault(), "%.2f€", totalPrice * quantity));

        return totalPrice;
    }

    //launch the summary activity from the floating button
    public void goToSummary(View v) {
        startActivity(new Intent(this, OrderSummaryActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursorNameDetails.close();
        db.close();
    }
}