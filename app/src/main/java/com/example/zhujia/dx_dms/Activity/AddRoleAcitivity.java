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
 * 新增角色信息
 */

public class AddRoleAcitivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text1,expireTime;
    private Toolbar toolbar;
    private EditText roleName;
    private SharedPreferences sharedPreferences;
    private Spinner groupid;
    private String group_id;
    Intent intent;
    JSONObject object,pager;
    private String token;
    private List<AllData> dicts1 = new ArrayList<AllData>();
    private  ArrayAdapter<AllData> arrAdapterpay1;



    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addrole_xml);
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
        groupid=(Spinner) findViewById(R.id.groupid);
        roleName=(EditText)findViewById(R.id.roleName);
        groupid.setOnItemSelectedListener(listener);

        if(intent.getStringExtra("type")==null){
            text1.setText("修改资源信息");
            loadGet(intent.getStringExtra("id"));
            //roleName.setText(intent.getStringExtra("roleName"));
            loadbaseinfo();
        }else {
            text1.setText("新增资源信息");
            loadbaseinfo();
        }


    }

    //所属集团
    Spinner.OnItemSelectedListener listener=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            group_id=((AllData)groupid.getSelectedItem()).getStr();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    private void loadGet(String id){
        System.out.print(id);
        new HttpUtils().Post(Constant.APPURLS+"/system/systemrole/get"+"/"+id,token,new HttpUtils.HttpCallback() {

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
        new HttpUtils().postJson(Constant.APPURLS+"/group/groupbase/list","{}",token,new HttpUtils.HttpCallback() {

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
                object.put("roleName",roleName.getText().toString());
                object.put("groupId",group_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String params=object.toString();
            //保存
            if(group_id==null||group_id.equals("0")){
                Toast.makeText(getApplicationContext(),"集团不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(roleName.getText().toString())){
                Toast.makeText(getApplicationContext(),"角色名称不能为空",Toast.LENGTH_SHORT).show();
            }else if(intent.getStringExtra("type")==null) {
                //修改
                new HttpUtils().postJson(Constant.APPURLS + "/system/systemrole/update" + "/" + intent.getStringExtra("id"),params,token,new HttpUtils.HttpCallback() {

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

                new HttpUtils().postJson(Constant.APPURLS+"/system/systemrole/add",params,token,new HttpUtils.HttpCallback() {

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
                            Toast.makeText(AddRoleAcitivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            Intent data=new Intent();
                            data.putExtra("freshen","y");
                            setResult(RESULT_OK,data);
                            finish();
                        }

                        break;

                    case 1:
                        JSONArray companylist=new JSONArray(msg.obj.toString());
                        dicts1.add(new AllData("0","请选择"));
                        for(int i=0;i<companylist.length();i++){
                            JSONObject object1=companylist.getJSONObject(i);
                            dicts1.add(new AllData(object1.getString("id"),object1.getString("groupName")));
                            arrAdapterpay1 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts1);
                            //设置样式
                            arrAdapterpay1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            groupid.setAdapter(arrAdapterpay1);
                        }
                        if(intent.getStringExtra("type")==null){
                            int d=arrAdapterpay1.getCount();
                            for(int j=0;j<d;j++){
                                if(intent.getStringExtra("GroupId").equals(arrAdapterpay1.getItem(j).getStr())){
                                    groupid.setAdapter(arrAdapterpay1);
                                    groupid.setSelection(j,true);
                                }
                            }
                        }


                        break;

                    case  2:
                        JSONObject basicresource =new JSONObject(msg.obj.toString());
                        roleName.setText(basicresource.getString("roleName"));
                        break;

                    default:
                        Toast.makeText(AddRoleAcitivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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
