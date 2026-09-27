package com.example.smartpantry.util;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IngredientMatcherTest {
    @Test
    public void requiresEveryIngredientBeforeSuggestingRecipe() {
        Recipe recipe = recipe("Toast", new String[][]{
                {"bread", "2", "slices"},
                {"cheese", "50", "g"}
        });

        assertFalse(IngredientMatcher.canMakeRecipe(recipe, Collections.singletonList(
                new PantryItem("bread", 2, "slices", "")
        )));
    }

    @Test
    public void acceptsPluralNamesAndCompatibleMetricUnits() {
        Recipe recipe = recipe("Milk recipe", new String[][]{
                {"tomato", "500", "g"},
                {"milk", "250", "ml"}
        });

        assertTrue(IngredientMatcher.canMakeRecipe(recipe, Arrays.asList(
                new PantryItem("tomatoes", 0.5, "kg", ""),
                new PantryItem("milk", 0.25, "l", "")
        )));
    }

    @Test
    public void doesNotTreatSlicesAsPieces() {
        Recipe recipe = recipe("Toast", new String[][]{
                {"bread", "2", "slices"}
        });

        assertFalse(IngredientMatcher.canMakeRecipe(recipe, Collections.singletonList(
                new PantryItem("bread", 2, "pieces", "")
        )));
    }

    private Recipe recipe(String name, String[][] ingredients) {
        Recipe recipe = new Recipe(1, name, "Prepare the ingredients.");
        for (String[] ingredient : ingredients) {
            recipe.addIngredient(new com.example.smartpantry.model.RecipeIngredient(
                    ingredient[0],
                    Double.parseDouble(ingredient[1]),
                    ingredient[2]));
        }
        return recipe;
    }
}