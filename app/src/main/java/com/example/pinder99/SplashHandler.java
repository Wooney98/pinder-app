package com.example.pinder99;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class SplashHandler extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//어플 들어갈떄 로딩화면 GIF로 설정함.

        ImageView gif_image = (ImageView) findViewById( R.id.gif_image);
        Glide.with(this).load(R.drawable.splash_image).into(gif_image);

        Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(SplashHandler.this, LoginActivity.class);
                startActivity(main);
                finish();
            }
        },4000); // 3초 후  스플래시 화면을 닫습니다
    }

}
