package com.macisdev.apppedidospizzeria;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    //Araylist that contains the elements to order
    public static ArrayList<OrderElement> orderelements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Loads main menu options
        ListView mainMenu = findViewById(R.id.main_menu);

        String[] mainMenuEntries = {getString(R.string.pizzas_menu),
                getString(R.string.appetizers),
                getString(R.string.drinks_menu),
                getString(R.string.show_order_summary)};

        mainMenu.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1 ,
                mainMenuEntries));

        mainMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(MainActivity.this, PizzasActivity.class));
                }

                if (position == 1) {
                    Toast.makeText(MainActivity.this, R.string.not_available, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, CustomizePizzaActivity.class));
                }

                if (position == 2) {
                    Toast.makeText(MainActivity.this, R.string.not_available, Toast.LENGTH_SHORT).show();
                }

                if (position == 3) { //Option that finalizes the order.
                    startActivity(new Intent(MainActivity.this, OrderSummaryActivity.class));
                }
            }
        });
    }
}
