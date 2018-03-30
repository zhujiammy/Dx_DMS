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
 * 产品管理
 */

public class productmanagementActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private TextView prolist_btn,producttype_btn,productseries_btn;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productmanagement_xml);
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
        prolist_btn=(TextView)findViewById(R.id.prolist_btn);
        prolist_btn.setOnClickListener(this);
        producttype_btn=(TextView)findViewById(R.id.producttype_btn);
        producttype_btn.setOnClickListener(this);
        productseries_btn=(TextView)findViewById(R.id.productseries_btn);
        productseries_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v==prolist_btn){
            //产品列表
            intent=new Intent(this,ProductListActivity.class);
            startActivity(intent);

        }
        if(v==producttype_btn){
            //类型列表
            intent=new Intent(this,ProductTypeActivity.class);
            startActivity(intent);

        }
        if(v==productseries_btn){
            //系列列表
            intent=new Intent(this,ProductSeriesActivity.class);
            startActivity(intent);
        }

    }
}
