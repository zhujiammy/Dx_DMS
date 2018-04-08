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
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhujia.dx_dms.Adapter.CityAdapter;
import com.example.zhujia.dx_dms.Adapter.DistrictAdapter;
import com.example.zhujia.dx_dms.Adapter.MyListAdapter;
import com.example.zhujia.dx_dms.Adapter.MySubListAdapter;
import com.example.zhujia.dx_dms.Adapter.ProvinceAdapter;
import com.example.zhujia.dx_dms.Data.Data;
import com.example.zhujia.dx_dms.R;
import com.example.zhujia.dx_dms.Tools.CitySelect1Activity;
import com.example.zhujia.dx_dms.Tools.Net.Constant;
import com.example.zhujia.dx_dms.Tools.Net.HttpUtils;
import com.hmy.popwindow.PopWindow;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZHUJIA on 2018/3/15.
 * 新增客户信息
 */

public class AddCustomerLsitActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text1,province_city_dis;
    private Toolbar toolbar;
    private EditText partnerName,addr,shopName,shopAddr,email,mobile,phone,qq;
    Intent intent;
    JSONObject object;
    private SharedPreferences sharedPreferences;
    private String token;
    private PopWindow popWindow;
    private   View customView;
    private Button ok_btn;
    private Handler mhandler;
    private String province,city,district;
    private ListView province_listview;
    private ListView city_ListView;
    private ListView district_ListView;
    private ProvinceAdapter provinceAdapter;
    private CityAdapter cityAdapter;
    private DistrictAdapter districtAdapter;
    private List<Data> mListData1=new ArrayList<>();
    private List<Data> mListData2=new ArrayList<>();
    private List<Data> mListData3=new ArrayList<>();




    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aaddcustomer_xml);
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
        partnerName=(EditText)findViewById(R.id.partnerName);
        addr=(EditText)findViewById(R.id.addr);
        shopName=(EditText)findViewById(R.id.shopName);
        shopAddr=(EditText)findViewById(R.id.shopAddr);
        email=(EditText)findViewById(R.id.email);
        mobile=(EditText)findViewById(R.id.mobile);
        phone=(EditText)findViewById(R.id.phone);
        qq=(EditText)findViewById(R.id.qq);
        province_city_dis=(TextView)findViewById(R.id.province_city_dis);
        province_city_dis.setOnClickListener(this);
        customView = View.inflate(AddCustomerLsitActivity.this,R.layout.city_xml, null);
        popWindow = new PopWindow.Builder(AddCustomerLsitActivity.this)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .setView(customView)
                .create();
        loadcityinfo();
        ok_btn=(Button)customView.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(this);
        province_listview=(ListView)customView.findViewById(R.id.province_listview);
        city_ListView=(ListView)customView.findViewById(R.id.city_ListView);
        district_ListView=(ListView)customView.findViewById(R.id.district_ListView);
        provinceAdapter = new ProvinceAdapter(getApplicationContext(), getData());
        if(intent.getStringExtra("type").equals("2")){
            text1.setText("修改客户");
            loadGet(intent.getStringExtra("id"));
        }else {
            text1.setText("新增客户");
        }


    }

    private void loadcityinfo(){
        new HttpUtils().Post(Constant.APPURLS+"common/address",token,new HttpUtils.HttpCallback() {

            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mhandler,4,data
                );
                mhandler.sendMessage(msg);
            }

        });
    }

    @SuppressLint("HandlerLeak")
    private List<Data> getData(){
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                try{
                    switch (msg.what) {

                        case 4:
                            //返回item类型数据
                            JSONArray reslutJSONObject=new JSONArray(msg.obj.toString());
                            mListData1.clear();
                            fillDataToList(reslutJSONObject);
                            province_listview.setAdapter(provinceAdapter);
                            setSubList(0,mListData1.get(0).getList());
                            province=mListData1.get(0).getValue();
                            province=mListData1.get(0).getValue();
                            province_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1,
                                                        int position, long arg3) {
                                    setSubList(position,mListData1.get(position).getList());
                                    province=mListData1.get(position).getValue();
                                    province=mListData1.get(position).getValue();
                                }
                            });
                            break;
                        default:
                            Toast.makeText(AddCustomerLsitActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        return mListData1;
    }

    private void fillDataToList(JSONArray data) throws JSONException {


        Data rechargData=null;
        for(int i=0;i<data.length();i++){
            rechargData=new Data();
            JSONObject object=data.getJSONObject(i);
            rechargData.setKey(object.getString("key"));
            rechargData.setValue(object.getString("value"));
            rechargData.setList(object.getString("children"));
            mListData1.add(rechargData);
        }



    }


    //市
    public void setSubList(int position,String list) {
        Log.e("TAG", "setSubList: "+list );
        mListData2.clear();
        try {
            Data rechargData=null;
            JSONArray jsonArray=new JSONArray(list);
            for(int i=0;i<jsonArray.length();i++){
                rechargData=new Data();
                JSONObject object=jsonArray.getJSONObject(i);
                rechargData.setKey(object.getString("key"));
                rechargData.setValue(object.getString("value"));
                rechargData.setList3(object.getString("children"));
                mListData2.add(rechargData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final int location = position;
        provinceAdapter.setSelectedPosition(position);
        provinceAdapter.notifyDataSetInvalidated();
        cityAdapter = new CityAdapter(getApplicationContext(),mListData2,
                position);
        city_ListView.setAdapter(cityAdapter);
        city="";
        setSubLists(0,mListData2.get(0).getList3());
        city=mListData2.get(0).getValue();
        city=mListData2.get(0).getValue();
        city_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                setSubLists(position,mListData2.get(position).getList3());
                cityAdapter.setSelectedPosition(position);
                cityAdapter.notifyDataSetInvalidated();
                city=mListData2.get(position).getValue();
                city=mListData2.get(position).getKey();


            }
        });
    }


    //区
    public void setSubLists(int position,String list) {
        Log.e("TAG", "setSubList: "+list );
        mListData3.clear();
        try {
            Data rechargData=null;
            JSONArray jsonArray=new JSONArray(list);
            for(int i=0;i<jsonArray.length();i++){
                rechargData=new Data();
                JSONObject object=jsonArray.getJSONObject(i);
                rechargData.setKey(object.getString("key"));
                rechargData.setValue(object.getString("value"));
                mListData3.add(rechargData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final int location = position;
        cityAdapter.setSelectedPosition(position);
        cityAdapter.notifyDataSetInvalidated();
        districtAdapter = new DistrictAdapter(getApplicationContext(),mListData3,
                position);
        district_ListView.setAdapter(districtAdapter);
        district="";
        district_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                districtAdapter.setSelectedPosition(position);
                districtAdapter.notifyDataSetInvalidated();
                district=mListData3.get(position).getValue();
                district=mListData3.get(position).getKey();


            }
        });
    }



    private void loadGet(String id){
        System.out.print(id);
        new HttpUtils().Post(Constant.APPURLS+"partner/partnerinfo/get"+"/"+id,token,new HttpUtils.HttpCallback() {

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
                object.put("partnerName",partnerName.getText().toString());
                object.put("province",province);
                object.put("city",city);
                object.put("district",district);
                object.put("addr",addr.getText().toString());
                object.put("shopName",shopName.getText().toString());
                object.put("shopAddr",shopAddr.getText().toString());
                object.put("email",email.getText().toString());
                object.put("mobile",mobile.getText().toString());
                object.put("phone",phone.getText().toString());
                object.put("qq",qq.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String params=object.toString();
            //保存
            if(TextUtils.isEmpty(partnerName.getText().toString())){
                Toast.makeText(getApplicationContext(),"客户名称不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(province_city_dis.getText().toString())){
                Toast.makeText(getApplicationContext(),"省市区不能为空",Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(addr.getText().toString())){
                Toast.makeText(getApplicationContext(),"详细地址不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(shopName.getText().toString())){
                Toast.makeText(getApplicationContext(),"店铺名称不能为空",Toast.LENGTH_SHORT).show();
            }
            else if(intent.getStringExtra("type").equals("2")) {

                //修改
                new HttpUtils().postJson(Constant.APPURLS + "partner/partnerinfo/update" + "/" + intent.getStringExtra("id"),params,token, new HttpUtils.HttpCallback() {

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

                new HttpUtils().postJson(Constant.APPURLS+"partner/partnerinfo/add",params,token,new HttpUtils.HttpCallback() {

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
                            Toast.makeText(AddCustomerLsitActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            Intent data=new Intent();
                            data.putExtra("freshen","y");
                            setResult(RESULT_OK,data);
                            finish();
                        }
                        break;
                    case 2:

                        JSONObject object=new JSONObject(msg.obj.toString());
                        partnerName.setText(object.getString("partnerName"));
                        province_city_dis.setText(object.getString("province")+" "+object.getString("city")+" "+object.getString("district"));
                        addr.setText(object.getString("addr"));
                        shopName.setText(object.getString("shopName"));
                        shopAddr.setText(object.getString("shopAddr"));
                        email.setText(object.getString("email"));
                        mobile.setText(object.getString("mobile"));
                        phone.setText(object.getString("phone"));
                        qq.setText(object.getString("qq"));

                        break;



                    default:
                        Toast.makeText(AddCustomerLsitActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onClick(View v) {

        if(v==province_city_dis){
            popWindow.show();
        }

        if(v==ok_btn){

            if(!province.equals("")&&!city.equals("")&&!district.equals("")){
                province_city_dis.setText(province+" "+city+" "+district);
                popWindow.dismiss();
            }else {
                Toast.makeText(getApplicationContext(),"地址还没选择完整!",Toast.LENGTH_SHORT).show();
            }




        }
    }
}
