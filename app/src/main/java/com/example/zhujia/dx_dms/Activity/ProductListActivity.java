package com.example.zhujia.dx_dms.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhujia.dx_dms.Adapter.ProductListAdapter;
import com.example.zhujia.dx_dms.Data.Data;
import com.example.zhujia.dx_dms.R;
import com.example.zhujia.dx_dms.Tools.Net.Constant;
import com.example.zhujia.dx_dms.Tools.Net.HttpUtils;
import com.example.zhujia.dx_dms.Tools.OnLoadMoreListener;
import com.example.zhujia.dx_dms.Tools.OnRefreshListener;
import com.example.zhujia.dx_dms.Tools.SuperRefreshRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZHUJIA on 2018/3/14.
 * 产品列表
 */

public class ProductListActivity extends AppCompatActivity implements View.OnClickListener,OnRefreshListener,OnLoadMoreListener {


    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private int pageindex=1;
    JSONObject object;
    JSONObject pager;
    private ViewGroup.LayoutParams lp;
    boolean hasMoreData;
    private Handler mHandler;
    private List<Data> mListData=new ArrayList<>();
    JSONObject reslutJSONObject;
    JSONArray contentjsonarry;
    public static final int  REQUEST_CODE=1001;
    private ProductListAdapter adapter;
    private  int total;
    private Intent intent;
    private SuperRefreshRecyclerView recyclerView;
    private TextView text;
    private String type,token,list,list1;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productlist_xml);
        intent=getIntent();
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
        sharedPreferences =getSharedPreferences("Session", Context.MODE_APPEND);
        token=sharedPreferences.getString("token","");
        type=intent.getStringExtra("type");
        initUI();
        loaddata();//加载列表数据
        loadstatue();
        adapter=new ProductListAdapter(this,getData());

    }


    private void initUI(){

        //初始化
        recyclerView= (SuperRefreshRecyclerView)findViewById(R.id.recyclerview);
        recyclerView.init(this,this);
        recyclerView.setRefreshEnabled(true);
        recyclerView.setLoadingMoreEnable(true);
        recyclerView.setHasFixedSize(true);
        text=(TextView) findViewById(R.id.text1);

        text.setText("产品列表");



    }


    private void  loadstatue(){
        new HttpUtils().Post(Constant.APPURLS+"product/productsku/query/status",token,new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,6,data
                );
                mHandler.sendMessage(msg);
            }

        });
    }

    private void loaddata(){
        object = new JSONObject();
        pager=new JSONObject();
      /*  try {
            //object.put("id","1");
           *//* JSONArray jsonArray=new JSONArray();
            pager.put("searchCreateTimeBegin","");
            pager.put("searchCreateTimeEnd","");
            pager.put("likeCompanyCode","");
            pager.put("likeCompanyName","");
            pager.put("likeShortName","");
            pager.put("searchGroupId",jsonArray);
            pager.put("searchStatus",jsonArray);*//*
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        String json=pager.toString();
        String params="currentPage="+pageindex+"&"+"pageSize="+"15"+"&"+"sortName="+"id"+"&"+"sortType="+"desc";
        new HttpUtils().postJson(Constant.APPURLS+"product/productsku/page?"+params,json,token,new HttpUtils.HttpCallback() {

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
        Log.e("TAG", "login: "+json);

    }


    //加载更多
    private void initItemMoreData() {
        object = new JSONObject();
        pager=new JSONObject();
     /*   try {
            //object.put("id","1");
            JSONArray jsonArray=new JSONArray();
            pager.put("searchCreateTimeBegin","");
            pager.put("searchCreateTimeEnd","");
            pager.put("likeCompanyCode","");
            pager.put("likeCompanyName","");
            pager.put("likeShortName","");
            pager.put("searchGroupId",jsonArray);
            pager.put("searchStatus",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        String json=pager.toString();
        String params="currentPage="+pageindex+1+"&"+"pageSize="+"15"+"&"+"sortName="+"id"+"&"+"sortType="+"desc";
        new HttpUtils().postJson(Constant.APPURLS+"product/productsku/page?"+params,json,token,new HttpUtils.HttpCallback() {

            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");
                if ((null == data) || (data.equals(""))) {
                    // 网络连接异常

                    mHandler.sendEmptyMessage(9);

                }else {
                    JSONObject resulutJsonobj;

                    try
                    {

                        resulutJsonobj=new JSONObject(data);
                        if(!resulutJsonobj.isNull("rows")){
                            contentjsonarry=resulutJsonobj.getJSONArray("rows");
                            if(contentjsonarry.length()<0){
                                hasMoreData=false;
                            }
                            pageindex=pageindex+1;
                            hasMoreData=true;
                            fillDataToList(resulutJsonobj);
                            if(!hasMoreData){
                                mHandler.sendEmptyMessage(4);
                            }else {
                                mHandler.sendEmptyMessage(3);
                            }
                        }else {
                            recyclerView.setLoadingMore(false);
                        }


                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void fillDataToList(JSONObject data) throws JSONException {


        if(!data.isNull("rows")){
            contentjsonarry=data.getJSONArray("rows");
            Data rechargData=null;
            for(int i=0;i<contentjsonarry.length();i++){
                rechargData=new Data();
                JSONObject object=contentjsonarry.getJSONObject(i);
                rechargData.setId(object.getString("id"));
                rechargData.setModel(object.getString("model"));
                rechargData.setItemNo(object.getString("itemNo"));
                rechargData.setFcno(object.getString("fcno"));
                rechargData.setSkuName(object.getString("skuName"));
                rechargData.setListPric(object.getString("listPrice"));
                rechargData.setStatus(object.getString("status"));
                rechargData.setWeight(object.getString("weight"));
                rechargData.setPackageLength(object.getString("packageLength"));
                rechargData.setPackageWidth(object.getString("packageWidth"));
                rechargData.setPackageHeight(object.getString("packageHeight"));
                rechargData.setMainImgUrl(object.getString("mainImgUrl"));
                rechargData.setCompanyId(object.getString("companyId"));
                rechargData.setCreateTime(object.getString("createTime"));
                rechargData.setStatuslist(list1);
                mListData.add(rechargData);
            }
        }


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
                            reslutJSONObject=new JSONObject(msg.obj.toString());
                            mListData.clear();
                            fillDataToList(reslutJSONObject);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            recyclerView.showData();
                            recyclerView.setRefreshing(false);
                            break;
                        case 1:
                            list=msg.obj.toString();
                            break;

                        case 2:
                            reslutJSONObject=new JSONObject(msg.obj.toString());
                            if(reslutJSONObject.getString("code").equals("200")){
                                Toast.makeText(getApplicationContext(),"操作成功",Toast.LENGTH_SHORT).show();
                                recyclerView.setRefreshing(true);
                            }
                            break;
                        case 3:
                            adapter.notifyDataSetChanged();
                            recyclerView.setLoadingMore(false);
                            break;
                        case 4:
                            adapter.notifyDataSetChanged();
                            break;
                        case 6:
                            list1=msg.obj.toString();
                            break;
                        default:
                            Toast.makeText(ProductListActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        return mListData;
    }

    @Override
    public void onClick(View v) {


    }


    public void showNormalDialogproduct(final int position, final String id, String msgs, final String url){

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ProductListActivity.this);
        normalDialog.setMessage(msgs);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        new HttpUtils().Post(Constant.APPURLS+url+id,token,new HttpUtils.HttpCallback() {

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
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.seach_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.seach_btn) {
            loaddata();
        }
        if(id==R.id.add){
            //新增信息
            intent=new Intent(this,AddProduct.class);
            intent.putExtra("type","1");
            startActivityForResult(intent,REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==REQUEST_CODE&&resultCode==RESULT_OK){
            recyclerView.setRefreshing(true);
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mListData.clear();
                pageindex=1;
                loaddata();
            }
        },1000);
    }

    @Override
    public void onLoadMore() {
        initItemMoreData();
    }
}
