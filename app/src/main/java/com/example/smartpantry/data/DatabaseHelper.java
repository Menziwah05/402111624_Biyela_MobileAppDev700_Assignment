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
    private static final int DATABASE_VERSION = 2;

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
        seedSouthAfricanRecipes(db);
        seedPantry(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add the new recipe catalogue without deleting a user's existing pantry.
        if (oldVersion < 2) {
            seedSouthAfricanRecipes(db);
        }
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

    private void seedSouthAfricanRecipes(SQLiteDatabase db) {
        addRecipe(db, "Chakalaka",
                "Heat the oil and soften the onion, garlic, green pepper and grated carrots. " +
                        "Add tomatoes and curry powder and cook until the vegetables soften. " +
                        "Stir in the baked beans and sugar, then simmer until thick. Serve as " +
                        "a relish with pap, bread or braai food.",
                new String[][]{{"vegetable oil", "30", "ml"}, {"onion", "1", "pieces"},
                        {"garlic", "2", "cloves"}, {"green pepper", "1", "pieces"},
                        {"carrots", "2", "pieces"}, {"tomatoes", "300", "g"},
                        {"curry powder", "1.5", "tbsp"}, {"baked beans", "400", "g"},
                        {"sugar", "20", "g"}});

        addRecipe(db, "Pap and Boerewors",
                "Bring the water and salt to a boil. Slowly stir in the maize meal, cover " +
                        "and cook over low heat, stirring occasionally, until thick and cooked. " +
                        "Grill or pan-cook the boerewors until browned and cooked through. Serve " +
                        "the boerewors with pap.",
                new String[][]{{"water", "1500", "ml"}, {"maize meal", "500", "g"},
                        {"salt", "5", "g"}, {"boerewors", "500", "g"}});

        addRecipe(db, "Bobotie",
                "Soak the bread in some of the milk. Brown the lamb mince with onion, then " +
                        "stir in curry powder, raisins and chutney. Mix in the soaked bread " +
                        "and place in a baking dish. Beat the eggs with the remaining milk, " +
                        "pour over the mince and bake until the egg topping is set and golden.",
                new String[][]{{"lamb mince", "500", "g"}, {"onion", "1", "pieces"},
                        {"bread", "1", "slices"}, {"milk", "250", "ml"},
                        {"eggs", "2", "pieces"}, {"curry powder", "1", "tbsp"},
                        {"raisins", "50", "g"}, {"apricot chutney", "30", "g"}});

        addRecipe(db, "Chicken Bunny Chow",
                "Warm the oil and cook the onion and garlic until soft. Add curry powder and " +
                        "chicken and stir to coat. Add tomatoes, potatoes and water, then simmer " +
                        "until the chicken and potatoes are cooked and the curry is thick. Hollow " +
                        "out the bread loaf, fill it with curry and serve with the bread centre.",
                new String[][]{{"bread", "1", "loaf"}, {"chicken", "500", "g"},
                        {"onion", "1", "pieces"}, {"garlic", "2", "cloves"},
                        {"potatoes", "2", "pieces"}, {"tomatoes", "250", "g"},
                        {"curry powder", "1", "tbsp"}, {"vegetable oil", "15", "ml"},
                        {"water", "250", "ml"}});

        addRecipe(db, "Vetkoek (Amagwinya)",
                "Mix the flour, yeast, sugar and salt. Add warm water and knead to a soft " +
                        "dough. Cover and leave in a warm place until doubled in size. Shape " +
                        "into balls and deep-fry in hot oil until puffed and golden, turning " +
                        "once. Drain and serve plain or filled with savoury mince.",
                new String[][]{{"flour", "500", "g"}, {"instant yeast", "7", "g"},
                        {"sugar", "15", "g"}, {"salt", "5", "g"},
                        {"water", "300", "ml"}, {"vegetable oil", "1000", "ml"}});

        addRecipe(db, "Malva Pudding",
                "Beat the eggs and sugar until pale, then mix in apricot jam, milk and vinegar. " +
                        "Fold in flour and bicarbonate of soda and bake until springy. Heat the " +
                        "cream, butter and sugar for the sauce, pour it over the hot pudding, " +
                        "and allow it to soak in before serving.",
                new String[][]{{"eggs", "2", "pieces"}, {"sugar", "300", "g"},
                        {"apricot jam", "30", "g"}, {"milk", "125", "ml"},
                        {"vinegar", "15", "ml"}, {"flour", "250", "g"},
                        {"bicarbonate of soda", "5", "g"}, {"cream", "250", "ml"},
                        {"butter", "100", "g"}});

        addRecipe(db, "Milk Tart (Melktert)",
                "Rub the butter into the flour and sugar, add one egg and bring together into " +
                        "a pastry. Press into a tart tin and bake until lightly golden. Warm the " +
                        "milk. Whisk the remaining eggs with flour and sugar, gradually whisk " +
                        "in the warm milk, then cook gently until thick. Pour into the crust, " +
                        "dust with cinnamon and chill until set.",
                new String[][]{{"flour", "300", "g"}, {"butter", "125", "g"},
                        {"sugar", "200", "g"}, {"eggs", "4", "pieces"},
                        {"milk", "750", "ml"}, {"cinnamon", "1", "tsp"}});

        addRecipe(db, "Cape Malay Tomato Bredie",
                "Brown the lamb in a heavy pot. Add onion and cook until softened. Add tomatoes " +
                        "and a little water, cover and simmer slowly until the meat is tender. " +
                        "Add potatoes partway through cooking and simmer until soft. Season and " +
                        "serve hot, traditionally with rice.",
                new String[][]{{"lamb", "600", "g"}, {"onion", "1", "pieces"},
                        {"tomatoes", "500", "g"}, {"potatoes", "3", "pieces"},
                        {"vegetable oil", "15", "ml"}, {"water", "250", "ml"},
                        {"salt", "5", "g"}, {"black pepper", "2", "g"}});
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
