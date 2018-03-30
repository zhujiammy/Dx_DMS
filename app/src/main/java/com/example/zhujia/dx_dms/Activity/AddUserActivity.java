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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.example.zhujia.dx_dms.Tools.MultiSelectionSpinner;
import com.example.zhujia.dx_dms.Tools.Net.Constant;
import com.example.zhujia.dx_dms.Tools.Net.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZHUJIA on 2018/3/15.
 * 新增用户
 */

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text1,expireTime;
    private Toolbar toolbar;
    private EditText mobilePhone,password,realName,userName;
    private SharedPreferences sharedPreferences;
    private Spinner group;
    private String group_id,company_Ids,role_Ids;
    private MultiSelectionSpinner companyIds,roleIds;
    Intent intent;
    net.sf.json.JSONObject object,pager;
    static String groupId;
    private int conuts;
    private String token;
    private List<String> ID=new ArrayList<>();
    private List<String> Name=new ArrayList<>();
    private List<String> IDs=new ArrayList<>();
    private List<String> Names=new ArrayList<>();
    private List<AllData> dicts1 = new ArrayList<AllData>();
    private  ArrayAdapter<AllData> arrAdapterpay1;
    private List<AllData> dicts2 = new ArrayList<AllData>();
    private  ArrayAdapter<AllData> arrAdapterpay2;
    private List<AllData> dicts3 = new ArrayList<AllData>();
    private  ArrayAdapter<AllData> arrAdapterpay3;
    private List<String>groupCompanyid=new ArrayList<>();
    private List<String>Role=new ArrayList<>();
    private LinearLayout password_lin;




    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adduser_xml);
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
        group=(Spinner) findViewById(R.id.group);
        mobilePhone=(EditText)findViewById(R.id.mobilePhone);
        password=(EditText)findViewById(R.id.password);
        realName=(EditText)findViewById(R.id.realName);
        userName=(EditText)findViewById(R.id.userName);
        password_lin=(LinearLayout)findViewById(R.id.password_lin);
        group.setOnItemSelectedListener(listener);
        companyIds=(MultiSelectionSpinner)findViewById(R.id.companyIds);
        roleIds=(MultiSelectionSpinner)findViewById(R.id.roleIds);




        if(intent.getStringExtra("type").equals("2")){
            text1.setText("修改用户");
            conuts=0;
            loaduserinfo(intent.getStringExtra("id"));
            loadbaseinfo();
            userName.setEnabled(false);
            password_lin.setVisibility(View.GONE);
        }else if(intent.getStringExtra("type").equals("1")) {
            text1.setText("新增用户");
            userName.addTextChangedListener(watcher);
            loadbaseinfo();
        }


    }

    private  void loaduserinfo(String id){
        new HttpUtils().Post(Constant.APPURLS+"/system/systemuser/get"+"/"+id,token,new HttpUtils.HttpCallback() {

            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,5,data
                );
                mHandler.sendMessage(msg);
            }

        });
    }

    //判断用户名是否存在
    TextWatcher watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(s.length()<6){
                Toast.makeText(AddUserActivity.this,"用户名不能小于6位字符",Toast.LENGTH_SHORT).show();
            }else {
                object = new net.sf.json.JSONObject();
                pager=new net.sf.json.JSONObject();
                Log.e("TAG", "afterTextChanged: "+s );
                try {
                    object.put("userName",s.toString());
                } catch (net.sf.json.JSONException e) {
                    e.printStackTrace();
                }
                String params=object.toString();
                new HttpUtils().postJson(Constant.APPURLS+"/system/systemuser/list",params,token,new HttpUtils.HttpCallback() {

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


        }
    };

    //所属集团
    Spinner.OnItemSelectedListener listener=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            group_id=((AllData)group.getSelectedItem()).getStr();
            loadcompany_Ids(group_id);
            loadrole_Ids(group_id);


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };




    //集团集合
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


    //公司集合
    private void loadcompany_Ids(String group_id){
        object = new net.sf.json.JSONObject();
        pager=new net.sf.json.JSONObject();
        try {
            //object.put("id","1");
            net.sf.json.JSONArray jsonArray1= new net.sf.json.JSONArray();
            jsonArray1.element(group_id);
            object.put("searchGroupId",jsonArray1.toString().replace("\"","").replace("\"",""));



        } catch (net.sf.json.JSONException e) {
            e.printStackTrace();
        }
        String params=object.toString();
        Log.e("TAG", "params: "+object );
        new HttpUtils().postJson(Constant.APPURLS+"/group/groupcompany/list/",params,token,new HttpUtils.HttpCallback() {

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


    //角色集合
    private void loadrole_Ids(String role_Ids){
        try {
            //object.put("id","1");
            net.sf.json.JSONArray jsonArray1= new net.sf.json.JSONArray();
            jsonArray1.element(role_Ids);
            object.put("searchGroupId",jsonArray1.toString().replace("\"","").replace("\"",""));
        } catch (net.sf.json.JSONException e) {
            e.printStackTrace();
        }
        String params=object.toString();
        Log.e("TAG", "params: "+object );
        new HttpUtils().postJson(Constant.APPURLS+"/system/systemrole/list",params,token,new HttpUtils.HttpCallback() {

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

            object = new net.sf.json.JSONObject();

            //保存
            if(intent.getStringExtra("type").equals("1")){
                try {
                    net.sf.json.JSONArray jsonArray1= new net.sf.json.JSONArray();
                    jsonArray1.element(companyIds.getItemsAsString());
                    object.put("companyIds",jsonArray1.toString().replace("\"","").replace("\"",""));
                    object.put("groupId",group_id);
                    object.put("mobilePhone",mobilePhone.getText().toString());
                    object.put("userName",userName.getText().toString());
                    object.put("password",password.getText().toString());
                    object.put("realName",realName.getText().toString());

                    net.sf.json.JSONArray jsonArray2=new net.sf.json.JSONArray();
                    jsonArray2.element(roleIds.getItemsAsString());
                    object.put("roleIds",jsonArray2.toString().replace("\"","").replace("\"",""));
                } catch (net.sf.json.JSONException e) {
                    e.printStackTrace();
                }
                String params=object.toString();
                if(group_id==null||group_id.equals("0")){
                    Toast.makeText(getApplicationContext(),"所属集团不能为空",Toast.LENGTH_SHORT).show();
                }else if(companyIds.getItemsAsString().equals("")||companyIds.getItemsAsString().equals("n")){
                    Toast.makeText(getApplicationContext(),"所属公司不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(roleIds.getItemsAsString().equals("")||roleIds.getItemsAsString().equals("n")){
                    Toast.makeText(getApplicationContext(),"角色不能为空",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(userName.getText().toString())){
                    Toast.makeText(getApplicationContext(),"用户名不能为空",Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(mobilePhone.getText().toString())){
                    Toast.makeText(getApplicationContext(),"手机号码不能为空",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();

                } else if(TextUtils.isEmpty(realName.getText().toString())){
                    Toast.makeText(getApplicationContext(),"真实姓名不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    new HttpUtils().postJson(Constant.APPURLS+"/system/systemuser/add",params,token,new HttpUtils.HttpCallback() {

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




            }else if(intent.getStringExtra("type").equals("2")){
                //修改

                try {
                    net.sf.json.JSONArray jsonArray1= new net.sf.json.JSONArray();
                    jsonArray1.element(companyIds.getItemsAsString());
                    object.put("companyIds",jsonArray1.toString().replace("\"","").replace("\"",""));
                    object.put("groupId",group_id);
                    object.put("mobilePhone",mobilePhone.getText().toString());
                    object.put("realName",realName.getText().toString());

                    net.sf.json.JSONArray jsonArray2=new net.sf.json.JSONArray();
                    jsonArray2.element(roleIds.getItemsAsString());
                    object.put("roleIds",jsonArray2.toString().replace("\"","").replace("\"",""));
                } catch (net.sf.json.JSONException e) {
                    e.printStackTrace();
                }
                String params=object.toString();
                if(group_id==null||group_id.equals("0")){
                    Toast.makeText(getApplicationContext(),"所属集团不能为空",Toast.LENGTH_SHORT).show();
                }else if(companyIds.getItemsAsString().equals("")||companyIds.getItemsAsString().equals("n")){
                    Toast.makeText(getApplicationContext(),"所属公司不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(roleIds.getItemsAsString().equals("")||roleIds.getItemsAsString().equals("n")){
                    Toast.makeText(getApplicationContext(),"角色不能为空",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(mobilePhone.getText().toString())){
                    Toast.makeText(getApplicationContext(),"手机号码不能为空",Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(realName.getText().toString())){
                    Toast.makeText(getApplicationContext(),"真实姓名不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    new HttpUtils().postJson(Constant.APPURLS + "/system/systemuser/update" + "/" + intent.getStringExtra("id"),params,token, new HttpUtils.HttpCallback() {

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
                            Toast.makeText(AddUserActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            Intent data=new Intent();
                            data.putExtra("freshen","y");
                            setResult(RESULT_OK,data);
                            finish();
                        }

                        break;

                    case 1:
                        JSONArray groulist=new JSONArray(msg.obj.toString());
                        dicts1.add(new AllData("0","请选择"));
                        for(int i=0;i<groulist.length();i++){
                            JSONObject object1=groulist.getJSONObject(i);
                            dicts1.add(new AllData(object1.getString("id"),object1.getString("groupName")));
                            arrAdapterpay1 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts1);
                            //设置样式
                            arrAdapterpay1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            group.setAdapter(arrAdapterpay1);
                        }



                        break;

                    case 2:
                        if(!group_id.equals(groupId)){
                            conuts=1;
                        }
                        JSONArray companylist=new JSONArray(msg.obj.toString());
                        Log.e("companylist", "handleMessage: "+ companylist.length());
                        if(group_id.equals("0")||companylist.length()==0){
                            ID.clear();
                            Name.clear();
                                companyIds.setEnabled(false);
                                companyIds.set_def();
                        }else {
                            ID.clear();
                            Name.clear();
                            companyIds.set_defs();
                            companyIds.setEnabled(true);
                            for(int i=0;i<companylist.length();i++){
                                JSONObject object1=companylist.getJSONObject(i);
                                ID.add(object1.getString("id"));
                                Name.add(object1.getString("companyName"));
                                companyIds.setItemsID(Name,ID);
                            }
                            if(conuts==0){
                                if(groupCompanyid.size()>0){
                                    companyIds.setSelection(groupCompanyid);
                                }
                            }

                        }





                        break;

                    case 3:
                        if(!group_id.equals(groupId)){
                            conuts=1;
                        }
                        JSONArray roleNamelist=new JSONArray(msg.obj.toString());
                        Log.e("roleNamelist", "handleMessage: "+ roleNamelist.length());
                        if(group_id.equals("0")||roleNamelist.length()==0){
                            IDs.clear();
                            Names.clear();
                            roleIds.setEnabled(false);
                            roleIds.set_def();
                        }else {
                            ID.clear();
                            Name.clear();
                            roleIds.set_defs();
                            roleIds.setEnabled(true);
                            for(int i=0;i<roleNamelist.length();i++){
                                JSONObject object1=roleNamelist.getJSONObject(i);
                                IDs.add(object1.getString("id"));
                                Names.add(object1.getString("roleName"));
                                roleIds.setItemsID(Names,IDs);
                            }
                            if(conuts==0){
                                if(Role.size()>0){
                                    roleIds.setSelection(Role);
                                }
                            }

                        }



                        break;
                    case  4:
                        JSONArray UserNames =new JSONArray(msg.obj.toString());
                        if(UserNames.length()>0){
                            Toast.makeText(AddUserActivity.this,"用户名已存在！",Toast.LENGTH_LONG).show();
                        }

                        break;

                    case  5:
                        JSONObject Userinfo =new JSONObject(msg.obj.toString());
                            userName.setText(Userinfo.getString("userName"));
                            realName.setText(Userinfo.getString("realName"));
                            mobilePhone.setText(Userinfo.getString("mobilePhone"));
                            groupId=Userinfo.getString("groupId");
                            JSONArray groupCompanys=Userinfo.getJSONArray("groupCompanys");
                            for(int i=0;i<groupCompanys.length();i++){
                                JSONObject object=groupCompanys.getJSONObject(i);
                                groupCompanyid.add(object.getString("companyName"));
                            }
                            JSONArray Rolelist=Userinfo.getJSONArray("systemRoleList");
                        for(int j=0;j<Rolelist.length();j++){
                            JSONObject object=Rolelist.getJSONObject(j);
                            Role.add(object.getString("roleName"));
                        }
                        if(intent.getStringExtra("type").equals("2")){
                            Log.e("TAG", "groupId: "+groupId );
                            int d=arrAdapterpay1.getCount();
                            for(int j=0;j<d;j++){
                                if(groupId!=null){
                                    if(groupId.equals(arrAdapterpay1.getItem(j).getStr())){
                                        group.setAdapter(arrAdapterpay1);
                                        group.setSelection(j,true);
                                    }
                                }

                            }


                        }

                        break;
                    default:
                        Toast.makeText(AddUserActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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
