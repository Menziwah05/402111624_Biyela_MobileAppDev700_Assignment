package com.example.smartpantry;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.ValidationUtils;

import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private EditText nameInput;
    private EditText quantityInput;
    private Spinner unitInput;
    private TextView unitError;
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
        unitError = findViewById(R.id.textIngredientUnitError);
        expiryInput = findViewById(R.id.inputIngredientExpiry);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.ingredient_unit_options));
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitInput.setAdapter(unitAdapter);
        unitInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    unitError.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // The prompt row remains selected until the user chooses a unit.
            }
        });

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
        selectUnit(item.getUnit());
        expiryInput.setText(item.getExpiryDate());
    }

    private void selectUnit(String savedUnit) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) unitInput.getAdapter();
        for (int index = 1; index < adapter.getCount(); index++) {
            if (adapter.getItem(index).equalsIgnoreCase(savedUnit)) {
                unitInput.setSelection(index);
                return;
            }
        }

        // Keep older custom unit values available when editing existing records.
        adapter.insert(savedUnit, 1);
        unitInput.setSelection(1);
    }

    private void saveIngredient() {
        nameInput.setError(null);
        quantityInput.setError(null);
        unitError.setVisibility(View.GONE);
        expiryInput.setError(null);

        String name = nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
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
        if (unitInput.getSelectedItemPosition() == 0) {
            unitError.setVisibility(View.VISIBLE);
            unitInput.requestFocus();
            return;
        }
        String unit = unitInput.getSelectedItem().toString().trim().toLowerCase(Locale.ROOT);

        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException exception) {
            quantityInput.setError("Enter a valid number");
            quantityInput.requestFocus();
            return;
        }
        if (!Double.isFinite(quantity) || quantity <= 0) {
            quantityInput.setError("Enter a finite quantity greater than zero");
            quantityInput.requestFocus();
            return;
        }
        if (!expiryDate.isEmpty() && !ValidationUtils.isValidExpiryDate(expiryDate)) {
            expiryInput.setError("Enter a real date in YYYY-MM-DD format, for example 2026-09-30");
            expiryInput.requestFocus();
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
