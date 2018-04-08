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
import com.example.zhujia.dx_dms.Activity.AddPartnerInvoiceAcitivity;
import com.example.zhujia.dx_dms.Activity.AddProduct;
import com.example.zhujia.dx_dms.Activity.CustomerLsitActivity;
import com.example.zhujia.dx_dms.Activity.PartnerInvoiceAcitivity;
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

public class PartnerInvoiceAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder> implements View.OnClickListener  {

    private List<Data> datas;
    public PartnerInvoiceAcitivity context;
    private int type=0;
    private SharedPreferences sharedPreferences;
    private String business_id,userId,departmentPersonName,departmentPersonSession,token;
    private Handler mHandler;
    private List<AllData> dicts1 = new ArrayList<AllData>();
    private List<AllData> dicts2= new ArrayList<AllData>();
    private PartnerInvoiceAdapter.OnitemClickListener onitemClickListener=null;
    @SuppressLint("WrongConstant")
    public PartnerInvoiceAdapter(PartnerInvoiceAcitivity context1, List<Data>data){
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

    public void setOnitemClickListener(PartnerInvoiceAdapter.OnitemClickListener onitemClickListener) {
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

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.partnerinvoice_data_xml, parent, false);
            PartnerInvoiceAdapter.LinearViewHolder linearViewHolder = new PartnerInvoiceAdapter.LinearViewHolder(baseView);
            baseView.setOnClickListener(this);
            return linearViewHolder;

        }
        else {

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.partnerinvoice_data_xml, parent, false);
            PartnerInvoiceAdapter.GridViewHolder gridViewHolder = new PartnerInvoiceAdapter.GridViewHolder(baseView);
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
            final PartnerInvoiceAdapter.LinearViewHolder linearViewHolder= (PartnerInvoiceAdapter.LinearViewHolder) holder;
            linearViewHolder.invoiceAddr.setText(datas.get(position).getInvoiceAddr());
            linearViewHolder.invoiceBankName.setText(datas.get(position).getInvoiceBankName());
            linearViewHolder.invoiceBankNo.setText(datas.get(position).getInvoiceBankNo());
            linearViewHolder.invoiceCode.setText(datas.get(position).getInvoiceCode());
            linearViewHolder.invoicePhone.setText(datas.get(position).getInvoicePhone());
            linearViewHolder.invoiceTitle.setText(datas.get(position).getInvoiceTitle());

            try {
                if(datas.get(position).getStatuslist()!=null){
                    JSONArray statuslist = null;
                    statuslist = new JSONArray(datas.get(position).getStatuslist());
                    for(int i=0;i<statuslist.length();i++){
                        JSONObject object1=statuslist.getJSONObject(i);
                        dicts2.add(new AllData(object1.getString("key"),object1.getString("value")));
                    }
                    for(int j=0;j<dicts2.size();j++){
                        if(datas.get(position).getInvoiceType().equals(dicts2.get(j).getStr())){
                            linearViewHolder.invoiceType.setText(dicts2.get(j).getText());
                        }
                    }
                }

                if(datas.get(position).getList()!=null){
                    JSONArray statuslist = null;
                    statuslist = new JSONArray(datas.get(position).getList());
                    Log.e("TAG", "onBindViewHolder: "+ datas.get(position).getList());
                    for(int i=0;i<statuslist.length();i++){
                        JSONObject object1=statuslist.getJSONObject(i);
                        dicts1.add(new AllData(object1.getString("id"),object1.getString("partnerName")));
                    }
                    for(int j=0;j<dicts1.size();j++){
                        if(datas.get(position).getPartnerInfoId().equals(dicts1.get(j).getStr())){
                            linearViewHolder.partnerInfoId.setText(dicts1.get(j).getText());
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //编辑
            linearViewHolder.edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//新增信息
                    Intent intent=new Intent(context,AddPartnerInvoiceAcitivity.class);
                    intent.putExtra("id",datas.get(position).getId());
                    intent.putExtra("invoiceType",datas.get(position).getInvoiceType());
                    context.startActivityForResult(intent,REQUEST_CODE);
                }
            });


        }
    }






    public static class LinearViewHolder extends BaseRecyclerViewHolder {

        private TextView partnerInfoId,invoiceAddr,invoiceBankName,invoiceBankNo,invoiceCode,invoicePhone,invoiceTitle,invoiceType;
        private ImageView mainImgUrl;
        private Button edit_btn,btn1,btn2;
        private LinearLayout lin;
        public LinearViewHolder(View itemView) {
            super(itemView);
            partnerInfoId=(TextView)itemView.findViewById(R.id.partnerInfoId);
            invoiceAddr=(TextView)itemView.findViewById(R.id.invoiceAddr);
            invoiceBankName=(TextView)itemView.findViewById(R.id.invoiceBankName);
            invoiceBankNo=(TextView)itemView.findViewById(R.id.invoiceBankNo);
            invoiceCode=(TextView)itemView.findViewById(R.id.invoiceCode);
            invoicePhone=(TextView)itemView.findViewById(R.id.invoicePhone);
            invoiceTitle=(TextView)itemView.findViewById(R.id.invoiceTitle);
            invoiceType=(TextView)itemView.findViewById(R.id.invoiceType);
            edit_btn=(Button)itemView.findViewById(R.id.edit_btn);


        }
    }

    public static class GridViewHolder extends BaseRecyclerViewHolder {


        public GridViewHolder(View itemView) {
            super(itemView);

        }
    }

}