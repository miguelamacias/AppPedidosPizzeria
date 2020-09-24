package com.macisdev.apppedidospizzeria;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class DrinksListFragment extends ListFragment {
    //the cursor and Database are declared here so they can be closed from onDestroy
    private Cursor cursor;
    private SQLiteDatabase db;

    interface PizzaListInterface {
        void pizzaClicked(int id);
    }

    private PizzaListInterface communicationInterface;

    public DrinksListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //loads pizza list from DB
        DBHelper dbHelper = new DBHelper(inflater.getContext());
        db = dbHelper.getReadableDatabase();
        //gets the pizza list from the DB
        cursor = db.rawQuery("SELECT _id, name, description FROM products WHERE type = ?", new String[] {DBHelper.typeDrink});

        //Sets the list that contains the results from the BD
        setListAdapter(new SimpleCursorAdapter(inflater.getContext(),
                android.R.layout.two_line_list_item,
                cursor,
                new String[]{"name", "description"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    //Gets the activity in which the fragment is been shown
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.communicationInterface = (PizzaListInterface) context;
    }

    //called when an item from the list is clicked
    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        if (communicationInterface != null) {
            //it calls the method in the parent activity
            communicationInterface.pizzaClicked((int)id);//this ID contains the _id of the pizza in the DB table
        }
    }
}
