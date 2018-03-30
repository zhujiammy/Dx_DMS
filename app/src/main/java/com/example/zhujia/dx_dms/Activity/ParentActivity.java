package com.example.zhujia.dx_dms.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.zhujia.dx_dms.Adapter.TreeViewAdapter;
import com.example.zhujia.dx_dms.Data.Element;
import com.example.zhujia.dx_dms.R;
import com.example.zhujia.dx_dms.Tools.Net.Constant;
import com.example.zhujia.dx_dms.Tools.Net.HttpUtils;
import com.example.zhujia.dx_dms.Tools.TreeViewItemClickListener;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParentActivity extends AppCompatActivity{
    private String token;
    private SharedPreferences sharedPreferences;
    /** 树中的元素集合 */
    private ArrayList<Element> elements;
    Element e2,e1;
    /** 数据源元素集合 */
    private ArrayList<Element> elementsData;
    ListView treeview;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_xml);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        token=sharedPreferences.getString("token","");

        loadbaseinfo();

        treeview = (ListView) findViewById(R.id.treeview);

    }


    private void loadbaseinfo(){
        new HttpUtils().Post(Constant.APPURLS+"/system/systemresource/query/tree",token,new HttpUtils.HttpCallback() {

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


    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what) {

                    case 1:
                        //返回item类型数据
                        JSONArray reslutJSONObject=new JSONArray(msg.obj.toString());
                        elements = new ArrayList<Element>();
                        elementsData = new ArrayList<Element>();
                        for(int i=0;i<reslutJSONObject.length();i++)
                        {

                            JSONObject object=reslutJSONObject.getJSONObject(i);
                            e1 = new Element(object.getString("label"), Element.TOP_LEVEL, object.getInt("value"), Element.NO_PARENT, true, false);
                            elements.add(e1);
                            JSONArray jsonArray=object.getJSONArray("children");
                            for(int j=0;j>jsonArray.length();j++){
                                JSONObject object1=jsonArray.getJSONObject(j);
                                 e2 = new Element(object1.getString("label"), Element.TOP_LEVEL + 1, object1.getInt("value"), e1.getId(), true, false);
                                elementsData.add(e1);
                                elementsData.add(e2);
                            }


                        }
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        TreeViewAdapter treeViewAdapter = new TreeViewAdapter(
                                elements, elementsData, inflater);
                        TreeViewItemClickListener treeViewItemClickListener = new TreeViewItemClickListener(treeViewAdapter);
                        treeview.setAdapter(treeViewAdapter);
                        treeview.setOnItemClickListener(treeViewItemClickListener);

                        break;





                    default:
                        Toast.makeText(ParentActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    };

/*    private void init() {
        elements = new ArrayList<Element>();
        elementsData = new ArrayList<Element>();

        //添加节点  -- 节点名称，节点level，节点id，父节点id，是否有子节点，是否展开

     *//*   //添加最外层节点
        Element e1 = new Element("山东省", Element.TOP_LEVEL, 0, Element.NO_PARENT, true, false);

        //添加第一层节点
        Element e2 = new Element("青岛市", Element.TOP_LEVEL + 1, 1, e1.getId(), true, false);
        //添加第二层节点
        Element e3 = new Element("市南区", Element.TOP_LEVEL + 2, 2, e2.getId(), true, false);
        //添加第三层节点
        Element e4 = new Element("香港中路", Element.TOP_LEVEL + 3, 3, e3.getId(), false, false);

        //添加第一层节点
        Element e5 = new Element("烟台市", Element.TOP_LEVEL + 1, 4, e1.getId(), true, false);
        //添加第二层节点
        Element e6 = new Element("芝罘区", Element.TOP_LEVEL + 2, 5, e5.getId(), true, false);
        //添加第三层节点
        Element e7 = new Element("凤凰台街道", Element.TOP_LEVEL + 3, 6, e6.getId(), false, false);

        //添加第一层节点
        Element e8 = new Element("威海市", Element.TOP_LEVEL + 1, 7, e1.getId(), false, false);

        //添加最外层节点
        Element e9 = new Element("广东省", Element.TOP_LEVEL, 8, Element.NO_PARENT, true, false);
        //添加第一层节点
        Element e10 = new Element("深圳市", Element.TOP_LEVEL + 1, 9, e9.getId(), true, false);
        //添加第二层节点
        Element e11 = new Element("南山区", Element.TOP_LEVEL + 2, 10, e10.getId(), true, false);
        //添加第三层节点
        Element e12 = new Element("深南大道", Element.TOP_LEVEL + 3, 11, e11.getId(), true, false);
        //添加第四层节点
        Element e13 = new Element("10000号", Element.TOP_LEVEL + 4, 12, e12.getId(), false, false);*//*

        //添加初始树元素
        elements.add(e1);
        elements.add(e9);
        //创建数据源
        elementsData.add(e1);
        elementsData.add(e2);
        elementsData.add(e3);
        elementsData.add(e4);
        elementsData.add(e5);
        elementsData.add(e6);
        elementsData.add(e7);
        elementsData.add(e8);
        elementsData.add(e9);
        elementsData.add(e10);
        elementsData.add(e11);
        elementsData.add(e12);
        elementsData.add(e13);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
