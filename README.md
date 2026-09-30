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
- Unit selection from a dropdown with common kitchen units.
- Optional expiry dates are validated as real calendar dates in `YYYY-MM-DD`
  format, with an inline error shown before saving invalid input.
- 26 pre-loaded recipes with ingredients and preparation steps, including
  chakalaka, pap and boerewors, bobotie, bunny chow, vetkoek, malva pudding,
  melktert and Cape Malay tomato bredie.
- Strict matching checks every recipe ingredient and required quantity.
- Ingredient matching handles common plural forms such as `tomato` and
  `tomatoes`.
- Compatible units are converted before comparison, including g/kg and ml/l;
  incompatible count units such as pieces and slices are not mixed.
- Empty suggestions feedback instead of a blank screen.
- Suggestions are recalculated from the saved pantry each time the Recipes
  screen opens or resumes, and the screen shows the current number of matches.
- Database upgrades add the South African recipes without deleting existing
  pantry records.
- No maps, GPS, location, shopping, payment, or external recipe API features.

## Open and run

1. Open the `smart-pantry-manager` folder in Android Studio.
2. Allow Gradle to sync.
3. Start an Android emulator or connect an Android device.
4. Press Run.

The app uses SQLite on the device. The first database creation seeds a small
sample pantry and 26 recipes so the recipe screen has useful content on the
first launch. Users can edit or delete the sample ingredients and add their
own.

On devices upgrading from the previous database version, the new South African
recipe collection is added without clearing the user's pantry. A recipe only
appears after every required ingredient and quantity is present.

### South African recipe references

The local recipes are concise app-friendly adaptations based on traditional
dishes. Research references:

- [Pap, wors and chakalaka — SBS Food](https://www.sbs.com.au/food/recipe/pap-wors-chakalaka/tu56uqqv9)
- [Spicy chakalaka — Food & Home](https://www.foodandhome.co.za/recipes/spicy-chakalaka)
- [South African bobotie — BBC Food](https://www.bbc.co.uk/food/recipes/bobotie_95101)
- [South African heritage dishes — TASTE](https://taste.co.za/15-south-african-heritage-recipes-as-voted-for-by-you)
- [Traditional malva pudding — TASTE](https://taste.co.za/recipes/traditional-malva-pudding)

## Verification

Run the local Java tests from the project root:

```bash
./gradlew test
```

The tests cover impossible dates, the required date format, missing recipe
ingredients, metric unit conversion, plural ingredient names, and incompatible
count units.

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
2. Add an ingredient, select its unit from the dropdown, and show it in the
   RecyclerView.
3. Edit its quantity, then delete it.
4. Open Recipes and show the live match count and only recipes that currently
   pass strict matching.
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