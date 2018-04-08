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
 * 客户管理
 */

public class CustomerManagementAcitivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private TextView customer_btn,inovice_btn,partnerbank_btn,partneraccount_btn,partnerrecharge_btn,partneraccountlog_btn,partnersku_btn;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customermanagement_xml);
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
        customer_btn=(TextView)findViewById(R.id.customer_btn);
        customer_btn.setOnClickListener(this);
        inovice_btn=(TextView)findViewById(R.id.inovice_btn);
        inovice_btn.setOnClickListener(this);
        partnerbank_btn=(TextView)findViewById(R.id.partnerbank_btn);
        partnerbank_btn.setOnClickListener(this);
        partneraccount_btn=(TextView)findViewById(R.id.partneraccount_btn);
        partneraccount_btn.setOnClickListener(this);
        partnerrecharge_btn=(TextView)findViewById(R.id.partnerrecharge_btn);
        partnerrecharge_btn.setOnClickListener(this);
        partneraccountlog_btn=(TextView)findViewById(R.id.partneraccountlog_btn);
        partneraccountlog_btn.setOnClickListener(this);
        partnersku_btn=(TextView)findViewById(R.id.partnersku_btn);
        partnersku_btn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if(v==customer_btn){
            //客户列表
            intent=new Intent(this,CustomerLsitActivity.class);
            startActivity(intent);

        }
        if(v==inovice_btn){
            //发票列表
            intent=new Intent(this,PartnerInvoiceAcitivity.class);
            startActivity(intent);

        }

        if(v==partnerbank_btn){
            //付款账号
            intent=new Intent(this,PartnerbankActivity.class);
            startActivity(intent);

        }
        if(v==partneraccount_btn){
            //资金余额
            intent=new Intent(this,PartnerAccountActivity.class);
            startActivity(intent);

        }
        if(v==partnerrecharge_btn){
            //充值列表
            intent=new Intent(this,PartnerRechargeActivity.class);
            startActivity(intent);

        }
        if(v==partneraccountlog_btn){
            //资金流水
            intent=new Intent(this,PartnerRechargeActivity.class);
            startActivity(intent);

        }
        if(v==partnersku_btn){
            //销价列表
            intent=new Intent(this,PartnerSkuActivity.class);
            startActivity(intent);

        }
    }
}
