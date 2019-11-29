package com.macisdev.apppedidospizzeria;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class CustomizePizzaActivity extends AppCompatActivity {
    ListView ingredientsList;
    String[] ingredientsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_pizza);

        ingredientsList = findViewById(R.id.ingredients_list);
        ingredientsArray = getResources().getStringArray(R.array.extra_ingredients);

        ingredientsList.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice,
                ingredientsArray));

    }

    public void getSelectedIngredients(View v) {
        //get checked ingredients form the listview
        SparseBooleanArray checkedIngredients = ingredientsList.getCheckedItemPositions();

        StringBuffer buffer = new StringBuffer("Extras: ");
        //iterate through the array of ingredients to get only the checked ones
        for (int i = 0; i < ingredientsArray.length; i++) {
            if (checkedIngredients.get(i)) {
                Log.d("INGREDIENTES_EXTRAS", ingredientsArray[i]);
                buffer.append(ingredientsArray[i]);
            }
        }

        PizzaDetailsActivity.setPizzaExtras(buffer.toString());
        finish();


    }
}
