package com.macisdev.apppedidospizzeria.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.macisdev.apppedidospizzeria.R;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pizzeria";
    private static final int DB_VERSION = 1;

    public static final String typePizza = "1";
    public static final String typeDrink = "2";
    public static final String typeStarter = "3";
    //public static final String typeMainMeal = "4";
    private final Context context;

    public DBHelper(Context context) {
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
            //Creates the products table
            db.execSQL("CREATE TABLE products (" +
                    "_id INTEGER PRIMARY KEY," +
                    "name TEXT," +
                    "description TEXT," +
                    "type INTEGER)");

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

            //creates the cities table
            db.execSQL("CREATE TABLE cities(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "zipCode TEXT," +
                    "deliveryPrice REAL)");
        }

        //Populates the tables
        populateProductsTable(db, currentDbVersion);
        populateProductsSizeTable(db, currentDbVersion);
        populateProductsIngredientsTable(db, currentDbVersion);
        populateCitiesTable(db, currentDbVersion);
    }

    private void populateProductsTable(SQLiteDatabase db, int dbVersion) {
        if (dbVersion < 1) {
            //initial demo pizzas, more can be added in following updates
            insertProductRow(db, 1, "Monster", context.getString(R.string.ingredients_monster), typePizza);
            insertProductRow(db, 2, "Carne de Ternera", context.getString(R.string.ingredients_carneTernera), typePizza);
            insertProductRow(db, 3, "4 Quesos", context.getString(R.string.ingredients_4Quesos), typePizza);
            insertProductRow(db, 4, "Barbacoa", context.getString(R.string.ingredients_barbacoa), typePizza);
            insertProductRow(db, 5, "Carbonara", context.getString(R.string.ingredients_carbonara), typePizza);
            insertProductRow(db, 6, "Di Luigi", context.getString(R.string.ingredients_diLuigi), typePizza);
            insertProductRow(db, 7, "Di Marco", context.getString(R.string.ingredients_diMarco), typePizza);
            insertProductRow(db, 8, "Hawaiana", context.getString(R.string.ingredients_hawaiana), typePizza);
            //Starters
            insertProductRow(db,101, "Rulo de cabra con nueces y miel", "", typeStarter);
            insertProductRow(db,102, "Patatas fritas", "", typeStarter);
            insertProductRow(db, 103, "Patatas con cheddar y bacon", "", typeStarter);
            insertProductRow(db, 104, "Nachos con cheddar y bacon", "", typeStarter);
            insertProductRow(db, 105, "Nuggets de pollo", "", typeStarter);
            insertProductRow(db, 106, "Alitas de pollo barbacoa", "", typeStarter);
            insertProductRow(db, 107, "Queso Frito", "", typeStarter);
            insertProductRow(db, 108, "Salsas", "Roque, Ali-Oli, Churrasco, Mayonesa, Rosa, Barbacoa", typeStarter);
            //Drinks
            insertProductRow(db, 301, "Coca-Cola", "Normal, Zero", typeDrink);
            insertProductRow(db, 302, "Fanta", "Naranja, Limón", typeDrink);
            insertProductRow(db, 303, "7Up", "", typeDrink);
            insertProductRow(db, 304, "Cruzcampo", "", typeDrink);
            insertProductRow(db, 305, "Cruzcampo 0,0%", "", typeDrink);
            insertProductRow(db, 306, "Agua Mineral", "", typeDrink);
        }
    }

    private void populateProductsIngredientsTable(SQLiteDatabase db, int dbVersion) {
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
    private void populateProductsSizeTable(SQLiteDatabase db, int dbVersion) {
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
            insertProductsSizeRow(db, 107, context.getString(R.string.size_standard), 3.20);
            insertProductsSizeRow(db, 108, context.getString(R.string.roquefort), 1.10);
            insertProductsSizeRow(db, 108, context.getString(R.string.garlic_sauce), 1.10);
            insertProductsSizeRow(db, 108, context.getString(R.string.steak_sauce), 1.10);
            insertProductsSizeRow(db, 108, context.getString(R.string.mayonnaise), 1.10);
            insertProductsSizeRow(db, 108, context.getString(R.string.cocktail_sauce), 1.10);
            insertProductsSizeRow(db, 108, context.getString(R.string.bbq), 1.10);
            //Drinks
            insertProductsSizeRow(db, 301, "Normal Lata 33cl", 1.30);
            insertProductsSizeRow(db, 301, "Normal Botella 50cl", 1.80);
            insertProductsSizeRow(db, 301, "Normal Botella 2L", 2.50);

            insertProductsSizeRow(db, 301, "Zero Lata 33cl", 1.30);
            insertProductsSizeRow(db, 301, "Zero Botella 50cl", 1.80);
            insertProductsSizeRow(db, 301, "Zero Botella 2L", 2.50);

            insertProductsSizeRow(db, 302, "Naranja Lata 33cl", 1.30);
            insertProductsSizeRow(db, 302, "Naranja Botella 50cl", 1.80);
            insertProductsSizeRow(db, 302, "Naranja Botella 2L", 2.50);

            insertProductsSizeRow(db, 302, "Limón Lata 33cl", 1.30);
            insertProductsSizeRow(db, 302, "Limón Botella 50cl", 1.80);
            insertProductsSizeRow(db, 302, "Limón Botella 2L", 2.50);

            insertProductsSizeRow(db, 303, "Lata 33cl", 1.30);
            insertProductsSizeRow(db, 303, "Botella 50cl", 1.80);
            insertProductsSizeRow(db, 303, "Botella 2L", 2.50);

            insertProductsSizeRow(db, 304, "Lata 33cl", 1.30);
            insertProductsSizeRow(db, 304, "Litrona 1L", 1.60);

            insertProductsSizeRow(db, 305, "Lata 33cl", 1.30);

            insertProductsSizeRow(db, 306, "Botella 50cl", 1.00);
            insertProductsSizeRow(db, 306, "Botella 1,5L", 1.60);
        }
    }

    private void populateCitiesTable(SQLiteDatabase db, int dbVersion) {
        if (dbVersion < 1) {
            insertCitiesRow(db, "Gelves", "41120", 0);
            insertCitiesRow(db, "Coria", "41100", 1);
            insertCitiesRow(db, "Palomares", "41132", 2);
            insertCitiesRow(db, "Mairena", "41125", 2.5);
            insertCitiesRow(db, "San Juan", "41142", 2);

        }
    }

    private void insertProductRow(SQLiteDatabase db, int id, String name, String description, String type) {
        ContentValues values = new ContentValues();
        values.put("_id", id);
        values.put("name", name);
        values.put("description", description);
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

    private void insertCitiesRow(SQLiteDatabase db, String name, String zipCode, double deliveryPrice) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("zipCode", zipCode);
        values.put("deliveryPrice", deliveryPrice);

        db.insertOrThrow("cities", null, values);
    }

}
