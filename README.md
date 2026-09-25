# Smart Pantry Manager

Smart Pantry Manager is a Java Android application for reducing food waste.
It stores the ingredients a user already has, then suggests only recipes that
can be made immediately from those ingredients.

## Assignment requirements covered

- Java Android Studio project.
- Five screens: Pantry, Add/Edit Ingredient, Suggested Recipes, Recipe Detail,
  and Settings.
- SQLite persistence through `SQLiteOpenHelper`.
- Full pantry CRUD: create, read, update, and delete.
- Custom `RecyclerView.Adapter` for pantry items and recipes.
- Intent navigation between Activities.
- Form validation for name, quantity, and unit.
- 18 pre-loaded recipes with ingredients and preparation steps.
- Strict matching checks every recipe ingredient and required quantity.
- Ingredient matching handles common plural forms such as `tomato` and
  `tomatoes`.
- Common units are converted before comparison, including g/kg and ml/l.
- Empty suggestions feedback instead of a blank screen.
- No maps, GPS, location, shopping, payment, or external recipe API features.

## Open and run

1. Open the `smart-pantry-manager` folder in Android Studio.
2. Allow Gradle to sync.
3. Start an Android emulator or connect an Android device.
4. Press Run.

The app uses SQLite on the device. The first database creation seeds a small
sample pantry and 18 recipes so the recipe screen has useful content on the
first launch. Users can edit or delete the sample ingredients and add their
own.

## How the strict rule works

`IngredientMatcher.canMakeRecipe()` checks every ingredient in a recipe:

1. It normalizes the ingredient names.
2. It finds pantry rows with the same normalized name.
3. It converts compatible units to a common base quantity.
4. It adds quantities when the same ingredient appears in more than one pantry
   row.
5. It rejects the recipe immediately if an ingredient is missing or the
   available quantity is too small.

For example, a recipe requiring 500 g of tomatoes will not appear when the
pantry contains only 300 g. A recipe requiring tomato will still match
tomatoes, but a recipe with any other missing ingredient is excluded.

## Database tables

- `pantry_items`: the user's ingredient name, quantity, unit, and optional
  expiry date.
- `recipes`: recipe names and preparation steps.
- `recipe_ingredients`: the exact ingredient and quantity requirements linked to
  each recipe.

## Suggested demonstration flow

1. Open Pantry and show the seeded items.
2. Add an ingredient and show it in the RecyclerView.
3. Edit its quantity, then delete it.
4. Open Recipes and show only recipes that currently pass strict matching.
5. Delete or reduce one ingredient and reopen Recipes to show a recipe
   disappear because a requirement is no longer satisfied.
6. Open a recipe detail screen.
7. Close and reopen the app to demonstrate SQLite persistence.
8. Open Settings and save the expiring-alert preference.

## Main source locations

- `app/src/main/java/com/example/smartpantry/data/DatabaseHelper.java`
  contains SQLite schema, CRUD operations, and seed data.
- `app/src/main/java/com/example/smartpantry/util/IngredientMatcher.java`
  contains the strict business rule.
- `app/src/main/java/com/example/smartpantry/ui/` contains custom adapters.
- `app/src/main/java/com/example/smartpantry/*Activity.java` contains the
  screens and Intent navigation.
- The completed report is prepared separately for the Moodle submission ZIP.
- `docs/Demo_Script.md` is a 5–7 minute narrated demonstration guide.
- `docs/GitHub_Commit_Plan.md` lists genuine incremental commit milestones. Do
  not fabricate commit history; make and push the commits as you develop.