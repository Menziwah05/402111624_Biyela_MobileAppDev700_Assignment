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
        String name = rawName.toLowerCase(Locale.ROOT).trim()
                .replaceAll("[^a-z0-9 ]", "")
                .replaceAll("\\s+", " ");
        if (name.equals("maize meal") || name.equals("mealie meal")
                || name.equals("mielie meal") || name.equals("corn meal")) {
            return "maize meal";
        }
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
        String firstUnit = normalizeUnit(first);
        String secondUnit = normalizeUnit(second);
        if (firstUnit.equals(secondUnit)) {
            return true;
        }
        return unitCategory(firstUnit).equals(unitCategory(secondUnit))
                && (unitCategory(firstUnit).equals("weight")
                || unitCategory(firstUnit).equals("volume"));
    }

    private static String unitCategory(String unit) {
        String normalized = normalizeUnit(unit);
        if (normalized.equals("kg") || normalized.equals("g")) {
            return "weight";
        }
        if (normalized.equals("l") || normalized.equals("ml") ||
                normalized.equals("cup") || normalized.equals("tbsp") ||
                normalized.equals("tsp")) {
            return "volume";
        }
        return "count:" + normalized;
    }

    private static String normalizeUnit(String unit) {
        String normalized = unit.toLowerCase(Locale.ROOT).trim();
        switch (normalized) {
            case "kilogram":
            case "kilograms":
                return "kg";
            case "gram":
            case "grams":
                return "g";
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "l";
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";
            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
                return "piece";
            case "slice":
            case "slices":
                return "slice";
            default:
                return normalized;
        }
    }

    private static double toBaseQuantity(double quantity, String unit) {
        String normalized = normalizeUnit(unit);
        switch (normalized) {
            case "kg":
                return quantity * 1000;
            case "g":
                return quantity;
            case "l":
                return quantity * 1000;
            case "cup":
                return quantity * 240;
            case "tbsp":
                return quantity * 15;
            case "tsp":
                return quantity * 5;
            case "ml":
                return quantity;
            default:
                return quantity;
        }
    }
}
