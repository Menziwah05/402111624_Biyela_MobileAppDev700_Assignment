# Smart Pantry Manager — 5–7 Minute Demonstration Script

This script is a guide for recording the required narrated video. Record the
actual app and GitHub repository yourself; do not submit this text as the
video.

## 0:00–0:45 — GitHub walkthrough

1. Open the public GitHub repository.
2. Show the README and project folder.
3. Scroll through at least 10 meaningful commits.
4. Briefly explain that the commits show the project growing from database
   setup, to CRUD, to adapters, strict matching, UI, and final fixes.

## 0:45–3:30 — Live application demonstration

1. Launch the application and show the seeded pantry list.
2. Open **Add ingredient**.
3. Try to save with an empty name or invalid quantity and show validation.
4. Add an ingredient such as `Tomatoes`, quantity `500`, unit `g`.
5. Edit the new ingredient and change its quantity.
6. Delete the ingredient and confirm that the list updates.
7. Open **Recipes** and show the strict suggestions.
8. Open one recipe and show its complete ingredient list and method.
9. Remove or reduce one required ingredient in the pantry.
10. Return to **Recipes** and show that the recipe disappears because it no
    longer has every required ingredient in enough quantity.
11. Open **Settings**, toggle expiry alerts, and save.
12. Close and relaunch the app to show that the pantry data remains.

## 3:30–4:45 — Explain the Activity and navigation design

Point to the actual Java code and explain:

- `MainActivity` displays the pantry `RecyclerView`.
- `AddEditIngredientActivity` handles the form and validation.
- Explicit Intents pass `ingredient_id` and `recipe_id` between screens.
- `RecipeDetailActivity` loads the selected recipe from SQLite.

## 4:45–6:00 — Explain SQLite and the adapter

Show `DatabaseHelper.java` and explain:

- `onCreate()` creates the three tables and seeds the initial data.
- `insertPantryItem`, `updatePantryItem`, `getAllPantryItems`, and
  `deletePantryItem` provide CRUD.
- `PantryAdapter` maps database records into RecyclerView rows.

## 6:00–7:00 — Explain strict matching and database choice

Show `IngredientMatcher.java` and explain:

- Every recipe ingredient is checked.
- Missing ingredients reject the recipe.
- Insufficient quantities reject the recipe.
- Simple plural forms are normalized.
- Compatible units are converted before comparison.

Finish by explaining that SQLite was chosen because the app is local-first,
does not need a server or account system, and must persist pantry data between
sessions.
