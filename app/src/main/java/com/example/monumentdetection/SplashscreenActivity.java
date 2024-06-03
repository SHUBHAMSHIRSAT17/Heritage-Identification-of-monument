package com.example.monumentdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashscreenActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        topAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_anim);
        bottomAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_anim);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    imageView.setAnimation(topAnim);
                    textView.setAnimation(bottomAnim);
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), ContinentActivity.class);
                startActivity(intent);
                finish();
            }
        };
        myThread.start();
    }
}