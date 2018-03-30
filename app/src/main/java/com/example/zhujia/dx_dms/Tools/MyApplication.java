package com.example.zhujia.dx_dms.Tools;

import android.app.Application;
import android.content.Context;

/**
 * Created by ZHUJIA on 2018/3/27.
 */

public class MyApplication extends Application{

    private static Context context;

    public static Context getContext() {
        return context;
    }

    private String companyId ;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }
}
