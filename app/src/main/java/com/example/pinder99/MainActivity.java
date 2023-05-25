package com.example.pinder99;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    ViewPager2 viewpager;
    MainViewPagerAdapter viewPagerAdapter;
    private String[] titles = new String[]{"지도", "게시판"};

//뒤로 가기 버튼 2번 했을 떄 앱 종료 구현
    //마지막으로 뒤로 가기 버튼 눌렀던 시간 저장
    private long backKeyPressedTime =0;
    //첫 번쨰로 뒤로 가기 버튼 누를 때 표시
    private Toast toast;

    @Override
    public void onBackPressed(){
        //마지막으로 뒤로 가기 버튼(이하 버튼이라 명명)을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        //마지막으로 버튼 누른 시간이 2.5초가 지났다면 Toast 출력
        //2500= 2.5s
        if(System.currentTimeMillis() > backKeyPressedTime +2500){
            backKeyPressedTime = System.currentTimeMillis();
            toast =Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG );
            toast.show();
            return;
        }

        //마지막으로 버튼 누른 시간에 2.5초를 더해 현재 시간과 비교 후
        //마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았다면 종료
        if (System.currentTimeMillis()<= backKeyPressedTime+2500){
            finish();
            toast.cancel();
            toast = Toast.makeText(this, "이용해 주셔서 감사합니다.", Toast.LENGTH_LONG );
            toast.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViewPager();
        setTabLayout();
        //메뉴 버튼 클릭시 화면 전환
        Button Side_btn = (Button) findViewById(R.id.Side_btn);
        Side_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View vIew){
                Intent intent = new Intent(getApplicationContext(), sidebar.class);
                startActivity(intent);
            }
        });
    }

    private void setViewPager() {
        viewpager = (ViewPager2) findViewById(R.id.viewpager);
        viewpager.setOffscreenPageLimit(1);
        viewpager.setUserInputEnabled(false);
        viewPagerAdapter = new MainViewPagerAdapter(this);
        viewPagerAdapter.addFragment(new MapFragment());
        viewPagerAdapter.addFragment(new ReportFragment());
        viewpager.setAdapter(viewPagerAdapter);
    }

    private void setTabLayout() {
        //메인 화면 탭 메뉴 기능
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewpager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        new TabLayoutMediator(tabLayout, viewpager, (tab, position) -> tab.setText(titles[position])).attach();

    }
}



