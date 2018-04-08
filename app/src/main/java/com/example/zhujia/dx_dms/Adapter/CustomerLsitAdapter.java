package com.example.zhujia.dx_dms.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zhujia.dx_dms.Activity.AddCustomerLsitActivity;
import com.example.zhujia.dx_dms.Activity.AddProduct;
import com.example.zhujia.dx_dms.Activity.CustomerLsitActivity;
import com.example.zhujia.dx_dms.Activity.ProductListActivity;
import com.example.zhujia.dx_dms.Data.AllData;
import com.example.zhujia.dx_dms.Data.Data;
import com.example.zhujia.dx_dms.R;
import com.example.zhujia.dx_dms.Tools.BaseRecyclerAdapter;
import com.example.zhujia.dx_dms.Tools.Net.Constant;
import com.example.zhujia.dx_dms.Tools.Net.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.zhujia.dx_dms.Activity.ProductListActivity.REQUEST_CODE;

/**
 * Created by ZHUJIA on 2018/3/14.
 */

public class CustomerLsitAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder> implements View.OnClickListener  {

    private List<Data> datas;
    public CustomerLsitActivity context;
    private int type=0;
    private SharedPreferences sharedPreferences;
    private String business_id,userId,departmentPersonName,departmentPersonSession,token;
    private Handler mHandler;
    private List<AllData> dicts1 = new ArrayList<AllData>();
    private List<AllData> dicts2= new ArrayList<AllData>();
    private CustomerLsitAdapter.OnitemClickListener onitemClickListener=null;
    @SuppressLint("WrongConstant")
    public CustomerLsitAdapter(CustomerLsitActivity context1, List<Data>data){
        this.context=context1;
        this.datas=data;
        sharedPreferences =context1.getSharedPreferences("Session", Context.MODE_APPEND);
        token=sharedPreferences.getString("token","");
    }
    @Override
    public void onClick(View view) {
        if(onitemClickListener!=null){
            onitemClickListener.onItemClick(view,(int)view.getTag());
        }
    }

    public void setOnitemClickListener(CustomerLsitAdapter.OnitemClickListener onitemClickListener) {
        this.onitemClickListener = onitemClickListener;
    }

    public static interface OnitemClickListener{
        void onItemClick(View view, int position);
    }


    //点击切换布局的时候通过这个方法设置type
    public void setType(int type) {
        this.type = type;
    }

    @Override
    //用来获取当前项Item是哪种类型的布局
    public int getItemViewType(int position) {
        return type;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View baseView;
        if (viewType == 0) {

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customerlsit_data_xml, parent, false);
            CustomerLsitAdapter.LinearViewHolder linearViewHolder = new CustomerLsitAdapter.LinearViewHolder(baseView);
            baseView.setOnClickListener(this);
            return linearViewHolder;

        }
        else {

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customerlsit_data_xml, parent, false);
            CustomerLsitAdapter.GridViewHolder gridViewHolder = new CustomerLsitAdapter.GridViewHolder(baseView);
            baseView.setOnClickListener(this);
            return gridViewHolder;
        }

    }



    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position, List<Map<String, Object>> data) {

    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, final int position) {
        if (type==0){
            final CustomerLsitAdapter.LinearViewHolder linearViewHolder= (CustomerLsitAdapter.LinearViewHolder) holder;
            linearViewHolder.partnerName.setText(datas.get(position).getPartnerName());
            linearViewHolder.address.setText(datas.get(position).getProvince()+" "+datas.get(position).getCity()+" "+datas.get(position).getDistrict()+" "+datas.get(position).getAddr());
            linearViewHolder.shopName.setText(datas.get(position).getShopName());
            linearViewHolder.shopAddr.setText(datas.get(position).getShopAddr());
            linearViewHolder.email.setText(datas.get(position).getEmail());
            linearViewHolder.mobile.setText(datas.get(position).getMobile());
            linearViewHolder.phone.setText(datas.get(position).getPhone());
            linearViewHolder.qq.setText(datas.get(position).getQq());

            try {
                if(datas.get(position).getStatuslist()!=null){
                    JSONArray statuslist = null;
                    statuslist = new JSONArray(datas.get(position).getStatuslist());
                    for(int i=0;i<statuslist.length();i++){
                        JSONObject object1=statuslist.getJSONObject(i);
                        dicts2.add(new AllData(object1.getString("key"),object1.getString("value")));
                    }
                    for(int j=0;j<dicts2.size();j++){
                        if(datas.get(position).getStatus().equals(dicts2.get(j).getStr())){
                            linearViewHolder.status.setText(dicts2.get(j).getText());
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(datas.get(position).getStatus().equals("created")){
                linearViewHolder.del_btn.setVisibility(View.VISIBLE);
            }else {
                linearViewHolder.del_btn.setVisibility(View.GONE);
            }


            //删除
            linearViewHolder.del_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showNormalDialog(position,datas.get(position).getId());
                }
            });

            //编辑
            linearViewHolder.edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//新增信息
                    Intent intent=new Intent(context,AddCustomerLsitActivity.class);
                    intent.putExtra("id",datas.get(position).getId());
                    intent.putExtra("type","2");
                    context.startActivityForResult(intent,REQUEST_CODE);
                }
            });


        }
    }




    private void showNormalDialog(final int position, final String id){

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setMessage("确认删除吗?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        new HttpUtils().Post(Constant.APPURLS+"partner/partnerinfo/delete"+"/"+id,token,new HttpUtils.HttpCallback() {

                            @Override
                            public void onSuccess(String data) {
                                // TODO Auto-generated method stub
                                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                                try {
                                    org.json.JSONObject reslutJSONObject=new org.json.JSONObject(data);
                                    if(reslutJSONObject.getString("code").equals("200")){
                                        datas.remove(position);
                                        notifyDataSetChanged();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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


    public static class LinearViewHolder extends BaseRecyclerViewHolder {

        private TextView partnerName,address,shopName,shopAddr,email,status,mobile,phone,qq;
        private ImageView mainImgUrl;
        private Button del_btn,edit_btn,btn1,btn2;
        private LinearLayout lin;
        public LinearViewHolder(View itemView) {
            super(itemView);
            partnerName=(TextView)itemView.findViewById(R.id.partnerName);
            address=(TextView)itemView.findViewById(R.id.address);
            shopName=(TextView)itemView.findViewById(R.id.shopName);
            shopAddr=(TextView)itemView.findViewById(R.id.shopAddr);
            email=(TextView)itemView.findViewById(R.id.email);
            status=(TextView)itemView.findViewById(R.id.status);
            mobile=(TextView)itemView.findViewById(R.id.mobile);
            phone=(TextView)itemView.findViewById(R.id.phone);
            qq=(TextView)itemView.findViewById(R.id.qq);
            del_btn=(Button)itemView.findViewById(R.id.del_btn);
            edit_btn=(Button)itemView.findViewById(R.id.edit_btn);


        }
    }

    public static class GridViewHolder extends BaseRecyclerViewHolder {


        public GridViewHolder(View itemView) {
            super(itemView);

        }
    }

}