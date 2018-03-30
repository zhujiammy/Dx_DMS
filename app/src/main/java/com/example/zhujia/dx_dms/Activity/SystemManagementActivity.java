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
 * Created by ZHUJIA on 2018/3/16.
 * 系统管理
 */

public class SystemManagementActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private TextView Jurisdiction_btn,role_btn,user_btn;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.systemmanagement_xml);
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
        Jurisdiction_btn=(TextView)findViewById(R.id.Jurisdiction_btn);
        Jurisdiction_btn.setOnClickListener(this);
        role_btn=(TextView)findViewById(R.id.role_btn);
        role_btn.setOnClickListener(this);
        user_btn=(TextView)findViewById(R.id.user_btn);
        user_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v==Jurisdiction_btn){
            //权限定义
            intent=new Intent(this,BasicresourceActivity.class);
            startActivity(intent);

        }
        if(v==role_btn){
            //角色定义
            intent=new Intent(this,RoleAcitivity.class);
            startActivity(intent);

        }
        if(v==user_btn){
            //用户管理
            intent=new Intent(this,UserManagementAcitvity.class);
            startActivity(intent);

        }


    }
}
