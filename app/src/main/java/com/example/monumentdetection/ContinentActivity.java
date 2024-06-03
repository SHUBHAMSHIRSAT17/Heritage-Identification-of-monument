package com.example.monumentdetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

public class ContinentActivity extends AppCompatActivity {

    CardView asia, africa, europe, northAmerica, southAmerica, australia;
    Intent intent;
    Boolean translate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_continent);

        asia = findViewById(R.id.asia_card);
        africa = findViewById(R.id.africa_card);
        europe = findViewById(R.id.europe_card);
        northAmerica = findViewById(R.id.north_america_card);
        southAmerica = findViewById(R.id.south_america_card);
        australia = findViewById(R.id.australia_card);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));

//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }

        intent = new Intent(ContinentActivity.this, MainActivity.class);

        asia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("continent", getResources().getString(R.string.asia));
                startActivity(intent);
            }
        });

        africa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("continent", getResources().getString(R.string.africa));
                startActivity(intent);
            }
        });

        europe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("continent", getResources().getString(R.string.europe));
                startActivity(intent);
            }
        });

        northAmerica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("continent", getResources().getString(R.string.north_america));
                startActivity(intent);
            }
        });

        southAmerica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("continent", getResources().getString(R.string.south_america));
                startActivity(intent);
            }
        });

        australia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("continent", getResources().getString(R.string.australia));
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lang, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.select_lang) {
            showChangeLanguageDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showChangeLanguageDialog() {
        final String[] listview = {"English", "हिंदी"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ContinentActivity.this);
        builder.setTitle(getResources().getString(R.string.choose));
        builder.setSingleChoiceItems(listview, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    setLocale("en", false);
                    recreate();
                } else if (i == 1) {
                    setLocale("hi", true);
                    recreate();
                }
                dialogInterface.dismiss();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setLocale(String lang, Boolean trans) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        translate = trans;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My Lang", lang);
        editor.putBoolean("translate", trans);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = preferences.getString("My Lang", "");
        Boolean trans = preferences.getBoolean("translate", false);
        setLocale(lang, trans);
    }


}