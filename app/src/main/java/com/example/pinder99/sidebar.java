package com.example.pinder99;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class sidebar extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar);


        //메뉴 닫기 버튼
        Button btn= findViewById(R.id.close_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){

                Intent intent = new Intent(sidebar.this, MainActivity.class);
                startActivity(intent);

                //sidebar
                finish();
            }
        });

        //탭 메뉴 문의하기 버튼 클릭시 문의하기 화면 전환
        Button Ask_btn = (Button)findViewById(R.id.Ask_btn);
        Ask_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public  void onClick(View view){
                Intent intent = new Intent(sidebar.this, Ask.class);
                startActivity(intent);
            }
        });

        //탭 메뉴 소개하기 버튼 클릭시 소개 창 화면 전환
        Button Introduce_btn = (Button)findViewById(R.id.Introduce_btn);
        Introduce_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public  void onClick(View view){
                Intent intent = new Intent(sidebar.this, Introduce.class);
                startActivity(intent);
            }
        });
    }
}