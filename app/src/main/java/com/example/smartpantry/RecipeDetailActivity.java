package com.example.smartpantry;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        Recipe recipe = new DatabaseHelper(this).getRecipe(recipeId);
        if (recipe == null) {
            finish();
            return;
        }

        ((TextView) findViewById(R.id.textRecipeDetailTitle)).setText(recipe.getName());
        LinearLayout ingredientsContainer = findViewById(R.id.recipeIngredientsContainer);
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            TextView ingredientView = new TextView(this);
            ingredientView.setText(String.format(Locale.getDefault(), "• %.2f %s %s",
                    ingredient.getRequiredQuantity(), ingredient.getUnit(),
                    ingredient.getName()));
            ingredientView.setTextSize(16);
            ingredientView.setPadding(0, 8, 0, 8);
            ingredientsContainer.addView(ingredientView);
        }
        ((TextView) findViewById(R.id.textRecipeSteps)).setText(recipe.getSteps());
        findViewById(R.id.buttonBackFromRecipe).setOnClickListener(v -> finish());
    }
}
