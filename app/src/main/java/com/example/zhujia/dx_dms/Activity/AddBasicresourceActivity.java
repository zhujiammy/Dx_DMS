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
import android.widget.LinearLayout;
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
 * 新增资源信息
 */

public class AddBasicresourceActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text1,expireTime;
    private Toolbar toolbar;
    private EditText resourceCode,resourceName;
    private SharedPreferences sharedPreferences;
    private Spinner parentId;
    private String parent_Id;
    Intent intent;
    JSONObject object,pager;
    private LinearLayout lin_btn;
    private String token;
    private List<AllData> dicts2 = new ArrayList<AllData>();
    private  ArrayAdapter<AllData> arrAdapterpay2;



    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addbasicresource_xml);
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

        initUI();
    }


    private void initUI(){
        text1=(TextView)findViewById(R.id.text1);

        parentId=(Spinner) findViewById(R.id.parentId);
        resourceCode=(EditText)findViewById(R.id.resourceCode);
        resourceName=(EditText)findViewById(R.id.resourceName);
        lin_btn=(LinearLayout)findViewById(R.id.lin_btn);

        parentId.setOnItemSelectedListener(listener1);


        if(intent.getStringExtra("type")==null){
            text1.setText("修改资源信息");
            loadGet(intent.getStringExtra("id"));
            lin_btn.setVisibility(View.GONE);
            loadbaseinfo();
        }else {
            text1.setText("新增资源信息");
            loadbaseinfo();

        }


    }





    Spinner.OnItemSelectedListener listener1=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            parent_Id=((AllData)parentId.getSelectedItem()).getStr();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void loadGet(String id){
            new HttpUtils().Post(Constant.APPURLS+"/system/systemresource/get"+"/"+id,token,new HttpUtils.HttpCallback() {

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
        new HttpUtils().Post(Constant.APPURLS+"/system/systemresource/query/parent",token,new HttpUtils.HttpCallback() {

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
                if(parent_Id==null||parent_Id.equals("n")){
                    object.put("parentId","");
                }else {
                    object.put("parentId",parent_Id);
                }

                object.put("resourceCode",resourceCode.getText().toString());
                object.put("resourceName",resourceName.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String params=object.toString();
            //保存
             if(parent_Id==null||parent_Id.equals("0")){
                Toast.makeText(getApplicationContext(),"上级资源不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(resourceCode.getText().toString())){
                Toast.makeText(getApplicationContext(),"资源代码不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(resourceName.getText().toString())){
                Toast.makeText(getApplicationContext(),"资源名称不能为空",Toast.LENGTH_SHORT).show();
            }else if(intent.getStringExtra("type")==null) {


                //修改
                new HttpUtils().postJson(Constant.APPURLS + "/system/systemresource/update" + "/" + intent.getStringExtra("id"),params,token,new HttpUtils.HttpCallback() {

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

                new HttpUtils().postJson(Constant.APPURLS+"/system/systemresource/add",params,token,new HttpUtils.HttpCallback() {

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
                            Toast.makeText(AddBasicresourceActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            Intent data=new Intent();
                            data.putExtra("freshen","y");
                            setResult(RESULT_OK,data);
                            finish();
                        }

                        break;


                    case  2:
                        JSONObject basicresource =new JSONObject(msg.obj.toString());
                        //parentId.setText(basicresource.getString("parentId"));
                        resourceName.setText(basicresource.getString("resourceName"));
                        resourceCode.setText(basicresource.getString("resourceCode"));
                        break;

                    case  1:
                        JSONArray basicresources=new JSONArray(msg.obj.toString());

                            dicts2.clear();
                            dicts2.add(new AllData("0","请选择"));
                            for(int i=0;i<basicresources.length();i++){
                                JSONObject object1=basicresources.getJSONObject(i);
                                dicts2.add(new AllData(object1.getString("id"),object1.getString("name")));
                                arrAdapterpay2 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts2);
                                //设置样式
                                arrAdapterpay2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                parentId.setAdapter(arrAdapterpay2);
                                parentId.setEnabled(true);

                            if(intent.getStringExtra("type")==null){
                                int d=arrAdapterpay2.getCount();
                                for(int j=0;j<d;j++){
                                    if(intent.getStringExtra("parentId").equals(arrAdapterpay2.getItem(j).getStr())){
                                        parentId.setAdapter(arrAdapterpay2);
                                        parentId.setSelection(j,true);
                                    }
                                }
                            }

                        }


                        break;

                    default:
                        Toast.makeText(AddBasicresourceActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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
