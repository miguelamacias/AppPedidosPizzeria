package com.macisdev.apppedidospizzeria.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.macisdev.apppedidospizzeria.R;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pizzaShop";
    private static final int DB_VERSION = 1;

    public static final String typePizza = "0";
    public static final String typeDrink = "3";
    public static final String typeStarter = "1";
    public static final String typeMainCourse = "2";
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
            //Pizzas
            insertProductRow(db, 1, context.getString(R.string.name_margherita), context.getString(R.string.ingredients_margherita), typePizza);
            insertProductRow(db, 2, context.getString(R.string.name_monster), context.getString(R.string.ingredients_monster), typePizza);
            insertProductRow(db, 3, context.getString(R.string.name_beef_meat), context.getString(R.string.ingredients_beef_meat), typePizza);
            insertProductRow(db, 4, context.getString(R.string.name_4_cheeses), context.getString(R.string.ingredients_4_cheeses), typePizza);
            insertProductRow(db, 5, context.getString(R.string.name_bbq_pizza), context.getString(R.string.ingredients_bbq), typePizza);
            insertProductRow(db, 6, context.getString(R.string.name_carbonara), context.getString(R.string.ingredients_carbonara), typePizza);
            insertProductRow(db, 7, context.getString(R.string.name_diLuigi), context.getString(R.string.ingredients_diLuigi), typePizza);
            insertProductRow(db, 8, context.getString(R.string.name_diMarco), context.getString(R.string.ingredients_diMarco), typePizza);
            insertProductRow(db, 9, context.getString(R.string.name_hawaiian), context.getString(R.string.ingredients_hawaiian), typePizza);
            insertProductRow(db, 10, context.getString(R.string.name_mixed), context.getString(R.string.ingredients_mixed), typePizza);
            insertProductRow(db, 11, context.getString(R.string.name_4_stagioni), context.getString(R.string.ingredients_4_stagioni), typePizza);
            insertProductRow(db, 12, context.getString(R.string.name_torino), context.getString(R.string.ingredients_torino), typePizza);
            insertProductRow(db, 13, context.getString(R.string.name_vegetarian), context.getString(R.string.ingredients_vegetarian), typePizza);
            insertProductRow(db, 14, context.getString(R.string.name_andalusian), context.getString(R.string.ingredients_andalusian), typePizza);

            //Starters
            insertProductRow(db,101, context.getString(R.string.name_goat_cheese), "", typeStarter);
            insertProductRow(db,102, context.getString(R.string.name_chips), "", typeStarter);
            insertProductRow(db, 103, context.getString(R.string.name_bacon_cheddar_chips), "", typeStarter);
            insertProductRow(db, 104, context.getString(R.string.name_bacon_cheddar_nachos), "", typeStarter);
            insertProductRow(db, 105, context.getString(R.string.name_chicken_nuggets), "", typeStarter);
            insertProductRow(db, 106, context.getString(R.string.name_bbq_chicken_wings), "", typeStarter);
            insertProductRow(db, 107, context.getString(R.string.name_fried_cheese), "", typeStarter);
            insertProductRow(db, 108, context.getString(R.string.name_sauces), context.getString(R.string.description_sauce), typeStarter);

            //Main courses
            insertProductRow(db,201, context.getString(R.string.name_carbonara_pasta), context.getString(R.string.description_pasta), typeMainCourse);
            insertProductRow(db,202, context.getString(R.string.name_bolognese), context.getString(R.string.description_pasta), typeMainCourse);
            insertProductRow(db,203, context.getString(R.string.name_cesar), context.getString(R.string.description_cesar), typeMainCourse);
            insertProductRow(db,204, context.getString(R.string.name_solomillo), context.getString(R.string.description_solomillo), typeMainCourse);
            insertProductRow(db,205, context.getString(R.string.name_burger), context.getString(R.string.description_burger)
                    , typeMainCourse);
            insertProductRow(db,206, context.getString(R.string.name_steak), context.getString(R.string.description_steak), typeMainCourse);
            insertProductRow(db,207, context.getString(R.string.name_serranito), context.getString(R.string.description_serranito), typeMainCourse);

            //Drinks
            insertProductRow(db, 301, context.getString(R.string.name_coke), context.getString(R.string.description_coke), typeDrink);
            insertProductRow(db, 302, context.getString(R.string.name_fanta), context.getString(R.string.description_fanta), typeDrink);
            insertProductRow(db, 303, context.getString(R.string.name_7up), "", typeDrink);
            insertProductRow(db, 304, context.getString(R.string.name_aquarius), context.getString(R.string.description_aquarius), typeDrink);
            insertProductRow(db, 305, context.getString(R.string.name_nestea), "", typeDrink);
            insertProductRow(db, 306, context.getString(R.string.name_beer), "", typeDrink);
            insertProductRow(db, 307, context.getString(R.string.name_fake_beer), "", typeDrink);
            insertProductRow(db, 308, context.getString(R.string.name_water), "", typeDrink);
        }
    }

    private void populateProductsIngredientsTable(SQLiteDatabase db, int dbVersion) {
        if (dbVersion < 1) {
            //pizza ingredients
            insertProductIngredientRow(db, 1, context.getString(R.string.mozzarella));
            insertProductIngredientRow(db, 2, context.getString(R.string.beef));
            insertProductIngredientRow(db, 2, context.getString(R.string.chicken));
            insertProductIngredientRow(db, 2, context.getString(R.string.bacon));
            insertProductIngredientRow(db, 2, context.getString(R.string.cheese_mix));
            insertProductIngredientRow(db, 3, context.getString(R.string.beef));
            insertProductIngredientRow(db, 4, context.getString(R.string.cheese_mix));
            insertProductIngredientRow(db, 5, context.getString(R.string.bbq_sauce));
            insertProductIngredientRow(db, 5, context.getString(R.string.chicken));
            insertProductIngredientRow(db, 5, context.getString(R.string.bacon));
            insertProductIngredientRow(db, 6, context.getString(R.string.carbonara_sauce));
            insertProductIngredientRow(db, 6, context.getString(R.string.bacon));
            insertProductIngredientRow(db, 7, context.getString(R.string.ham));
            insertProductIngredientRow(db, 7, context.getString(R.string.pepperoni));
            insertProductIngredientRow(db, 7, context.getString(R.string.pork_meat));
            insertProductIngredientRow(db, 8, context.getString(R.string.mushroom));
            insertProductIngredientRow(db, 8, context.getString(R.string.onion));
            insertProductIngredientRow(db, 8, context.getString(R.string.pepperoni));
            insertProductIngredientRow(db, 9, context.getString(R.string.ham));
            insertProductIngredientRow(db, 9, context.getString(R.string.pineapple));
            insertProductIngredientRow(db, 10, context.getString(R.string.ham));
            insertProductIngredientRow(db, 10, context.getString(R.string.beef));
            insertProductIngredientRow(db, 10, context.getString(R.string.green_pepper));
            insertProductIngredientRow(db, 11, context.getString(R.string.ham));
            insertProductIngredientRow(db, 11, context.getString(R.string.mushroom));
            insertProductIngredientRow(db, 11, context.getString(R.string.artichoke));
            insertProductIngredientRow(db, 11, context.getString(R.string.tuna));
            insertProductIngredientRow(db, 11, context.getString(R.string.anchovy));
            insertProductIngredientRow(db, 12, context.getString(R.string.tuna));
            insertProductIngredientRow(db, 13, context.getString(R.string.green_pepper));
            insertProductIngredientRow(db, 13, context.getString(R.string.onion));
            insertProductIngredientRow(db, 13, context.getString(R.string.mushroom));
            insertProductIngredientRow(db, 13, context.getString(R.string.artichoke));
            insertProductIngredientRow(db, 14, context.getString(R.string.ham));
            insertProductIngredientRow(db, 14, context.getString(R.string.olives));
            insertProductIngredientRow(db, 14, context.getString(R.string.bacon));

            //Main courses ingredients
            insertProductIngredientRow(db, 203, context.getString(R.string.parmesan));
            insertProductIngredientRow(db, 203, context.getString(R.string.chicken));
            insertProductIngredientRow(db, 203, context.getString(R.string.bacon));
            insertProductIngredientRow(db, 203, context.getString(R.string.toasted_bread));
            insertProductIngredientRow(db, 203, context.getString(R.string.cesar_sauce));

            insertProductIngredientRow(db, 205, context.getString(R.string.tomato));
            insertProductIngredientRow(db, 205, context.getString(R.string.onion));
            insertProductIngredientRow(db, 205, context.getString(R.string.cheddar));
            insertProductIngredientRow(db, 205, context.getString(R.string.lettuce));
            insertProductIngredientRow(db, 205, context.getString(R.string.bacon));
            insertProductIngredientRow(db, 205, context.getString(R.string.fried_egg));

            insertProductIngredientRow(db, 207, context.getString(R.string.green_pepper));
            insertProductIngredientRow(db, 207, context.getString(R.string.cured_ham));

            //Starters ingredients
            insertProductIngredientRow(db, 103, context.getString(R.string.bacon));
            insertProductIngredientRow(db, 103, context.getString(R.string.cheddar));
            insertProductIngredientRow(db, 104, context.getString(R.string.bacon));
            insertProductIngredientRow(db, 104, context.getString(R.string.cheddar));
        }
    }
    private void populateProductsSizeTable(SQLiteDatabase db, int dbVersion) {
        if (dbVersion < 1) {
            //Pizzas
            insertProductsSizeRow(db, 1, context.getString(R.string.size_medium_pizza), 5.50);
            insertProductsSizeRow(db, 1, context.getString(R.string.size_big_pizza), 11.60);
            insertProductsSizeRow(db, 2, context.getString(R.string.size_medium_pizza), 8.00);
            insertProductsSizeRow(db, 2, context.getString(R.string.size_big_pizza), 17.00);
            insertProductsSizeRow(db, 3, context.getString(R.string.size_medium_pizza), 6.50);
            insertProductsSizeRow(db, 3, context.getString(R.string.size_big_pizza), 14.50);
            insertProductsSizeRow(db, 4, context.getString(R.string.size_medium_pizza), 6.70);
            insertProductsSizeRow(db, 4, context.getString(R.string.size_big_pizza), 14.80);
            insertProductsSizeRow(db, 5, context.getString(R.string.size_medium_pizza), 7.30);
            insertProductsSizeRow(db, 5, context.getString(R.string.size_big_pizza), 15.20);
            insertProductsSizeRow(db, 6, context.getString(R.string.size_medium_pizza), 6.50);
            insertProductsSizeRow(db, 6, context.getString(R.string.size_big_pizza), 14.80);
            insertProductsSizeRow(db, 7, context.getString(R.string.size_medium_pizza), 7.30);
            insertProductsSizeRow(db, 7, context.getString(R.string.size_big_pizza), 16.50);
            insertProductsSizeRow(db, 8, context.getString(R.string.size_medium_pizza), 6.80);
            insertProductsSizeRow(db, 8, context.getString(R.string.size_big_pizza), 14.50);
            insertProductsSizeRow(db, 9, context.getString(R.string.size_medium_pizza), 6.50);
            insertProductsSizeRow(db, 9, context.getString(R.string.size_big_pizza), 13.50);
            insertProductsSizeRow(db, 10, context.getString(R.string.size_medium_pizza), 7.50);
            insertProductsSizeRow(db, 10, context.getString(R.string.size_big_pizza), 15.50);
            insertProductsSizeRow(db, 11, context.getString(R.string.size_medium_pizza), 7.30);
            insertProductsSizeRow(db, 11, context.getString(R.string.size_big_pizza), 15.50);
            insertProductsSizeRow(db, 12, context.getString(R.string.size_medium_pizza), 7.00);
            insertProductsSizeRow(db, 12, context.getString(R.string.size_big_pizza), 15.50);
            insertProductsSizeRow(db, 13, context.getString(R.string.size_medium_pizza), 6.50);
            insertProductsSizeRow(db, 13, context.getString(R.string.size_big_pizza), 14.00);
            insertProductsSizeRow(db, 14, context.getString(R.string.size_medium_pizza), 6.50);
            insertProductsSizeRow(db, 14, context.getString(R.string.size_big_pizza), 13.50);

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
            insertProductsSizeRow(db, 108, context.getString(R.string.ketchup), 1.10);
            insertProductsSizeRow(db, 108, context.getString(R.string.bbq), 1.10);

            //Main courses
            insertProductsSizeRow(db, 201, context.getString(R.string.spaghetti), 5.10);
            insertProductsSizeRow(db, 201, context.getString(R.string.penne), 5.10);
            insertProductsSizeRow(db, 201, context.getString(R.string.tagliatelle), 5.10);
            insertProductsSizeRow(db, 201, context.getString(R.string.fussili), 5.10);
            insertProductsSizeRow(db, 202, context.getString(R.string.spaghetti), 5.10);
            insertProductsSizeRow(db, 202, context.getString(R.string.penne), 5.10);
            insertProductsSizeRow(db, 202, context.getString(R.string.tagliatelle), 5.10);
            insertProductsSizeRow(db, 202, context.getString(R.string.fussili), 5.10);

            insertProductsSizeRow(db, 203, context.getString(R.string.size_standard), 6.50);

            insertProductsSizeRow(db, 204, context.getString(R.string.size_small_whisky), 3.00);
            insertProductsSizeRow(db, 204, context.getString(R.string.size_small_roquefort), 3.00);
            insertProductsSizeRow(db, 204, context.getString(R.string.size_big_whisky), 5.00);
            insertProductsSizeRow(db, 204, context.getString(R.string.size_big_roquefort), 5.00);


            insertProductsSizeRow(db, 205, context.getString(R.string.wo_chips), 4.50);
            insertProductsSizeRow(db, 205, context.getString(R.string.w_chips), 5.50);

            insertProductsSizeRow(db, 206, context.getString(R.string.chicken), 6.00);
            insertProductsSizeRow(db, 206, context.getString(R.string.pork_meat), 6.00);

            insertProductsSizeRow(db, 207, context.getString(R.string.chicken), 4.00);
            insertProductsSizeRow(db, 207, context.getString(R.string.pork_meat), 4.00);


            //Drinks
            insertProductsSizeRow(db, 301, context.getString(R.string.classic_can), 1.30);
            insertProductsSizeRow(db, 301, context.getString(R.string.classic_2l), 2.50);

            insertProductsSizeRow(db, 301, context.getString(R.string.zero_can), 1.30);
            insertProductsSizeRow(db, 301, context.getString(R.string.zero_2l), 2.50);

            insertProductsSizeRow(db, 302, context.getString(R.string.orange_can), 1.30);
            insertProductsSizeRow(db, 302, context.getString(R.string.orange_2l), 2.50);

            insertProductsSizeRow(db, 302, context.getString(R.string.lemon_can), 1.30);
            insertProductsSizeRow(db, 302, context.getString(R.string.lemon_2l), 2.50);

            insertProductsSizeRow(db, 303, context.getString(R.string.can), 1.30);
            insertProductsSizeRow(db, 303, context.getString(R.string.bottle2l), 2.50);

            insertProductsSizeRow(db, 304, context.getString(R.string.lemon_can), 1.30);
            insertProductsSizeRow(db, 304, context.getString(R.string.orange_can), 1.30);

            insertProductsSizeRow(db, 305, context.getString(R.string.can), 2.50);

            insertProductsSizeRow(db, 306, context.getString(R.string.can), 1.30);
            insertProductsSizeRow(db, 306, context.getString(R.string.bottle1l), 1.80);

            insertProductsSizeRow(db, 307, context.getString(R.string.can), 1.30);

            insertProductsSizeRow(db, 308, context.getString(R.string.bottle50cl), 1.00);
            insertProductsSizeRow(db, 308, context.getString(R.string.bottle15l), 1.60);
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
