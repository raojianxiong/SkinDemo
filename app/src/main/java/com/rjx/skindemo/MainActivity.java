package com.rjx.skindemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.rjx.skinlib.SkinManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onChange(View view) {
        //打包apk放到这个目录测试
        SkinManager.getInstance().loadSkin("/data/data/com.rjx.skindemo/skin.apk");
    }
    public void restore(View view){
        SkinManager.getInstance().loadSkin(null);
    }
}