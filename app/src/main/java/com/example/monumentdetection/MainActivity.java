package com.example.monumentdetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    CardView takePicture;
    public static final int RequestPermissionCode = 1;
    ImageView continentImg;
    TextView continentName;
    String continent;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        takePicture = findViewById(R.id.take_picture);
        continentImg = findViewById(R.id.continentImgMain);
        continentName = findViewById(R.id.continentNameMain);

        EnableRuntimePermission();

        continent = getIntent().getStringExtra("continent");

        if (continent.equals(getResources().getString(R.string.asia))) {
            continentName.setText(getResources().getString(R.string.asia));
            continentImg.setImageDrawable(getDrawable(R.drawable.asia));
        } else if (continent.equals(getResources().getString(R.string.africa))) {
            continentName.setText(getResources().getString(R.string.africa));
            continentImg.setImageDrawable(getDrawable(R.drawable.africa));
        } else if (continent.equals(getResources().getString(R.string.europe))) {
            continentName.setText(getResources().getString(R.string.europe));
            continentImg.setImageDrawable(getDrawable(R.drawable.europe));
        } else if (continent.equals(getResources().getString(R.string.north_america))) {
            continentName.setText(getResources().getString(R.string.north_america));
            continentImg.setImageDrawable(getDrawable(R.drawable.northamerica));
        } else if (continent.equals(getResources().getString(R.string.south_america))) {
            continentName.setText(getResources().getString(R.string.south_america));
            continentImg.setImageDrawable(getDrawable(R.drawable.southamerica));
        } else if (continent.equals(getResources().getString(R.string.australia))) {
            continentName.setText(getResources().getString(R.string.australia));
            continentImg.setImageDrawable(getDrawable(R.drawable.australia));
        }

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            try {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("bitmap", bitmap);
                intent.putExtra("continent", continent);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(MainActivity.this,"CAMERA permission allows us to Access CAMERA app",     Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}