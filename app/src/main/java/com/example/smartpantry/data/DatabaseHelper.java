package com.example.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PANTRY = "pantry_items";
    private static final String TABLE_RECIPES = "recipes";
    private static final String TABLE_INGREDIENTS = "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, " +
                "expiry_date TEXT)");
        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, required_quantity REAL NOT NULL, unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");

        seedRecipes(db);
        seedPantry(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry_items");
        onCreate(db);
    }

    public long insertPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = pantryValues(item);
        return db.insert(TABLE_PANTRY, null, values);
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_PANTRY, pantryValues(item), "id = ?",
                new String[]{String.valueOf(item.getId())});
    }

    public int deletePantryItem(int id) {
        return getWritableDatabase().delete(TABLE_PANTRY, "id = ?",
                new String[]{String.valueOf(id)});
    }

    public PantryItem getPantryItem(int id) {
        Cursor cursor = getReadableDatabase().query(TABLE_PANTRY, null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return pantryFromCursor(cursor);
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(TABLE_PANTRY, null, null, null,
                null, null, "name COLLATE NOCASE ASC");
        try {
            while (cursor.moveToNext()) {
                items.add(pantryFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }
        return items;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(TABLE_RECIPES, null, null, null,
                null, null, "name COLLATE NOCASE ASC");
        try {
            while (cursor.moveToNext()) {
                Recipe recipe = recipeFromCursor(cursor);
                loadIngredients(recipe);
                recipes.add(recipe);
            }
        } finally {
            cursor.close();
        }
        return recipes;
    }

    public Recipe getRecipe(int id) {
        Cursor cursor = getReadableDatabase().query(TABLE_RECIPES, null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                Recipe recipe = recipeFromCursor(cursor);
                loadIngredients(recipe);
                return recipe;
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    private void loadIngredients(Recipe recipe) {
        Cursor cursor = getReadableDatabase().query(TABLE_INGREDIENTS, null, "recipe_id = ?",
                new String[]{String.valueOf(recipe.getId())}, null, null, "id ASC");
        try {
            while (cursor.moveToNext()) {
                recipe.addIngredient(new RecipeIngredient(
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("required_quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit"))));
            }
        } finally {
            cursor.close();
        }
    }

    private ContentValues pantryValues(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry_date", item.getExpiryDate());
        return values;
    }

    private PantryItem pantryFromCursor(Cursor cursor) {
        return new PantryItem(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                cursor.getString(cursor.getColumnIndexOrThrow("expiry_date")));
    }

    private Recipe recipeFromCursor(Cursor cursor) {
        return new Recipe(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("steps")));
    }

    private void seedPantry(SQLiteDatabase db) {
        insertSeedPantry(db, "Eggs", 6, "pieces", "2026-10-05");
        insertSeedPantry(db, "Bread", 4, "slices", "2026-09-28");
        insertSeedPantry(db, "Tomatoes", 500, "g", "2026-09-30");
        insertSeedPantry(db, "Onion", 2, "pieces", "2026-10-02");
        insertSeedPantry(db, "Cheese", 200, "g", "2026-10-08");
        insertSeedPantry(db, "Pasta", 500, "g", "2027-01-12");
        insertSeedPantry(db, "Milk", 1, "l", "2026-09-26");
        insertSeedPantry(db, "Rice", 1, "kg", "2027-02-20");
    }

    private void insertSeedPantry(SQLiteDatabase db, String name, double quantity,
                                  String unit, String expiryDate) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);
        db.insert(TABLE_PANTRY, null, values);
    }

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Cheese Omelette",
                "Whisk eggs. Cook them in a warm pan, add cheese and fold when set.",
                new String[][]{{"eggs", "2", "pieces"}, {"cheese", "50", "g"},
                        {"onion", "0.25", "pieces"}});
        addRecipe(db, "Tomato Toast",
                "Toast the bread and top it with sliced tomatoes and a little cheese.",
                new String[][]{{"bread", "2", "slices"}, {"tomato", "150", "g"},
                        {"cheese", "30", "g"}});
        addRecipe(db, "Tomato Pasta",
                "Boil pasta. Cook tomatoes and onion together, then mix with the pasta.",
                new String[][]{{"pasta", "200", "g"}, {"tomatoes", "250", "g"},
                        {"onion", "0.5", "pieces"}});
        addRecipe(db, "Creamy Pasta",
                "Boil pasta. Warm milk with cheese, then combine with the cooked pasta.",
                new String[][]{{"pasta", "200", "g"}, {"milk", "250", "ml"},
                        {"cheese", "60", "g"}});
        addRecipe(db, "Egg Fried Rice",
                "Scramble the eggs, add cooked rice and stir until hot.",
                new String[][]{{"eggs", "2", "pieces"}, {"rice", "300", "g"},
                        {"onion", "0.25", "pieces"}});
        addRecipe(db, "Grilled Cheese",
                "Place cheese between bread slices and toast both sides until golden.",
                new String[][]{{"bread", "2", "slices"}, {"cheese", "70", "g"}});
        addRecipe(db, "French Toast",
                "Dip bread in whisked egg and milk, then cook both sides in a pan.",
                new String[][]{{"bread", "2", "slices"}, {"eggs", "2", "pieces"},
                        {"milk", "100", "ml"}});
        addRecipe(db, "Tomato Egg Scramble",
                "Cook tomatoes, add beaten eggs, and stir until the eggs are cooked.",
                new String[][]{{"tomatoes", "200", "g"}, {"eggs", "2", "pieces"},
                        {"onion", "0.25", "pieces"}});
        addRecipe(db, "Cheesy Tomato Pasta",
                "Cook pasta and tomatoes, then finish with grated cheese.",
                new String[][]{{"pasta", "250", "g"}, {"tomato", "200", "g"},
                        {"cheese", "80", "g"}});
        addRecipe(db, "Rice and Tomato Bowl",
                "Warm the rice, fold through cooked tomatoes and diced onion.",
                new String[][]{{"rice", "250", "g"}, {"tomatoes", "150", "g"},
                        {"onion", "0.25", "pieces"}});
        addRecipe(db, "Milk Rice Pudding",
                "Simmer rice in milk until soft and creamy.",
                new String[][]{{"rice", "150", "g"}, {"milk", "500", "ml"}});
        addRecipe(db, "Tomato Cheese Sandwich",
                "Layer tomatoes and cheese between bread and toast until warm.",
                new String[][]{{"bread", "2", "slices"}, {"tomatoes", "100", "g"},
                        {"cheese", "50", "g"}});
        addRecipe(db, "Simple Egg Sandwich",
                "Boil or scramble eggs and place them inside the bread.",
                new String[][]{{"bread", "2", "slices"}, {"eggs", "2", "pieces"}});
        addRecipe(db, "Pasta Frittata",
                "Mix cooked pasta with eggs and cheese, then cook until firm.",
                new String[][]{{"pasta", "150", "g"}, {"eggs", "3", "pieces"},
                        {"cheese", "50", "g"}});
        addRecipe(db, "Tomato Rice Soup",
                "Simmer tomatoes, rice, onion, and milk until the rice is tender.",
                new String[][]{{"tomatoes", "200", "g"}, {"rice", "80", "g"},
                        {"onion", "0.25", "pieces"}, {"milk", "100", "ml"}});
        addRecipe(db, "Cheese and Onion Toast",
                "Top bread with onion and cheese, then toast until bubbling.",
                new String[][]{{"bread", "2", "slices"}, {"onion", "0.25", "pieces"},
                        {"cheese", "60", "g"}});
        addRecipe(db, "One-Pot Tomato Rice",
                "Cook rice with tomatoes and onion in one covered pot.",
                new String[][]{{"rice", "250", "g"}, {"tomatoes", "250", "g"},
                        {"onion", "0.5", "pieces"}});
        addRecipe(db, "Creamy Tomato Pasta",
                "Cook tomatoes, stir in milk and cheese, then toss with pasta.",
                new String[][]{{"pasta", "200", "g"}, {"tomatoes", "200", "g"},
                        {"milk", "150", "ml"}, {"cheese", "40", "g"}});
    }

    private void addRecipe(SQLiteDatabase db, String name, String steps,
                           String[][] ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("steps", steps);
        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        for (String[] ingredient : ingredients) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("recipe_id", recipeId);
            ingredientValues.put("name", ingredient[0]);
            ingredientValues.put("required_quantity", Double.parseDouble(ingredient[1]));
            ingredientValues.put("unit", ingredient[2]);
            db.insert(TABLE_INGREDIENTS, null, ingredientValues);
        }
    }
}
