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

import com.example.zhujia.dx_dms.Adapter.CustomerLsitAdapter;
import com.example.zhujia.dx_dms.Adapter.PartnerbankAdapter;
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
 * 销价列表
 */

public class PartnerSkuActivity extends AppCompatActivity implements View.OnClickListener,OnRefreshListener,OnLoadMoreListener {


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
    private PartnerbankAdapter adapter;
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
       // loadstatues();
        //loadstatue();
       // adapter=new PartnerbankAdapter(this,getData());

    }


    private void initUI(){

        //初始化
        recyclerView= (SuperRefreshRecyclerView)findViewById(R.id.recyclerview);
        recyclerView.init(this,this);
        recyclerView.setRefreshEnabled(true);
        recyclerView.setLoadingMoreEnable(true);
        recyclerView.setHasFixedSize(true);
        text=(TextView) findViewById(R.id.text1);
        text.setText("销价列表");



    }

    //客户列表
    private void  loadstatues(){
        new HttpUtils().postJson(Constant.APPURLS+"partner/partnerinfo/list","{}",token,new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,7,data
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
                        mHandler,6,data
                );
                mHandler.sendMessage(msg);
            }

        });
    }



    private void loaddata(){
        object = new JSONObject();
        pager=new JSONObject();
        try {
            //object.put("id","1");
            JSONArray jsonArray=new JSONArray();
            pager.put("companyId","");
            pager.put("searchSalePriceBegin","");
            pager.put("searchSalePriceEnd","");
            pager.put("searchPartnerInfoId",jsonArray);
            pager.put("searchId",jsonArray);
            pager.put("searchSkuId",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json=pager.toString();
        String params="currentPage="+pageindex+"&"+"pageSize="+"15"+"&"+"sortName="+"id"+"&"+"sortType="+"desc";
        new HttpUtils().postJson(Constant.APPURLS+"partner/partnersku/page?"+params,json,token,new HttpUtils.HttpCallback() {

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
        try {
            //object.put("id","1");
            JSONArray jsonArray=new JSONArray();
            pager.put("companyId","");
            pager.put("id","");
            pager.put("likePaymentAccount","");
            pager.put("likePaymentNo","");
            pager.put("searchPartnerInfoId",jsonArray);
            pager.put("searchPaymentType",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json=pager.toString();
        String params="currentPage="+pageindex+1+"&"+"pageSize="+"15"+"&"+"sortName="+"id"+"&"+"sortType="+"desc";
        new HttpUtils().postJson(Constant.APPURLS+"partner/partnersku/page?"+params,json,token,new HttpUtils.HttpCallback() {

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
                rechargData.setPartnerInfoId(object.getString("partnerInfoId"));
                rechargData.setPaymentType(object.getString("paymentType"));
                rechargData.setPaymentAccount(object.getString("paymentAccount"));
                rechargData.setPaymentNo(object.getString("paymentNo"));
                rechargData.setStatuslist(list1);
                rechargData.setList(list);
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
                        case 7:
                            list=msg.obj.toString();
                            break;
                        default:
                            Toast.makeText(PartnerSkuActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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
            intent=new Intent(this,AddPartnerbankActivity.class);
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
