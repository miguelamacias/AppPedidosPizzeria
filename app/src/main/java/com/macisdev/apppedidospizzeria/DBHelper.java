package com.macisdev.apppedidospizzeria;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pizzeria";
    private static final int DB_VERSION = 1;

    public static final String typePizza = "1";
    public static final String typeDrink = "2";
    public static final String typeStarter = "3";
    public static final String typeMainMeal = "4";
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
            db.execSQL("CREATE TABLE products (" +
                    "_id INTEGER PRIMARY KEY," +
                    "name TEXT," +
                    "description TEXT," +
                    "type INTEGER)");

            //Creates the sizes table
            db.execSQL("CREATE TABLE sizes (size TEXT PRIMARY KEY)");

            //Creates the pizzas_sizes table
            db.execSQL("CREATE TABLE products_sizes(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "product_id INTEGER, " +
                    "size_id TEXT," +
                    "price REAL," +
                    "UNIQUE(product_id, size_id))");

            //Creates the pizzas_ingredients table
            db.execSQL("CREATE TABLE products_ingredients(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "product_id INTEGER, " +
                    "ingredient TEXT," +
                    "UNIQUE(product_id, ingredient))");
        }

        //Populates the tables
        populateProductsTable(db, currentDbVersion);
        populateSizesTable(db, currentDbVersion);
        populatePizzaSizeTable(db, currentDbVersion);
        populatePizzasIngredientsTable(db, currentDbVersion);
    }

    private void populateProductsTable(SQLiteDatabase db, int dbVersion) {
        if (dbVersion < 1) {
            //initial demo pizzas, more can be added in following updates
            insertProductRow(db, 1, "Monster", context.getString(R.string.ingedients_monster), typePizza);
            insertProductRow(db, 2, "Carne de Ternera", context.getString(R.string.ingedients_carneTernera), typePizza);
            insertProductRow(db, 3, "4 Quesos", context.getString(R.string.ingedients_4Quesos), typePizza);
            insertProductRow(db, 4, "Barbacoa", context.getString(R.string.ingedients_barbacoa), typePizza);
            insertProductRow(db, 5, "Carbonara", context.getString(R.string.ingedients_carbonara), typePizza);
            insertProductRow(db, 6, "Di Luigi", context.getString(R.string.ingedients_diLuigi), typePizza);
            insertProductRow(db, 7, "Di Marco", context.getString(R.string.ingedients_diMarco), typePizza);
            insertProductRow(db, 8, "Hawaiana", context.getString(R.string.ingedients_hawaiana), typePizza);
            //Starters
            insertProductRow(db,101, "Rulo de cabra con nueces y miel", "", typeStarter);
            insertProductRow(db,102, "Patatas fritas", "", typeStarter);
            insertProductRow(db, 103, "Patatas con cheddar y bacon", "", typeStarter);
            insertProductRow(db, 104, "Nachos con cheddar y bacon", "", typeStarter);
            insertProductRow(db, 105, "Nuggets de pollo", "", typeStarter);
            insertProductRow(db, 106, "Alitas de pollo barbacoa", "", typeStarter);
            insertProductRow(db, 107, "Queso Frito", "", typeStarter);
            insertProductRow(db, 108, "Salsas", "Roque, Ali-Oli, Churrasco, Mayonesa, Rosa, Barbacoa", typeStarter);
        }
    }

    private void populatePizzasIngredientsTable(SQLiteDatabase db, int dbVersion) {
        if (dbVersion < 1) {
            //pizza ingredients
            insertProductIngredientRow(db, 1, context.getString(R.string.beef));
            insertProductIngredientRow(db, 1, context.getString(R.string.chicken));
            insertProductIngredientRow(db, 1, context.getString(R.string.bacon));
            insertProductIngredientRow(db, 1, context.getString(R.string.cheese_mix));
            insertProductIngredientRow(db, 2, context.getString(R.string.beef));
            insertProductIngredientRow(db, 3, context.getString(R.string.cheese_mix));
            insertProductIngredientRow(db, 4, context.getString(R.string.bbq_sauce));
            insertProductIngredientRow(db, 4, context.getString(R.string.chicken));
            insertProductIngredientRow(db, 4, context.getString(R.string.bacon));
            insertProductIngredientRow(db, 5, context.getString(R.string.carbonara_sauce));
            insertProductIngredientRow(db, 5, context.getString(R.string.bacon));
            insertProductIngredientRow(db, 6, context.getString(R.string.ham));
            insertProductIngredientRow(db, 6, context.getString(R.string.pepperoni));
            insertProductIngredientRow(db, 6, context.getString(R.string.pork_meat));
            insertProductIngredientRow(db, 7, context.getString(R.string.mushroom));
            insertProductIngredientRow(db, 7, context.getString(R.string.onion));
            insertProductIngredientRow(db, 7, context.getString(R.string.spicy_sausage));
            insertProductIngredientRow(db, 8, context.getString(R.string.ham));
            insertProductIngredientRow(db, 8, context.getString(R.string.pineapple));
            //Starters ingredients
            insertProductIngredientRow(db, 103, context.getString(R.string.bacon));
            insertProductIngredientRow(db, 103, context.getString(R.string.cheddar));
        }
    }
    private void populatePizzaSizeTable(SQLiteDatabase db, int dbVersion) {
        if (dbVersion < 1) {
            //Pizzas
            insertProductsSizeRow(db, 1, context.getString(R.string.size_medium_pizza), 8.00);
            insertProductsSizeRow(db, 1, context.getString(R.string.size_big_pizza), 17.00);
            insertProductsSizeRow(db, 2, context.getString(R.string.size_medium_pizza), 6.50);
            insertProductsSizeRow(db, 2, context.getString(R.string.size_big_pizza), 14.50);
            insertProductsSizeRow(db, 3, context.getString(R.string.size_medium_pizza), 6.70);
            insertProductsSizeRow(db, 3, context.getString(R.string.size_big_pizza), 14.80);
            insertProductsSizeRow(db, 4, context.getString(R.string.size_medium_pizza), 7.30);
            insertProductsSizeRow(db, 4, context.getString(R.string.size_big_pizza), 15.20);
            insertProductsSizeRow(db, 5, context.getString(R.string.size_medium_pizza), 6.50);
            insertProductsSizeRow(db, 5, context.getString(R.string.size_big_pizza), 14.80);
            insertProductsSizeRow(db, 6, context.getString(R.string.size_medium_pizza), 7.30);
            insertProductsSizeRow(db, 6, context.getString(R.string.size_big_pizza), 16.50);
            insertProductsSizeRow(db, 7, context.getString(R.string.size_medium_pizza), 6.80);
            insertProductsSizeRow(db, 7, context.getString(R.string.size_big_pizza), 14.50);
            insertProductsSizeRow(db, 8, context.getString(R.string.size_medium_pizza), 6.50);
            insertProductsSizeRow(db, 8, context.getString(R.string.size_big_pizza), 13.50);
            //Starters
            insertProductsSizeRow(db, 101, context.getString(R.string.size_standard), 4.00);
            insertProductsSizeRow(db, 102, context.getString(R.string.size_standard), 2.20);
            insertProductsSizeRow(db, 103, context.getString(R.string.size_standard), 4.50);
            insertProductsSizeRow(db, 104, context.getString(R.string.size_standard), 4.00);
            insertProductsSizeRow(db, 105, context.getString(R.string.size_6units), 2.50);
            insertProductsSizeRow(db, 105, context.getString(R.string.size_10units), 4.00);
            insertProductsSizeRow(db, 106, context.getString(R.string.size_6units), 2.50);
            insertProductsSizeRow(db, 106, context.getString(R.string.size_10units), 4.00);
            insertProductsSizeRow(db, 107, context.getString(R.string.size_standard), 2.50);
            insertProductsSizeRow(db, 101, context.getString(R.string.size_standard), 3.20);
            //TODO every sauce should be treated as a different size
            insertProductsSizeRow(db, 108, context.getString(R.string.size_standard), 1.10);
        }
    }

    private void populateSizesTable(SQLiteDatabase db, int dbVersion) {
        if(dbVersion < 1) {
            //Insert medium size
            ContentValues values = new ContentValues();
            values.put("size", context.getString(R.string.size_medium_pizza));
            db.insertOrThrow("sizes",null, values);

            //Insert big size
            values.clear();
            values.put("size", context.getString(R.string.size_big_pizza));
            db.insertOrThrow("sizes",null, values);

            //Insert sizes for non-pizza products
            values.clear();
            values.put("size", "Tapa: 6 unidades");
            db.insertOrThrow("sizes", null, values);

            values.clear();
            values.put("size", "Ración: 10 unidades");
            db.insertOrThrow("sizes", null, values);
        }
    }

    private void insertProductRow(SQLiteDatabase db, int id, String name, String ingredients, String type) {
        ContentValues values = new ContentValues();
        values.put("_id", id);
        values.put("name", name);
        values.put("description", ingredients);
        values.put("type", type);

        db.insertOrThrow("products", null, values);
    }

    private void insertProductsSizeRow(SQLiteDatabase db, int product_id, String size_id, double price) {
        ContentValues values = new ContentValues();
        values.put("product_id", product_id);
        values.put("size_id", size_id);
        values.put("price", price);

        db.insertOrThrow("products_sizes", null, values);
    }

    private void insertProductIngredientRow(SQLiteDatabase db, int product_id, String ingredient) {
        ContentValues values = new ContentValues();
        values.put("product_id", product_id);
        values.put("ingredient", ingredient);

        db.insertOrThrow("products_ingredients", null, values);
    }
}
