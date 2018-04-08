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
 * 新增付款账户
 */

public class AddPartnerbankActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text1;
    private Toolbar toolbar;
    private EditText paymentAccount,paymentNo;
    private SharedPreferences sharedPreferences;
    private Spinner paymentType,partnerInfo;
    private String paymentType_id,partnerInfoId;
    Intent intent;
    JSONObject object,pager;
    private String token;
    private List<AllData> dicts1 = new ArrayList<AllData>();
    private  ArrayAdapter<AllData> arrAdapterpay1;
    private List<AllData> dicts2 = new ArrayList<AllData>();
    private  ArrayAdapter<AllData> arrAdapterpay2;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpartnerbank_xml);
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
        loadstatues();
        loadstatue();

        initUI();
    }


    private void initUI(){
        text1=(TextView)findViewById(R.id.text1);
        paymentType=(Spinner) findViewById(R.id.paymentType);
        partnerInfo=(Spinner) findViewById(R.id.partnerInfoId);
        paymentAccount=(EditText)findViewById(R.id.paymentAccount);
        paymentNo=(EditText)findViewById(R.id.paymentNo);
        paymentType.setOnItemSelectedListener(listener);
        partnerInfo.setOnItemSelectedListener(listener1);

        if(intent.getStringExtra("type").equals("2")){
            text1.setText("修改");
            loadGet(intent.getStringExtra("id"));
        }else {
            text1.setText("新增");

        }


    }

    private void loadGet(String id){
        System.out.print(id);
        new HttpUtils().Post(Constant.APPURLS+"partner/partnerbank/get"+"/"+id,token,new HttpUtils.HttpCallback() {

            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,4,data
                );
                mHandler.sendMessage(msg);
            }

        });

    }

    //付款方式
    private void  loadstatue(){
        new HttpUtils().Post(Constant.APPURLS+"partner/partnerbank/paymentTypeEnums",token,new HttpUtils.HttpCallback() {
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

    Spinner.OnItemSelectedListener listener=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            paymentType_id=((AllData)paymentType.getSelectedItem()).getStr();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    Spinner.OnItemSelectedListener listener1=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            partnerInfoId=((AllData)partnerInfo.getSelectedItem()).getStr();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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
                object.put("partnerInfoId",partnerInfoId);
                object.put("paymentType",paymentType_id);
                object.put("paymentAccount",paymentAccount.getText().toString());
                object.put("paymentNo",paymentNo.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String params=object.toString();
            //保存
            if(partnerInfoId==null||partnerInfoId.equals("0")){
                Toast.makeText(getApplicationContext(),"客户名称不能为空",Toast.LENGTH_SHORT).show();
            }
            if(paymentType_id==null||paymentType_id.equals("0")){
                Toast.makeText(getApplicationContext(),"支付方式不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(paymentAccount.getText().toString())){
                Toast.makeText(getApplicationContext(),"付款账户不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(paymentNo.getText().toString())){
                Toast.makeText(getApplicationContext(),"付款账号不能为空",Toast.LENGTH_SHORT).show();
            }else if(intent.getStringExtra("type").equals("2")) {


                //修改
                new HttpUtils().postJson(Constant.APPURLS + "partner/partnerbank/update" + "/" + intent.getStringExtra("id"),params,token, new HttpUtils.HttpCallback() {

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
            else {
                Log.e("TAG", "login: "+object );
                //新增

                new HttpUtils().postJson(Constant.APPURLS+"partner/partnerbank/add",params,token,new HttpUtils.HttpCallback() {

                    @Override
                    public void onSuccess(String data) {
                        // TODO Auto-generated method stub
                        com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                        Message msg= Message.obtain(
                                mHandler,0,data
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
                            Toast.makeText(AddPartnerbankActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            Intent data=new Intent();
                            data.putExtra("freshen","y");
                            setResult(RESULT_OK,data);
                            finish();
                        }

                        break;

                    case 2:
                        JSONArray paymentTypearry=new JSONArray(msg.obj.toString());
                        dicts1.add(new AllData("0","请选择"));
                        for(int i=0;i<paymentTypearry.length();i++){
                            JSONObject object1=paymentTypearry.getJSONObject(i);
                            dicts1.add(new AllData(object1.getString("key"),object1.getString("value")));
                            arrAdapterpay1 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts1);
                            //设置样式
                            arrAdapterpay1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            paymentType.setAdapter(arrAdapterpay1);
                        }
                        if(intent.getStringExtra("type").equals("2")){
                            int d=arrAdapterpay1.getCount();
                            for(int j=0;j<d;j++){
                                if(intent.getStringExtra("paytype").equals(arrAdapterpay1.getItem(j).getStr())){
                                    paymentType.setAdapter(arrAdapterpay1);
                                    paymentType.setSelection(j,true);
                                }
                            }
                        }


                        break;

                    case 3:
                        JSONArray partnerInfoarry=new JSONArray(msg.obj.toString());
                        dicts2.add(new AllData("0","请选择"));
                        for(int i=0;i<partnerInfoarry.length();i++){
                            JSONObject object1=partnerInfoarry.getJSONObject(i);
                            dicts2.add(new AllData(object1.getString("id"),object1.getString("partnerName")));
                            arrAdapterpay2 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts2);
                            //设置样式
                            arrAdapterpay2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            partnerInfo.setAdapter(arrAdapterpay2);
                        }
                        if(intent.getStringExtra("type").equals("2")){
                            int d=arrAdapterpay2.getCount();
                            for(int j=0;j<d;j++){
                                if(intent.getStringExtra("partnerInfoId").equals(arrAdapterpay2.getItem(j).getStr())){
                                    partnerInfo.setAdapter(arrAdapterpay2);
                                    partnerInfo.setSelection(j,true);
                                }
                            }
                        }


                        break;

                    case 4:
                        JSONObject object=new JSONObject(msg.obj.toString());
                        paymentAccount.setText(object.getString("paymentAccount"));
                        paymentNo.setText(object.getString("paymentNo"));
                        break;

                    default:
                        Toast.makeText(AddPartnerbankActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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
