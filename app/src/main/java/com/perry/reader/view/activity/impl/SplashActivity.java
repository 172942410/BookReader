package com.perry.reader.view.activity.impl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.perry.reader.R;
import com.perry.reader.view.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 1000);//3秒后执行Runnable中的run方法
    }
}
