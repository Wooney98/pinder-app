package com.example.pinder99;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Ask extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_tab);

        //메뉴 닫기 버튼
        Button btn= findViewById(R.id.close_btn2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){

                Intent intent = new Intent(Ask.this, sidebar.class);


                //Ask
                finish();
            }
        });
    }
}

