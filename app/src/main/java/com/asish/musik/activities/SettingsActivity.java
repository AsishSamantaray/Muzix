package com.asish.musik.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.asish.musik.R;

public class SettingsActivity extends AppCompatActivity {

    Switch switchShake;
    static String MY_PREFS_NAME ="ShakeFeature";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        switchShake = findViewById(R.id.switchShake);

        SharedPreferences preferences = getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
        final boolean isAllowed= preferences.getBoolean("feature",false);
        if(isAllowed){
            switchShake.setChecked(true);
        }else{
            switchShake.setChecked(false);

        }

        switchShake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE).edit();
                    editor.putBoolean("feature",true);
                    editor.apply();
                }
                else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE).edit();
                    editor.putBoolean("feature",false);
                    editor.apply();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
