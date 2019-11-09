package com.macisdev.apppedidospizzeria;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pizzeria";
    private static final int DB_VERSION = 1;
    private final Context context;

    DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDB(db, 0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDB(db, oldVersion);
    }

    private void updateDB(SQLiteDatabase db, int currentDbVersion) {
        //Updates the DB incrementally
        if (currentDbVersion < 1) {
            //Creates the Pizzas table
            db.execSQL("CREATE TABLE pizzas (" +
                    "_id INTEGER PRIMARY KEY," +
                    "name TEXT," +
                    "ingredients TEXT)");
        }

        //Populates the table
        populatePizzasTable(db, currentDbVersion);
    }

    private void populatePizzasTable(SQLiteDatabase db, int dbVersion) {
        if (dbVersion < 1) {
            //initial demo pizzas, more can be added in following updates
            insertPizzaRow(db, 1, "Monster", context.getString(R.string.ingedients_monster));
            insertPizzaRow(db, 2, "Carne de Ternera", context.getString(R.string.ingedients_carneTernera));
            insertPizzaRow(db, 3, "4 Quesos", context.getString(R.string.ingedients_4Quesos));
            insertPizzaRow(db, 4, "Barbacoa", context.getString(R.string.ingedients_barbacoa));
            insertPizzaRow(db, 5, "Carbonara", context.getString(R.string.ingedients_carbonara));
            insertPizzaRow(db, 6, "Di Luigi", context.getString(R.string.ingedients_diLuigi));
            insertPizzaRow(db, 7, "Di Marco", context.getString(R.string.ingedients_diMarco));
            insertPizzaRow(db, 8, "Hawaiana", context.getString(R.string.ingedients_hawaiana));

        }
    }

    private void insertPizzaRow(SQLiteDatabase db, int id, String name, String ingredients) {
        ContentValues values = new ContentValues();
        values.put("_id", id);
        values.put("name", name);
        values.put("ingredients", ingredients);

        db.insertOrThrow("pizzas", null, values);
    }
}
