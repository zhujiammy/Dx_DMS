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
import com.example.zhujia.dx_dms.Activity.AddProduct;
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

public class ProductListAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder> implements View.OnClickListener  {

    private List<Data> datas;
    public ProductListActivity context;
    private int type=0;
    private SharedPreferences sharedPreferences;
    private String business_id,userId,departmentPersonName,departmentPersonSession,token;
    private Handler mHandler;
    private List<AllData> dicts1 = new ArrayList<AllData>();
    private List<AllData> dicts2= new ArrayList<AllData>();
    private ProductListAdapter.OnitemClickListener onitemClickListener=null;
    @SuppressLint("WrongConstant")
    public ProductListAdapter(ProductListActivity context1, List<Data>data){
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

    public void setOnitemClickListener(ProductListAdapter.OnitemClickListener onitemClickListener) {
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

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.productlist_data_xml, parent, false);
            ProductListAdapter.LinearViewHolder linearViewHolder = new ProductListAdapter.LinearViewHolder(baseView);
            baseView.setOnClickListener(this);
            return linearViewHolder;

        }
        else {

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.productlist_data_xml, parent, false);
            ProductListAdapter.GridViewHolder gridViewHolder = new ProductListAdapter.GridViewHolder(baseView);
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
            final ProductListAdapter.LinearViewHolder linearViewHolder= (ProductListAdapter.LinearViewHolder) holder;
            linearViewHolder.skuName.setText(datas.get(position).getSkuName());
            linearViewHolder.model.setText(datas.get(position).getModel());
            linearViewHolder.itemNo.setText(datas.get(position).getItemNo());
            linearViewHolder.fcno.setText(datas.get(position).getFcno());
            linearViewHolder.listPrice.setText("¥"+datas.get(position).getListPric());
            linearViewHolder.weight.setText("重量:"+datas.get(position).getWeight()+"KG");
            linearViewHolder.Specifications.setText("规格:"+datas.get(position).getPackageLength()+"*"+datas.get(position).getPackageWidth()+"*"+datas.get(position).getPackageHeight()+"(CM)");
            linearViewHolder.createTime.setText(datas.get(position).getCreateTime());
            linearViewHolder.typeName.setText(datas.get(position).getTypeName());
            linearViewHolder.seriesName.setText(datas.get(position).getSeriesName());

            if(!datas.get(position).getMainImgUrl().equals("")){
                Glide.with(context).load(Constant.loadimag+datas.get(position).getMainImgUrl()).into(linearViewHolder.mainImgUrl);
                Log.e("TAG", "onBindViewHolder: "+ Constant.loadimag+datas.get(position).getMainImgUrl());
            }else {
                linearViewHolder.mainImgUrl.setImageDrawable(context.getResources().getDrawable(R.mipmap.def));
            }
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

            if(datas.get(position).getStatus().equals("created")||datas.get(position).getStatus().equals("unpublish")){
                linearViewHolder.btn2.setText("上架");
            }else if(datas.get(position).getStatus().equals("published")){
                linearViewHolder.btn2.setText("下架");
            }
            if(datas.get(position).getStatus().equals("created")){
                linearViewHolder.del_btn.setVisibility(View.VISIBLE);
            }else {
                linearViewHolder.del_btn.setVisibility(View.GONE);
            }

            //停用
            linearViewHolder.btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.showNormalDialogproduct(position,datas.get(position).getId(),"确认停用","product/productsku/close/");
                }
            });

            //上下架
            linearViewHolder.btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(datas.get(position).getStatus().equals("created")||datas.get(position).getStatus().equals("unpublish")||datas.get(position).getStatus().equals("closed")){
                        linearViewHolder.btn2.setText("上架");
                        context.showNormalDialogproduct(position,datas.get(position).getId(),"确认上架","product/productsku/publish/");
                    }else if(datas.get(position).getStatus().equals("published")){
                        context.showNormalDialogproduct(position,datas.get(position).getId(),"确认下架","product/productsku/unpublish/");
                    }
                }
            });

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
                    Intent intent=new Intent(context,AddProduct.class);
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
                        new HttpUtils().Post(Constant.APPURLS+"/product/productsku/delete"+"/"+id,token,new HttpUtils.HttpCallback() {

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

        private TextView skuName,model,itemNo,fcno,listPrice,status,weight,Specifications,createTime,typeName,seriesName;
        private ImageView mainImgUrl;
        private Button del_btn,edit_btn,btn1,btn2;
        private LinearLayout lin;
        public LinearViewHolder(View itemView) {
            super(itemView);
            skuName=(TextView)itemView.findViewById(R.id.skuName);
            model=(TextView)itemView.findViewById(R.id.model);
            itemNo=(TextView)itemView.findViewById(R.id.itemNo);
            fcno=(TextView)itemView.findViewById(R.id.fcno);
            listPrice=(TextView)itemView.findViewById(R.id.listPrice);
            status=(TextView)itemView.findViewById(R.id.status);
            weight=(TextView)itemView.findViewById(R.id.weight);
            Specifications=(TextView)itemView.findViewById(R.id.Specifications);
            mainImgUrl=(ImageView) itemView.findViewById(R.id.mainImgUrl);
            createTime=(TextView)itemView.findViewById(R.id.createTime);
            del_btn=(Button)itemView.findViewById(R.id.del_btn);
            edit_btn=(Button)itemView.findViewById(R.id.edit_btn);
            btn1=(Button)itemView.findViewById(R.id.btn1);
            btn2=(Button)itemView.findViewById(R.id.btn2);
            seriesName=(TextView)itemView.findViewById(R.id.seriesName);
            typeName=(TextView)itemView.findViewById(R.id.typeName);

        }
    }

    public static class GridViewHolder extends BaseRecyclerViewHolder {


        public GridViewHolder(View itemView) {
            super(itemView);

        }
    }

}