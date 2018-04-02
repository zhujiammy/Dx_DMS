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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhujia.dx_dms.Adapter.MyListAdapter;
import com.example.zhujia.dx_dms.Adapter.MySubListAdapter;
import com.example.zhujia.dx_dms.Data.AllData;
import com.example.zhujia.dx_dms.Data.Data;
import com.example.zhujia.dx_dms.R;
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
 * 新增资源信息
 */

public class AddBasicresourceActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text1;
    private Toolbar toolbar;
    private EditText resourceCode,resourceName;
    private SharedPreferences sharedPreferences;
    private TextView parentId;
    Intent intent;
    JSONObject object,pager;
    private LinearLayout lin_btn;
    private String token;
    private PopWindow popWindow;
    private   View customView;
    private Button ok_btn;
    private String parentIds;
    private Handler mHandler;
    private ListView listView;
    private ListView subListView;
    private MyListAdapter myAdapter;
    private MySubListAdapter subAdapter;
    private List<Data> mListData=new ArrayList<>();
    private List<Data> mListDatas=new ArrayList<>();
    private String text2,text3;



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

        parentId=(TextView) findViewById(R.id.parentId);
        resourceCode=(EditText)findViewById(R.id.resourceCode);
        resourceName=(EditText)findViewById(R.id.resourceName);
        lin_btn=(LinearLayout)findViewById(R.id.lin_btn);
        parentId.setOnClickListener(this);


        if(intent.getStringExtra("type")==null){
            text1.setText("修改资源信息");
            loadGet(intent.getStringExtra("id"));
            lin_btn.setVisibility(View.GONE);
        }else {
            text1.setText("新增资源信息");

            customView = View.inflate(AddBasicresourceActivity.this,R.layout.parent_xml, null);
            listView = (ListView) customView.findViewById(R.id.listView);
            subListView = (ListView) customView.findViewById(R.id.subListView);
            ok_btn=(Button)customView.findViewById(R.id.ok_btn);
            ok_btn.setOnClickListener(this);
            popWindow = new PopWindow.Builder(AddBasicresourceActivity.this)
                    .setStyle(PopWindow.PopWindowStyle.PopUp)
                    .setView(customView)
                    .create();
            loadbaseinfo();
            myAdapter = new MyListAdapter(getApplicationContext(), getData());


        }


    }
    private void loadbaseinfo(){
        new HttpUtils().Post(Constant.APPURLS+"/system/systemresource/query/tree",token,new HttpUtils.HttpCallback() {

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


    @SuppressLint("HandlerLeak")
    private List<Data>getData(){
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                try{
                    switch (msg.what) {

                        case 0:
                            //返回item类型数据
                            JSONArray reslutJSONObject=new JSONArray(msg.obj.toString());
                            mListData.clear();
                            fillDataToList(reslutJSONObject);
                            listView.setAdapter(myAdapter);
                            setSubList(0,mListData.get(0).getList());
                            text2=mListData.get(0).getLabel();
                            parentIds=mListData.get(0).getValue();
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1,
                                                        int position, long arg3) {
                                    setSubList(position,mListData.get(position).getList());
                                    parentIds=mListData.get(position).getValue();
                                    text2=mListData.get(position).getLabel();
                                }
                            });
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
        return mListData;
    }
    public void setSubList(int position,String list) {
        Log.e("TAG", "setSubList: "+list );
        mListDatas.clear();
        try {
            Data rechargData=null;
            JSONArray jsonArray=new JSONArray(list);
            for(int i=0;i<jsonArray.length();i++){
                rechargData=new Data();
                JSONObject object=jsonArray.getJSONObject(i);
                rechargData.setLabel(object.getString("label"));
                rechargData.setValue(object.getString("value"));
                mListDatas.add(rechargData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final int location = position;
        myAdapter.setSelectedPosition(position);
        myAdapter.notifyDataSetInvalidated();
        subAdapter = new MySubListAdapter(getApplicationContext(),mListDatas,
                position);
        subListView.setAdapter(subAdapter);
        text3="";
        subListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                subAdapter.setSelectedPosition(position);
                subAdapter.notifyDataSetInvalidated();
                parentIds=mListDatas.get(position).getValue();
                text3=mListDatas.get(position).getLabel();


            }
        });
    }
    private void fillDataToList(JSONArray data) throws JSONException {


        Data rechargData=null;
        for(int i=0;i<data.length();i++){
            rechargData=new Data();
            JSONObject object=data.getJSONObject(i);
            rechargData.setLabel(object.getString("label"));
            rechargData.setValue(object.getString("value"));
            rechargData.setList(object.getString("children"));
            mListData.add(rechargData);
        }



    }
    private void loadGet(String id){
            new HttpUtils().Post(Constant.APPURLS+"/system/systemresource/get"+"/"+id,token,new HttpUtils.HttpCallback() {

                @Override
                public void onSuccess(String data) {
                    // TODO Auto-generated method stub
                    com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                    Message msg= Message.obtain(
                            mHandlers,2,data
                    );
                    mHandlers.sendMessage(msg);
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

                object.put("parentId",parentIds);
                object.put("resourceCode",resourceCode.getText().toString());
                object.put("resourceName",resourceName.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String params=object.toString();
            //保存
             if(TextUtils.isEmpty(parentId.getText().toString())){
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
                                mHandlers, 0, data
                        );
                        mHandlers.sendMessage(msg);
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
                                mHandlers,0,data
                        );
                        mHandlers.sendMessage(msg);
                    }

                });
            }

        }



        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandlers=new Handler(){
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

        if(v==parentId){
            popWindow.show(customView);
        }

        if(v==ok_btn){
            popWindow.dismiss();
             if(!text2.equals("")){
                    parentId.setText(text2);
                }
                if(!text3.equals("")){
                    Log.e("TAG", "onItemClick: "+text3 );
                    parentId.setText(text2+" "+text3);
                }



        }
    }
}
