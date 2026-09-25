package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.ui.RecipeAdapter;
import com.example.smartpantry.util.IngredientMatcher;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.RecipeClickListener {
    private DatabaseHelper databaseHelper;
    private RecipeAdapter recipeAdapter;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);
        emptyMessage = findViewById(R.id.textNoSuggestions);
        RecyclerView recipeList = findViewById(R.id.recyclerSuggestedRecipes);
        recipeList.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(this);
        recipeList.setAdapter(recipeAdapter);
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<Recipe> suggestions = new ArrayList<>();
        List<com.example.smartpantry.model.PantryItem> pantry =
                databaseHelper.getAllPantryItems();
        for (Recipe recipe : databaseHelper.getAllRecipes()) {
            if (IngredientMatcher.canMakeRecipe(recipe, pantry)) {
                suggestions.add(recipe);
            }
        }
        recipeAdapter.setRecipes(suggestions);
        emptyMessage.setVisibility(suggestions.isEmpty()
                ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void setupNavigation() {
        Button pantry = findViewById(R.id.navPantrySuggestions);
        Button suggestions = findViewById(R.id.navSuggestionsActive);
        Button settings = findViewById(R.id.navSettingsSuggestions);
        pantry.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        suggestions.setOnClickListener(v -> loadSuggestions());
        settings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getId());
        startActivity(intent);
    }
}
