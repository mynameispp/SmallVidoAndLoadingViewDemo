package com.zsx.android.loadinganima;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LoadingView loadingView= (LoadingView) findViewById(R.id.main_loading_view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //停止动画，并开始隐藏动画
                loadingView.disapper();
            }
        },10000);
    }
}
