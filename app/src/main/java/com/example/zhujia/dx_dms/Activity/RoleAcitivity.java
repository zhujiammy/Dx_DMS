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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhujia.dx_dms.Adapter.Role_Adapter;
import com.example.zhujia.dx_dms.Adapter.SimpleTreeAdapter;
import com.example.zhujia.dx_dms.Data.BaseActivity;
import com.example.zhujia.dx_dms.Data.Data;
import com.example.zhujia.dx_dms.R;
import com.example.zhujia.dx_dms.Tools.Net.Constant;
import com.example.zhujia.dx_dms.Tools.Net.HttpUtils;
import com.example.zhujia.dx_dms.Tools.OnLoadMoreListener;
import com.example.zhujia.dx_dms.Tools.OnRefreshListener;
import com.example.zhujia.dx_dms.Tools.SuperRefreshRecyclerView;
import com.hmy.popwindow.PopWindow;
import com.multilevel.treelist.Node;
import com.multilevel.treelist.TreeListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ZHUJIA on 2018/3/14.
 * 角色列表
 */

public class RoleAcitivity extends BaseActivity implements View.OnClickListener,OnRefreshListener,OnLoadMoreListener {


    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private String business_id,departmentId;
    private String params;
    private int pageindex=1;
    JSONObject object;
    JSONObject pager;
    private int istouch=0;
    private ViewGroup.LayoutParams lp;
    boolean hasMoreData;
    private Handler mHandler;
    private List<Data> mListData=new ArrayList<>();
    JSONObject reslutJSONObject;
    JSONArray contentjsonarry;
    JSONObject contentjsonobject;
    public static final int  REQUEST_CODE=1001;
    private Role_Adapter adapter;
    private  int total;
    private PopWindow popWindow;
    private Button ok_btn;
    private   View customView;
    private Intent intent;
    private SuperRefreshRecyclerView recyclerView;
    private TextView text;
    private String type,token,list;
    private String roleId ;
    private TreeListViewAdapter mAdapter;
    ListView mTree;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.role_xml);
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
        adapter=new Role_Adapter(this,getData());

    }


    private void initUI(){

        //初始化
        recyclerView= (SuperRefreshRecyclerView)findViewById(R.id.recyclerview);
        recyclerView.init(this,this);
        recyclerView.setRefreshEnabled(true);
        recyclerView.setLoadingMoreEnable(true);
        recyclerView.setHasFixedSize(true);
        text=(TextView) findViewById(R.id.text1);

        text.setText("角色列表");

        customView = View.inflate(RoleAcitivity.this,R.layout.permissions_pop_xml, null);
        ok_btn=(Button)customView.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(this);
        popWindow = new PopWindow.Builder(RoleAcitivity.this)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .setView(customView)
                .create();
        mTree = (ListView) customView.findViewById(R.id.lv_tree);
        //第一个参数  RecyclerView
        //第二个参数  上下文
        //第三个参数  数据集
        //第四个参数  默认展开层级数 0为不展开
        //第五个参数  展开的图标
        //第六个参数  闭合的图标

        mAdapter = new SimpleTreeAdapter(mTree, RoleAcitivity.this,
                mDatas, 1,R.mipmap.tree_ex,R.mipmap.tree_ec);

        mTree.setAdapter(mAdapter);
        Log.e("TAG", "mDatas: "+mDatas);



    }

    private void loaddata(){
        loadbaseinfo();
        object = new JSONObject();
        pager=new JSONObject();
        try {
            //object.put("id","1");
            JSONArray jsonArray=new JSONArray();
            pager.put("likeRoleName","");
            pager.put("searchGroupId",jsonArray);
            pager.put("searchId",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json=pager.toString();
        String params="currentPage="+pageindex+"&"+"pageSize="+"10"+"&"+"sortName="+"id"+"&"+"sortType="+"desc";
        new HttpUtils().postJson(Constant.APPURLS+"system/systemrole/page?"+params,json,token,new HttpUtils.HttpCallback() {

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
    private void loadbaseinfo(){
        Log.e("TAG", "login: "+object );
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

    //加载更多
    private void initItemMoreData() {
        object = new JSONObject();
        pager=new JSONObject();
        try {
            //object.put("id","1");
            JSONArray jsonArray=new JSONArray();
            pager.put("likeRoleName","");
            pager.put("searchGroupId",jsonArray);
            pager.put("searchId",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json=pager.toString();
        String params="currentPage="+pageindex+1+"&"+"pageSize="+"10"+"&"+"sortName="+"id"+"&"+"sortType="+"desc";
        new HttpUtils().postJson(Constant.APPURLS+"system/systemrole/page?"+params,json,token,new HttpUtils.HttpCallback() {

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
                rechargData.setRoleName(object.getString("roleName"));
                rechargData.setGroupId(object.getString("groupId"));
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
                        case 1:
                            list=msg.obj.toString();
                            break;
                        case 2:
                            //返回item类型数据
                            JSONArray jsonArray=new JSONArray(msg.obj.toString());

                            List<Node> mlist = new ArrayList<>();
                            mlist.clear();
                            mDatas.clear();
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject object=jsonArray.getJSONObject(i);
                                if(object.isNull("pid")){
                                    mlist.add(new Node(object.getInt("id")+"",-1+"",object.getString("name")));
                                }else {
                                    mlist.add(new Node(object.getInt("id")+"",object.getInt("pid")+"",object.getString("name")));
                                }
                                if(object.getString("checked").equals("true")){
                                    mlist.get(i).setChecked(true);
                                }



                            }
                            mAdapter.addDataAll(mlist,0);
                            Log.e("TAG", "mlist: "+mlist.size());
                            break;
                        case 3:
                            adapter.notifyDataSetChanged();
                            recyclerView.setLoadingMore(false);
                            break;
                        case 4:
                            adapter.notifyDataSetChanged();
                            break;
                        case 5:
                            //返回item类型数据
                            JSONObject reslutJSONObject=new JSONObject(msg.obj.toString());
                            if(reslutJSONObject.getString("code").equals("200")){
                                Toast.makeText(RoleAcitivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                                recyclerView.setRefreshing(true);
                                popWindow.dismiss();
                            }

                            break;

                        default:
                            Toast.makeText(RoleAcitivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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

        //更新权限
        if(v==ok_btn){
            StringBuilder sb = new StringBuilder();
            //获取排序过的nodes
            //如果不需要刻意直接用 mDatas既可

            final List<Node> allNodes = mAdapter.getAllNodes();
            for (int i = 0; i < allNodes.size(); i++) {
                if (allNodes.get(i).isChecked()){
                    sb.append(allNodes.get(i).getId()+",");
                }
            }

            String strNodesName = sb.toString();
            if (!TextUtils.isEmpty(strNodesName)){
                update(strNodesName);
            }else {
                update("null");
            }

        }
    }
    //分配权限
    private void update(String resourceId){


        Log.e("TAG", "update: "+resourceId);
        if(resourceId.equals("null")){
            JSONArray array=new JSONArray();
            params=array.toString();

        }else {
            JSONArray jsonArray1=new JSONArray();
            jsonArray1.put(resourceId.substring(0,resourceId.length()-1));
            params=jsonArray1.toString().replace("\"","").replace("\"","");
        }

        Log.e("TAG", "update: "+params);
        new HttpUtils().postJson(Constant.APPURLS+"system/systemrole/save/resource/"+roleId,params,token,new HttpUtils.HttpCallback() {

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
            intent=new Intent(this,AddRoleAcitivity.class);
            intent.putExtra("type","1");
            startActivityForResult(intent,REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }
    public void shouw(String id){
        roleId =id;
        new HttpUtils().Post(Constant.APPURLS+"system/systemrole/edit/resource/"+id+"?type=pid",token,new HttpUtils.HttpCallback() {

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
        popWindow.show(customView);
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
