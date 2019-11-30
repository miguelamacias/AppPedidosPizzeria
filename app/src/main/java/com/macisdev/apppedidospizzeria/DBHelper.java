package com.macisdev.apppedidospizzeria;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {
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

            //Creates the sizes table
            db.execSQL("CREATE TABLE sizes (size TEXT PRIMARY KEY)");

            //Creates the pizzas_sizes table
            db.execSQL("CREATE TABLE pizzas_sizes(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "pizza_id INTEGER, " +
                    "size_id TEXT," +
                    "price REAL," +
                    "UNIQUE(pizza_id, size_id))");

            //Creates the pizzas_ingredients table
            db.execSQL("CREATE TABLE pizzas_ingredients(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "pizza_id INTEGER, " +
                    "ingredient TEXT," +
                    "UNIQUE(pizza_id, ingredient))");
        }

        //Populates the tables
        populatePizzasTable(db, currentDbVersion);
        populateSizesTable(db, currentDbVersion);
        populatePizzaSizeTable(db, currentDbVersion);
        populatePizzasIngredientsTable(db, currentDbVersion);


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

    private void populatePizzasIngredientsTable(SQLiteDatabase db, int dbVersion) {
        if (dbVersion < 1) {
            insertPizzaIngredientRow(db, 1, context.getString(R.string.beef));
            insertPizzaIngredientRow(db, 1, context.getString(R.string.chicken));
            insertPizzaIngredientRow(db, 1, context.getString(R.string.bacon));
            insertPizzaIngredientRow(db, 1, context.getString(R.string.cheese_mix));
            insertPizzaIngredientRow(db, 2, context.getString(R.string.beef));
            insertPizzaIngredientRow(db, 3, context.getString(R.string.cheese_mix));
            insertPizzaIngredientRow(db, 4, context.getString(R.string.bbq_sauce));
            insertPizzaIngredientRow(db, 4, context.getString(R.string.chicken));
            insertPizzaIngredientRow(db, 4, context.getString(R.string.bacon));
            insertPizzaIngredientRow(db, 5, context.getString(R.string.carbonara_sauce));
            insertPizzaIngredientRow(db, 5, context.getString(R.string.bacon));
            insertPizzaIngredientRow(db, 6, context.getString(R.string.ham));
            insertPizzaIngredientRow(db, 6, context.getString(R.string.pepperoni));
            insertPizzaIngredientRow(db, 6, context.getString(R.string.pork_meat));
            insertPizzaIngredientRow(db, 7, context.getString(R.string.mushroom));
            insertPizzaIngredientRow(db, 7, context.getString(R.string.onion));
            insertPizzaIngredientRow(db, 7, context.getString(R.string.spicy_sausage));
            insertPizzaIngredientRow(db, 8, context.getString(R.string.ham));
            insertPizzaIngredientRow(db, 8, context.getString(R.string.pineapple));
        }
    }
    private void populatePizzaSizeTable(SQLiteDatabase db, int dbVersion) {
        if (dbVersion < 1) {
            insertPizzaSizeRow(db, 1, context.getString(R.string.size_medium), 8.00);
            insertPizzaSizeRow(db, 1, context.getString(R.string.size_big), 17.00);
            insertPizzaSizeRow(db, 2, context.getString(R.string.size_medium), 6.50);
            insertPizzaSizeRow(db, 2, context.getString(R.string.size_big), 14.50);
            insertPizzaSizeRow(db, 3, context.getString(R.string.size_medium), 6.70);
            insertPizzaSizeRow(db, 3, context.getString(R.string.size_big), 14.80);
            insertPizzaSizeRow(db, 4, context.getString(R.string.size_medium), 7.30);
            insertPizzaSizeRow(db, 4, context.getString(R.string.size_big), 15.20);
            insertPizzaSizeRow(db, 5, context.getString(R.string.size_medium), 6.50);
            insertPizzaSizeRow(db, 5, context.getString(R.string.size_big), 14.80);
            insertPizzaSizeRow(db, 6, context.getString(R.string.size_medium), 7.30);
            insertPizzaSizeRow(db, 6, context.getString(R.string.size_big), 16.50);
            insertPizzaSizeRow(db, 7, context.getString(R.string.size_medium), 6.80);
            insertPizzaSizeRow(db, 7, context.getString(R.string.size_big), 14.50);
            insertPizzaSizeRow(db, 8, context.getString(R.string.size_medium), 6.50);
            insertPizzaSizeRow(db, 8, context.getString(R.string.size_big), 13.50);
        }
    }

    private void populateSizesTable(SQLiteDatabase db, int dbVersion) {
        if(dbVersion < 1) {
            //Insert medium size
            ContentValues values = new ContentValues();
            values.put("size", context.getString(R.string.size_medium));
            db.insertOrThrow("sizes",null, values);

            //Insert big size
            values.clear();
            values.put("size", context.getString(R.string.size_big));
            db.insertOrThrow("sizes",null, values);
        }
    }

    private void insertPizzaRow(SQLiteDatabase db, int id, String name, String ingredients) {
        ContentValues values = new ContentValues();
        values.put("_id", id);
        values.put("name", name);
        values.put("ingredients", ingredients);

        db.insertOrThrow("pizzas", null, values);
    }

    private void insertPizzaSizeRow(SQLiteDatabase db, int pizza_id, String size_id, double price) {
        ContentValues values = new ContentValues();
        values.put("pizza_id", pizza_id);
        values.put("size_id", size_id);
        values.put("price", price);

        db.insertOrThrow("pizzas_sizes", null, values);
    }

    private void insertPizzaIngredientRow(SQLiteDatabase db, int pizza_id, String ingredient) {
        ContentValues values = new ContentValues();
        values.put("pizza_id", pizza_id);
        values.put("ingredient", ingredient);

        db.insertOrThrow("pizzas_ingredients", null, values);
    }
}
