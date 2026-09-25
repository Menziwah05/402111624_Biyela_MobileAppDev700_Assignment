package com.example.smartpantry.util;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.List;
import java.util.Locale;

public final class IngredientMatcher {
    private IngredientMatcher() {
    }

    public static boolean canMakeRecipe(Recipe recipe, List<PantryItem> pantryItems) {
        for (RecipeIngredient required : recipe.getIngredients()) {
            double available = 0;
            boolean compatibleIngredientFound = false;

            for (PantryItem pantryItem : pantryItems) {
                if (normalizeName(pantryItem.getName()).equals(normalizeName(required.getName()))
                        && compatibleUnits(pantryItem.getUnit(), required.getUnit())) {
                    compatibleIngredientFound = true;
                    available += toBaseQuantity(pantryItem.getQuantity(), pantryItem.getUnit());
                }
            }

            double needed = toBaseQuantity(required.getRequiredQuantity(), required.getUnit());
            if (!compatibleIngredientFound || available + 0.0001 < needed) {
                return false;
            }
        }
        return true;
    }

    public static String normalizeName(String rawName) {
        String name = rawName.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
        if (name.endsWith("ies") && name.length() > 3) {
            return name.substring(0, name.length() - 3) + "y";
        }
        if (name.endsWith("es") && name.length() > 4) {
            return name.substring(0, name.length() - 2);
        }
        if (name.endsWith("s") && name.length() > 3) {
            return name.substring(0, name.length() - 1);
        }
        return name;
    }

    private static boolean compatibleUnits(String first, String second) {
        return unitCategory(first).equals(unitCategory(second));
    }

    private static String unitCategory(String unit) {
        String normalized = unit.toLowerCase(Locale.ROOT).trim();
        if (normalized.equals("kg") || normalized.equals("g") ||
                normalized.equals("gram") || normalized.equals("grams")) {
            return "weight";
        }
        if (normalized.equals("l") || normalized.equals("ml") ||
                normalized.equals("litre") || normalized.equals("liter") ||
                normalized.equals("cup") || normalized.equals("tbsp") ||
                normalized.equals("tsp")) {
            return "volume";
        }
        return "count";
    }

    private static double toBaseQuantity(double quantity, String unit) {
        String normalized = unit.toLowerCase(Locale.ROOT).trim();
        switch (normalized) {
            case "kg":
                return quantity * 1000;
            case "g":
            case "gram":
            case "grams":
                return quantity;
            case "l":
            case "litre":
            case "liter":
                return quantity * 1000;
            case "cup":
                return quantity * 240;
            case "tbsp":
                return quantity * 15;
            case "tsp":
                return quantity * 5;
            case "ml":
            default:
                return quantity;
        }
    }
}
