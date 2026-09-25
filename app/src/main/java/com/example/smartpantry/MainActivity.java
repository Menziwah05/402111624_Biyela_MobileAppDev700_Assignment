package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.ui.PantryAdapter;

public class MainActivity extends AppCompatActivity implements PantryAdapter.PantryActionListener {
    private DatabaseHelper databaseHelper;
    private PantryAdapter pantryAdapter;
    private TextView pantrySummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        pantrySummary = findViewById(R.id.textPantrySummary);
        RecyclerView pantryList = findViewById(R.id.recyclerPantry);
        pantryList.setLayoutManager(new LinearLayoutManager(this));
        pantryAdapter = new PantryAdapter(this);
        pantryList.setAdapter(pantryAdapter);

        findViewById(R.id.buttonAddIngredient).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        java.util.List<PantryItem> items = databaseHelper.getAllPantryItems();
        pantryAdapter.setItems(items);
        pantrySummary.setText(items.size() + " pantry items saved locally");
    }

    private void setupNavigation() {
        Button pantry = findViewById(R.id.navPantry);
        Button suggestions = findViewById(R.id.navSuggestions);
        Button settings = findViewById(R.id.navSettings);
        pantry.setOnClickListener(v -> recreate());
        suggestions.setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        settings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra("ingredient_id", item.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete pantry item?")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deletePantryItem(item.getId());
                    loadPantry();
                })
                .show();
    }
}
