package com.example.txs.myapplication;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;


public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Fresco 建议放在这里
        Fresco.initialize(this);
    }
}
