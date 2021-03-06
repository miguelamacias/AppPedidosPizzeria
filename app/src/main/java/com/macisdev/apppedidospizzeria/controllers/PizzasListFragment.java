package com.macisdev.apppedidospizzeria.controllers;


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

import com.macisdev.apppedidospizzeria.R;
import com.macisdev.apppedidospizzeria.util.DBHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class PizzasListFragment extends ListFragment {

    interface PizzasListInterface {
        void productClicked(int id);
    }

    private PizzasListInterface communicationInterface;

    public PizzasListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //loads pizza list from DB
        DBHelper dbHelper = new DBHelper(inflater.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //gets the pizza list from the DB

        Cursor cursor = db.rawQuery("SELECT _id, name, description FROM products WHERE type = ?", new String[]{DBHelper.typePizza});

        //Sets the list to contain the results from the BD
        setListAdapter(new SimpleCursorAdapter(inflater.getContext(),
                R.layout.layout_listview_two_items,
                cursor,
                new String[]{"name", "description"},
                new int[]{R.id.tv_1, R.id.tv_2},
                0));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    //Gets the activity in which the fragment is being shown
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.communicationInterface = (PizzasListInterface) context;
    }

    //called when an item from the list is clicked
    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        if (communicationInterface != null) {
            //it calls the method in the parent activity
            communicationInterface.productClicked((int)id);//this ID contains the _id of the pizza in the DB table
        }
    }
}
