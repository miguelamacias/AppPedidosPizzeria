package com.macisdev.apppedidospizzeria.controllers;

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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.macisdev.apppedidospizzeria.R;
import com.macisdev.apppedidospizzeria.model.OrderElement;
import com.macisdev.apppedidospizzeria.model.OrderSingleton;
import com.macisdev.apppedidospizzeria.util.DBHelper;

import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {
    public static final String PRODUCT_ID_KEY = "productIdKey";
    public static final String PRODUCT_EXTRA_INGREDIENTS_KEY = "productExtraIngredientsKey";
    public static final String PRODUCT_EXTRA_TYPE_KEY = "productExtraTypeKey";
    public static final String PRODUCT_EXTRA_NUMBER_KEY = "productExtraNumberKey";

    private static final Double MEDIUM_EXTRA_PRICE = 0.5;
    private static final Double BIG_EXTRA_PRICE = 1.0;

    //Used to save the user selection while they customize the product
    private static int spinnerSizeIndex = 0;
    private static int spinnerQuantityIndex = 0;

    private Spinner spinnerQuantity;
    private SQLiteDatabase db;
    private Cursor cursorNameDetails;
    private TextView tvTotalPrice;

    //Fields needed to get the info about the item ordered
    private int productID, numberOfExtras;
    private String productName, productSize;
    private double productPrice;
    private String productExtras;
    private Spinner spinnerSizes;


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

        //Checks if the customize button should be visible (the product needs to be customizable)
        Button btnCustomize = findViewById(R.id.btn_customize);
        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();
        Cursor ingredientsCursor = db.rawQuery(
                "SELECT _id, ingredient FROM products_ingredients WHERE product_id = ?",
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

        //Event listener for the floating button, using onClick from the layout breaks the app in older phones
        ExtendedFloatingActionButton addToOrderFloatingButton = findViewById(R.id.add_to_order_floating_button);
        addToOrderFloatingButton.setOnClickListener(e -> addProductToOrder(null));

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


        //We also need the sizes and their prices, it's easier getting them in a separate cursor
        final Cursor cursorSizePrice = db.query("products_sizes",
                new String[]{"_id", "size_id", "price"},
                "product_id = ?",
                new String[]{String.valueOf(selectedProductId)},
                null,
                null,
                "_id");

        SimpleCursorAdapter adapterSizePrice = new SimpleCursorAdapter(this,
                R.layout.layout_listview_two_items,
                cursorSizePrice,
                new String[]{"size_id", "price"},
                new int[]{R.id.tv_1, R.id.tv_2},
                0);

        //ViewBinder used to format the price shown in the Spinner correctly
        adapterSizePrice.setViewBinder((view, cursor, columnIndex) -> {
            if (columnIndex == 2) { //Price is the column #2 of the cursor
                double price = cursor.getDouble(columnIndex); //Getting the price as is stored in the db
                TextView textView = (TextView) view; //Making the generic view a textView
                //Formatting the price so it shows the currency sign.
                textView.setText(String.format(Locale.getDefault(),
                        "%.2f%s", price, getString(R.string.currency_sign)));
                return true;
            }
            return false;
        });

        //Loads the available sizes in the spinner
        spinnerSizes = findViewById(R.id.spinner_size);
        spinnerSizes.setAdapter(adapterSizePrice);

        //Attach a listener to the spinner to get the selected size
        spinnerSizes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tvSize = view.findViewById(R.id.tv_1);
                productSize = tvSize.getText().toString();
                refreshPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Updates the price when an option is chosen in the spinner
        spinnerQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Loads the spinners previous selection
        spinnerQuantity.setSelection(spinnerQuantityIndex);
        spinnerSizes.setSelection(spinnerSizeIndex);

        //Shows the current price
        refreshPrice();
    }

    //Method that triggers when the Customize pizza buttons is pressed
    public void customizeProduct(View v) {
        //Saves the current spinner selection
        spinnerQuantityIndex = spinnerQuantity.getSelectedItemPosition();
        spinnerSizeIndex = spinnerSizes.getSelectedItemPosition();

        //extra ingredients can be added only to certain products, but they can be usually removed
        if (productID > 100) {
            startActivity(CustomizeProductActivity.newIntentDeleteIngredients(this, "",
                    productID, 0));
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

    //Updates and show the current price
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursorNameDetails.close();
        db.close();
    }

    //Creates a new intent properly configured to open this activity after a customization has been done
    public static Intent newIntentFromCustomize(Context context, int productId, int extraType,
                                                String extraIngredients, int numberOfExtras) {
        Intent intent = new Intent(context, ProductDetailsActivity.class);
        intent.putExtra(PRODUCT_ID_KEY, productId);
        intent.putExtra(PRODUCT_EXTRA_TYPE_KEY, extraType);
        intent.putExtra(PRODUCT_EXTRA_INGREDIENTS_KEY, extraIngredients);
        intent.putExtra(PRODUCT_EXTRA_NUMBER_KEY, numberOfExtras);

        return intent;
    }
}