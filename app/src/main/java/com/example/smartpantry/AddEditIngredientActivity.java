package com.example.smartpantry;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;

import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private EditText nameInput;
    private EditText quantityInput;
    private EditText unitInput;
    private EditText expiryInput;
    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        databaseHelper = new DatabaseHelper(this);
        nameInput = findViewById(R.id.inputIngredientName);
        quantityInput = findViewById(R.id.inputIngredientQuantity);
        unitInput = findViewById(R.id.inputIngredientUnit);
        expiryInput = findViewById(R.id.inputIngredientExpiry);

        ingredientId = getIntent().getIntExtra("ingredient_id", -1);
        TextView title = findViewById(R.id.textIngredientFormTitle);
        if (ingredientId >= 0) {
            title.setText("Edit pantry ingredient");
            populateForm();
        } else {
            title.setText("Add pantry ingredient");
        }

        findViewById(R.id.buttonCancelIngredient).setOnClickListener(v -> finish());
        findViewById(R.id.buttonSaveIngredient).setOnClickListener(v -> saveIngredient());
    }

    private void populateForm() {
        PantryItem item = databaseHelper.getPantryItem(ingredientId);
        if (item == null) {
            finish();
            return;
        }
        nameInput.setText(item.getName());
        quantityInput.setText(String.format(Locale.getDefault(), "%.2f", item.getQuantity()));
        unitInput.setText(item.getUnit());
        expiryInput.setText(item.getExpiryDate());
    }

    private void saveIngredient() {
        nameInput.setError(null);
        quantityInput.setError(null);
        unitInput.setError(null);

        String name = nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        String unit = unitInput.getText().toString().trim().toLowerCase(Locale.ROOT);
        String expiryDate = expiryInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Ingredient name is required");
            nameInput.requestFocus();
            return;
        }
        if (quantityText.isEmpty()) {
            quantityInput.setError("Quantity is required");
            quantityInput.requestFocus();
            return;
        }
        if (unit.isEmpty()) {
            unitInput.setError("Unit is required, for example g or pieces");
            unitInput.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException exception) {
            quantityInput.setError("Enter a valid number");
            quantityInput.requestFocus();
            return;
        }
        if (quantity <= 0) {
            quantityInput.setError("Quantity must be greater than zero");
            quantityInput.requestFocus();
            return;
        }

        PantryItem item = new PantryItem(ingredientId, name, quantity, unit, expiryDate);
        if (ingredientId >= 0) {
            databaseHelper.updatePantryItem(item);
            Toast.makeText(this, "Pantry item updated", Toast.LENGTH_SHORT).show();
        } else {
            databaseHelper.insertPantryItem(item);
            Toast.makeText(this, "Pantry item added", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
