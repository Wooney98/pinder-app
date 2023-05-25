package com.example.pinder99;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class kakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this, "b57fd3a253a7d6da0d8d84a50d4e8020");
    }
}
