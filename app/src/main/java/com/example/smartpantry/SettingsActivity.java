package com.example.smartpantry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private Switch expiryAlertsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("pantry_settings", MODE_PRIVATE);
        expiryAlertsSwitch = findViewById(R.id.switchExpiryAlerts);
        expiryAlertsSwitch.setChecked(preferences.getBoolean("expiry_alerts", true));

        findViewById(R.id.buttonSaveSettings).setOnClickListener(v -> {
            preferences.edit()
                    .putBoolean("expiry_alerts", expiryAlertsSwitch.isChecked())
                    .apply();
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        });

        Button pantry = findViewById(R.id.navPantrySettings);
        Button suggestions = findViewById(R.id.navSuggestionsSettings);
        pantry.setOnClickListener(v -> finish());
        suggestions.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, SuggestedRecipesActivity.class)));
    }
}
