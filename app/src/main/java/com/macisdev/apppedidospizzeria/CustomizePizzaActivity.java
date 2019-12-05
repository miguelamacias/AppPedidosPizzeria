package com.macisdev.apppedidospizzeria;

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

import java.util.ArrayList;

public class CustomizePizzaActivity extends AppCompatActivity {
    public static final int REMOVE_MODE = 1;
    public static final int ADD_MODE = 2;
    public static final String PIZZA_ID_KEY = "pizzaIdKey";
    public static final String EXTRA_MODE_KEY = "extraModeKey";
    public static final String PREVIOUS_SELECTION_KEY = "previousSelectionKey";

    private ListView ingredientsList;
    private String[] ingredientsArray;

    private int mode;
    private int pizzaId;
    private String previousSelection;

    private Cursor ingredientsCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_pizza);
        ingredientsList = findViewById(R.id.ingredients_list);

        //Getting the extra info from the Intent
        mode = getIntent().getIntExtra(EXTRA_MODE_KEY, 2);
        pizzaId = getIntent().getIntExtra(PIZZA_ID_KEY, 0);
        previousSelection = getIntent().getStringExtra(PREVIOUS_SELECTION_KEY);
        if (previousSelection == null) {
            previousSelection = "";
        }
        //Loads one ingredients list acording to the selected mode
        switch (mode) {
            case REMOVE_MODE:
                //Loads the list of ingredients from the database
                SQLiteDatabase db = new DBHelper(this).getReadableDatabase();

                ingredientsCursor = db.rawQuery("SELECT _id, ingredient FROM pizzas_ingredients WHERE pizza_id = ?",
                        new String[]{String.valueOf(pizzaId)});

                ingredientsList.setAdapter(new SimpleCursorAdapter(this,
                        android.R.layout.simple_list_item_multiple_choice,
                        ingredientsCursor,
                        new String[]{"ingredient"},
                        new int[]{android.R.id.text1},
                        0));
                break;
            case ADD_MODE:
                ingredientsArray = getResources().getStringArray(R.array.extra_ingredients);
                ingredientsList.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_multiple_choice,
                        ingredientsArray));
                break;
        }
    }

    //when the ok button is pressed
    public void getSelectedIngredients(View v) {
        //get checked ingredients from the listview
        SparseBooleanArray checkedIngredients = ingredientsList.getCheckedItemPositions();
        //Stringbuilder to store and build the ingredinets string
        StringBuilder stringBuilder = new StringBuilder(previousSelection);

        if (mode == ADD_MODE) { //Always executes first
            int numberOfExtras = 0;
            stringBuilder.append("EXTRA: ");

            //iterate through the array of ingredients to get only the checked ones
            for (int i = 0; i < ingredientsArray.length; i++) {
                if (checkedIngredients.get(i)) {
                    stringBuilder.append(ingredientsArray[i]).append(" ");
                    numberOfExtras += 0;
                }
            }

            Intent deleteIngredientsIntent = new Intent(this, CustomizePizzaActivity.class);
            deleteIngredientsIntent.putExtra(EXTRA_MODE_KEY, REMOVE_MODE);
            deleteIngredientsIntent.putExtra(PREVIOUS_SELECTION_KEY, stringBuilder.toString());
            deleteIngredientsIntent.putExtra(PIZZA_ID_KEY, pizzaId);
            startActivity(deleteIngredientsIntent);
            //TODO implements the function to get the number of extras
            //pizzaDetailsIntent.putExtra(PizzaDetailsActivity.PIZZA_EXTRA_NUMBER_KEY, numberOfExtras);

        }

        if (mode == REMOVE_MODE) {
            //Converts the cursor to a String Arraylist to make it easier to work with its data
            ArrayList<String> ingredientsArray = new ArrayList<>();
            for (ingredientsCursor.moveToFirst(); !ingredientsCursor.isAfterLast(); ingredientsCursor.moveToNext()) {
                ingredientsArray.add(ingredientsCursor.getString(1));
            }
            stringBuilder.append("\nSIN: ");
            //gets a string with the deleted items
            for (int i = 0; i < ingredientsArray.size(); i++) {
                if (checkedIngredients.get(i)) {
                    stringBuilder.append(ingredientsArray.get(i)).append(" ");
                }
            }
            Intent pizzaDetailsIntent = new Intent(this, PizzaDetailsActivity.class);
            pizzaDetailsIntent.putExtra(PizzaDetailsActivity.PIZZA_ID_KEY, pizzaId);
            pizzaDetailsIntent.putExtra(PizzaDetailsActivity.PIZZA_EXTRA_TYPE_KEY, REMOVE_MODE);
            pizzaDetailsIntent.putExtra(PizzaDetailsActivity.PIZZA_EXTRA_INGREDIENTS_KEY, stringBuilder.toString());
            startActivity(pizzaDetailsIntent);
        }

    }
}
