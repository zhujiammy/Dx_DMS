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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by ZHUJIA on 2018/3/15.
 * 修改发票
 */

public class AddPartnerInvoiceAcitivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text1;
    private Toolbar toolbar;
    private EditText invoiceAddr,invoiceBankName,invoiceBankNo,invoiceCode,invoicePhone,invoiceTitle,partnerInfoId;
    private SharedPreferences sharedPreferences;
    Intent intent;
    JSONObject object,pager;
    private String token,partnerInfo;
    private Spinner invoiceType;
    private String invoiceType_id;
    private List<AllData> dicts1 = new ArrayList<AllData>();
    private List<AllData> dicts2 = new ArrayList<AllData>();
    private  ArrayAdapter<AllData> arrAdapterpay1;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpartnerinvoice_xml);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent=getIntent();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        token=sharedPreferences.getString("token","");
        loadbaseinfo();
        loadGet(intent.getStringExtra("id"));
        loadstatues();
        initUI();
    }


    private void initUI(){
        text1=(TextView)findViewById(R.id.text1);
        invoiceAddr=(EditText) findViewById(R.id.invoiceAddr);
        invoiceBankName=(EditText)findViewById(R.id.invoiceBankName);
        invoiceBankNo=(EditText)findViewById(R.id.invoiceBankNo);
        invoiceCode=(EditText)findViewById(R.id.invoiceCode);
        invoicePhone=(EditText)findViewById(R.id.invoicePhone);
        invoiceTitle=(EditText)findViewById(R.id.invoiceTitle);
        invoiceType=(Spinner) findViewById(R.id.invoiceType);
        partnerInfoId=(EditText)findViewById(R.id.partnerInfoId);
        partnerInfoId.setEnabled(false);
        invoiceType.setOnItemSelectedListener(listener);
            text1.setText("修改发票");
    }

    //客户列表
    private void  loadstatues(){
        new HttpUtils().postJson(Constant.APPURLS+"partner/partnerinfo/list","{}",token,new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,3,data
                );
                mHandler.sendMessage(msg);
            }

        });
    }
    private void loadGet(String id){
        System.out.print(id);
        new HttpUtils().Post(Constant.APPURLS+"partner/partnerinvoice/get"+"/"+id,token,new HttpUtils.HttpCallback() {

            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,2,data
                );
                mHandler.sendMessage(msg);
            }

        });

    }

    private void loadbaseinfo(){
        new HttpUtils().Post(Constant.APPURLS+"partner/partnerinvoice/inoviceTypeEnums",token,new HttpUtils.HttpCallback() {
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

    //所属集团
    Spinner.OnItemSelectedListener listener=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            invoiceType_id=((AllData)invoiceType.getSelectedItem()).getStr();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.save_btn){

            object = new JSONObject();
            try {
                object.put("invoiceAddr",invoiceAddr.getText().toString());
                object.put("invoiceBankName",invoiceBankName.getText().toString());
                object.put("invoiceBankNo",invoiceBankNo.getText().toString());
                object.put("invoiceCode",invoiceCode.getText().toString());
                object.put("invoicePhone",invoicePhone.getText().toString());
                object.put("invoiceTitle",invoiceTitle.getText().toString());
                object.put("invoiceType",invoiceType_id);
                object.put("partnerInfoId",partnerInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String params=object.toString();
            //保存
            if(TextUtils.isEmpty(invoiceAddr.getText().toString())){
                Toast.makeText(getApplicationContext(),"注册地址不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(invoiceBankName.getText().toString())){
                Toast.makeText(getApplicationContext(),"开户银行不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(invoiceBankNo.getText().toString())){
                Toast.makeText(getApplicationContext(),"银行账号不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(invoiceCode.getText().toString())){
                Toast.makeText(getApplicationContext(),"纳税人识别码不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(invoicePhone.getText().toString())){
                Toast.makeText(getApplicationContext(),"注册电话不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(invoiceTitle.getText().toString())){
                Toast.makeText(getApplicationContext(),"发票抬头不能为空",Toast.LENGTH_SHORT).show();
            }else if(invoiceType_id==null){
                Toast.makeText(getApplicationContext(),"发票类型不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(partnerInfoId.getText().toString())){
                Toast.makeText(getApplicationContext(),"客户名称不能为空",Toast.LENGTH_SHORT).show();
            }else {
                //修改
                new HttpUtils().postJson(Constant.APPURLS + "partner/partnerinvoice/update" + "/" + intent.getStringExtra("id"),params,token, new HttpUtils.HttpCallback() {

                    @Override
                    public void onSuccess(String data) {
                        // TODO Auto-generated method stub
                        com.example.zhujia.dx_dms.Tools.Log.printJson("tag", data, "header");

                        Message msg = Message.obtain(
                                mHandler, 0, data
                        );
                        mHandler.sendMessage(msg);
                    }

                });
            }







        }



        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what) {

                    case 0:
                        //返回item类型数据
                        JSONObject reslutJSONObject=new JSONObject(msg.obj.toString());
                        if(reslutJSONObject.getString("code").equals("200")){
                            Toast.makeText(AddPartnerInvoiceAcitivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            Intent data=new Intent();
                            data.putExtra("freshen","y");
                            setResult(RESULT_OK,data);
                            finish();
                        }

                        break;

                    case 1:
                        JSONArray companylist=new JSONArray(msg.obj.toString());
                        for(int i=0;i<companylist.length();i++){
                            JSONObject object1=companylist.getJSONObject(i);
                            dicts1.add(new AllData(object1.getString("key"),object1.getString("value")));
                            arrAdapterpay1 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts1);
                            //设置样式
                            arrAdapterpay1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            invoiceType.setAdapter(arrAdapterpay1);
                        }



                        break;

                    case 2:
                        JSONObject object=new JSONObject(msg.obj.toString());
                        invoiceAddr.setText(object.getString("invoiceAddr"));
                        invoiceBankName.setText(object.getString("invoiceBankName"));
                        invoiceBankNo.setText(object.getString("invoiceBankNo"));
                        invoiceCode.setText(object.getString("invoiceCode"));
                        invoicePhone.setText(object.getString("invoicePhone"));
                        invoiceTitle.setText(object.getString("invoiceTitle"));
                        partnerInfo=object.getString("partnerInfoId");

                            if(!intent.getStringExtra("invoiceType").equals("")){
                                int d=arrAdapterpay1.getCount();
                                for(int j=0;j<d;j++){
                                    if(intent.getStringExtra("invoiceType").equals(arrAdapterpay1.getItem(j).getStr())){
                                        invoiceType.setAdapter(arrAdapterpay1);
                                        invoiceType.setSelection(j,true);
                                    }
                                }
                            }



                        break;
                    case 3:
                        JSONArray jsonArray=new JSONArray(msg.obj.toString());
                        if(jsonArray.length()>0){
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject object1=jsonArray.getJSONObject(i);
                                dicts2.add(new AllData(object1.getString("id"),object1.getString("partnerName")));
                            }
                            for(int j=0;j<dicts2.size();j++){
                                if(partnerInfo!=null){
                                    if(partnerInfo.equals(dicts2.get(j).getStr())){
                                        partnerInfoId.setText(dicts2.get(j).getText());
                                    }
                                }

                            }
                        }
                        break;

                    default:
                        Toast.makeText(AddPartnerInvoiceAcitivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onClick(View v) {

    }
}
