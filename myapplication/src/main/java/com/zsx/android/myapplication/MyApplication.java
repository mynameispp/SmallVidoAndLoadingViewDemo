package com.zsx.android.myapplication;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

import mabeijianxi.camera.VCamera;
import mabeijianxi.camera.util.DeviceUtils;

/**
 * 创建者： feifan.pi 在 2017/10/19.
 */

public class MyApplication extends Application {
    public static  Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        initSmallVideo(this);
    }
    public static void initSmallVideo(Context context) {
        // 设置拍摄视频缓存路径
        File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                VCamera.setVideoCachePath(dcim + "/mabeijianxi/");
            } else {
                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/")
                        + "/mabeijianxi/");
            }
        } else {
            VCamera.setVideoCachePath(dcim + "/mabeijianxi/");
        }
        VCamera.setDebugMode(true);
        VCamera.initialize(context);
    }
}
