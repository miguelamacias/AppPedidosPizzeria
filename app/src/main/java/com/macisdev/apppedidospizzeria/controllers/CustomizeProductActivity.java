package com.macisdev.apppedidospizzeria.controllers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.macisdev.apppedidospizzeria.R;
import com.macisdev.apppedidospizzeria.util.DBHelper;

import java.util.ArrayList;

public class CustomizeProductActivity extends AppCompatActivity {
    public static final int REMOVE_MODE = 1;
    public static final int ADD_MODE = 2;
    public static final String PRODUCT_ID_KEY = "pizzaIdKey";
    public static final String EXTRA_MODE_KEY = "extraModeKey";
    public static final String PREVIOUS_SELECTION_KEY = "previousSelectionKey";
    public static final String NUMBER_OF_EXTRAS_KEY = "NumberOfExtrasKey";

    private ListView ingredientsList;
    private String[] ingredientsArray;

    private int mode;
    private int productId;
    private int numberOfExtras;
    private String previousSelection;

    private Cursor ingredientsCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_product);
        ingredientsList = findViewById(R.id.ingredients_list);

        //Getting the extra info from the Intent
        mode = getIntent().getIntExtra(EXTRA_MODE_KEY, 2);
        productId = getIntent().getIntExtra(PRODUCT_ID_KEY, 0);
        numberOfExtras = getIntent().getIntExtra(NUMBER_OF_EXTRAS_KEY, 0);
        previousSelection = getIntent().getStringExtra(PREVIOUS_SELECTION_KEY);
        if (previousSelection == null) {
            previousSelection = "";
        }

        //Loads one ingredients list according to the selected mode
        switch (mode) {
            case REMOVE_MODE:
                this.setTitle(R.string.remove_ingredient);
                //Loads the list of ingredients from the database
                SQLiteDatabase db = new DBHelper(this).getReadableDatabase();

                ingredientsCursor = db.rawQuery("SELECT _id, ingredient FROM products_ingredients " +
                                "WHERE product_id = ?",
                        new String[]{String.valueOf(productId)});

                ingredientsList.setAdapter(new SimpleCursorAdapter(this,
                        android.R.layout.simple_list_item_multiple_choice,
                        ingredientsCursor,
                        new String[]{"ingredient"},
                        new int[]{android.R.id.text1},
                        0));
                break;
            case ADD_MODE:
                this.setTitle(R.string.add_ingredients);
                ingredientsArray = getResources().getStringArray(R.array.extra_ingredients);
                ingredientsList.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_multiple_choice,
                        ingredientsArray));
                break;
        }
    }

    //When the ok button is pressed
    public void getSelectedIngredients(View v) {
        //get checked ingredients from the listview
        SparseBooleanArray checkedIngredients = ingredientsList.getCheckedItemPositions();

        //Stringbuilder to store and build the ingredients string
        StringBuilder stringBuilder = new StringBuilder(previousSelection);

        if (mode == ADD_MODE) { //Always executes first
            int numberOfExtras = 0;

            //Iterate through the array of ingredients to get only the checked ones
            for (int i = 0; i < ingredientsArray.length; i++) {
                if (checkedIngredients.get(i)) {
                    //Append the header if hasn't been done before.
                    if (stringBuilder.length() < 1) {
                        stringBuilder.append("EXTRA: ");
                    }
                    stringBuilder.append(ingredientsArray[i]).append(", ");
                    numberOfExtras += 1;
                }
            }

            //Removes the last comma of the list of extra ingredients in case any has been chosen
            String listOfExtraIngredients = stringBuilder.toString();
            if (numberOfExtras > 0) {
                listOfExtraIngredients = listOfExtraIngredients.substring(0,
                        listOfExtraIngredients.length() - 2).concat(" ");
            }

            //Starts this activity in Remove Mode
            startActivity(newIntentDeleteIngredients(this,
                    listOfExtraIngredients,
                    productId, numberOfExtras));
        }

        if (mode == REMOVE_MODE) {
            //Converts the cursor to a String Arraylist to make it easier to work with its data
            ArrayList<String> ingredientsArray = new ArrayList<>();
            for (ingredientsCursor.moveToFirst(); !ingredientsCursor.isAfterLast(); ingredientsCursor.moveToNext()) {
                ingredientsArray.add(ingredientsCursor.getString(1));
            }
            //Boolean to check if the header have been added
            boolean headerAdded = false;
            //Gets a string with the deleted items
            for (int i = 0; i < ingredientsArray.size(); i++) {
                if (checkedIngredients.get(i)) {
                    if (!headerAdded) {
                        stringBuilder.append("\nSIN: ");
                        headerAdded = true;
                    }
                    stringBuilder.append(ingredientsArray.get(i)).append(", ");
                }
            }

            //Removes the last comma of the list
            String removedIngredients = stringBuilder.toString();
            if (headerAdded) {
                removedIngredients = removedIngredients.substring(0,
                        removedIngredients.length() - 2);
            }

            //Goes back to the productDetails activity
            startActivity(ProductDetailsActivity.newIntentFromCustomize(this,
                    productId,
                    REMOVE_MODE,
                    removedIngredients,
                    numberOfExtras));
        }
    }

    //Creates a intent to open this activity in addition Mode
    public static Intent newIntentAddIngredients(Context context, int productId) {
        Intent intent = new Intent(context, CustomizeProductActivity.class);
        intent.putExtra(EXTRA_MODE_KEY, ADD_MODE);
        intent.putExtra(PRODUCT_ID_KEY, productId);
        return intent;
    }

    //Creates a intent to open this same activity in remove Mode
    public static Intent newIntentDeleteIngredients(Context context, String previousSelection,
                                                    int productId, int numberOfExtras) {
        Intent intent = new Intent(context, CustomizeProductActivity.class);
        intent.putExtra(EXTRA_MODE_KEY, REMOVE_MODE);
        intent.putExtra(PREVIOUS_SELECTION_KEY, previousSelection);
        intent.putExtra(PRODUCT_ID_KEY, productId);
        intent.putExtra(NUMBER_OF_EXTRAS_KEY, numberOfExtras);
        return intent;
    }
}