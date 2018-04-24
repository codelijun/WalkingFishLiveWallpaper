package com.example.lijun.walkingfishwallpaperdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lijun.walkingfishwallpaperdemo.util.WallpaperUtil;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        WallpaperUtil.setLiveWallpaper(this, MainActivity.this, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // TODO: 2017/3/13 设置动态壁纸成功
                finish();
            } else {
                // TODO: 2017/3/13 取消设置动态壁纸
                finish();
            }
        }
    }
}
