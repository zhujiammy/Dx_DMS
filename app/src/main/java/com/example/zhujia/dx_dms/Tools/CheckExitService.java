package com.example.zhujia.dx_dms.Tools;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * Created by DXSW5 on 2017/9/20.
 *
 * 监听app是否退出
 */

public class CheckExitService  extends Service {

    private String packageName = "com.example.zhujia.dx_dms";
    SharedPreferences sharedPreferences;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        Log.e("TAG", "toast: "+"App要退出了" );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    //service异常停止的回调
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ActivityManager activtyManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activtyManager.getRunningAppProcesses();
        for (int i = 0; i < runningAppProcesses.size(); i++) {
            if (packageName.equals(runningAppProcesses.get(i).processName)) {

            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "onStartCommandsf: "+"App检测" );
    }
}
