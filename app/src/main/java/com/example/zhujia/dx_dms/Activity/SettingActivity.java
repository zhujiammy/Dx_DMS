package com.example.zhujia.dx_dms.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.zhujia.dx_dms.Data.AllData;
import com.example.zhujia.dx_dms.R;
import com.example.zhujia.dx_dms.Tools.Net.Constant;
import com.example.zhujia.dx_dms.Tools.Net.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZHUJIA on 2018/3/28.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView loginout_btn;
    private TextView text1;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private Spinner groupCompany;
    private List<AllData> dicts2 = new ArrayList<AllData>();
    private ArrayAdapter<AllData> arrAdapterpay2;
    private String token,groupCompanyid;
    private String groupCompanys,companyId;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_xml);
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
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        token=sharedPreferences.getString("token","");
        groupCompanys=sharedPreferences.getString("groupCompanys","");
        companyId=sharedPreferences.getString("companyId","");

        initUI();


    }

    private void initUI(){
        text1=(TextView)findViewById(R.id.text1);
        groupCompany=(Spinner)findViewById(R.id.groupCompanys);
        text1.setText("设置");
        loginout_btn=(TextView)findViewById(R.id.loginout_btn);
        loginout_btn.setOnClickListener(this);
        groupCompany.setOnItemSelectedListener(listener1);
        JSONArray basicresources= null;
        try {
            basicresources = new JSONArray(groupCompanys);
            dicts2.clear();
            for(int i=0;i<basicresources.length();i++){
                JSONObject object1=basicresources.getJSONObject(i);
                dicts2.add(new AllData(object1.getString("id"),object1.getString("companyName")));
                arrAdapterpay2 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts2);
                //设置样式
                arrAdapterpay2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                groupCompany.setAdapter(arrAdapterpay2);
            }

            int d=arrAdapterpay2.getCount();
            for(int j=0;j<d;j++){
                if(companyId.equals(arrAdapterpay2.getItem(j).getStr())){
                    groupCompany.setAdapter(arrAdapterpay2);
                    groupCompany.setSelection(j,true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    Spinner.OnItemSelectedListener listener1=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            groupCompanyid=((AllData)groupCompany.getSelectedItem()).getStr();
            SharedPreferences.Editor editor=sharedPreferences.edit();
            //系统用户
            editor.putString("companyId",groupCompanyid);
            editor.commit();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };



    @Override
    public void onClick(View v) {
        if(v==loginout_btn){
            new HttpUtils().LogoutPost(Constant.APPURLS+"/system/user/logout",token,new HttpUtils.HttpCallback() {

                @Override
                public void onSuccess(String data) {
                    // TODO Auto-generated method stub
                    com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                    Message msg= Message.obtain(
                            mHandler,1,data
                    );
                    mHandler.sendMessage(msg);
                }

            });
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what){

                    case 1:
                        //返回item类型数据
                        JSONObject reslutJSONObject=new JSONObject(msg.obj.toString());
                        if(reslutJSONObject.getString("code").equals("200")){

                            SharedPreferences.Editor editor1 = sharedPreferences.edit();
                            editor1.clear().commit();
                            Intent intent =new Intent(SettingActivity.this, LoginActivity.class);
                            startActivity(intent);
                            SettingActivity.this.finish();
                        }

                        break;

                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };
}
