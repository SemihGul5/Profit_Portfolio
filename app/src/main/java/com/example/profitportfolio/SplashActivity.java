package com.example.profitportfolio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.profitportfolio.databinding.ActivitySplashBinding;


public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    Animation left,right;
    public static final int SPLASH_SCREEN=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        left= AnimationUtils.loadAnimation(this ,R.anim.top_animation);
        right=AnimationUtils.loadAnimation(this,R.anim.right_animation);

        binding.textView.setAnimation(left);
        binding.textView4.setAnimation(right);


        createSplash();
    }
    private void createSplash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}