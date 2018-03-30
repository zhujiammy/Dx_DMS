package com.example.zhujia.dx_dms.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.zhujia.dx_dms.R;

/**
 * Created by ZHUJIA on 2018/3/15.
 * 集团管理
 */

public class GroupManagementActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private TextView basicinfo,companyinfo;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupmanagement_xml);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initUI();
    }

    private void initUI(){
        basicinfo=(TextView)findViewById(R.id.basicinfo);
        companyinfo=(TextView)findViewById(R.id.companyinfo);
        basicinfo.setOnClickListener(this);
        companyinfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v==basicinfo){
            //基础信息
            intent=new Intent(this,GroupinFormation_Activity.class);
            startActivity(intent);

        }
        if(v==companyinfo){
            //公司信息
            intent=new Intent(this,CompanyinformationActivity.class);
            startActivity(intent);
        }

    }
}
