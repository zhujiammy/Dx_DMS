package com.example.zhujia.dx_dms.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.zhujia.dx_dms.Activity.AddGroupinformation_Activity;
import com.example.zhujia.dx_dms.Activity.GroupinFormation_Activity;
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

import static com.example.zhujia.dx_dms.Activity.GroupinFormation_Activity.REQUEST_CODE;

/**
 * Created by ZHUJIA on 2018/3/14.
 */

public class GroupinFormation_Adapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder> implements View.OnClickListener  {

    private List<Data> datas;
    public GroupinFormation_Activity context;
    private int type=0;
    private SharedPreferences sharedPreferences;
    private String token;
    Map<String,String> params;
    private Handler mHandler;
    private List<AllData> dicts1 = new ArrayList<AllData>();
    private GroupinFormation_Adapter.OnitemClickListener onitemClickListener=null;
    @SuppressLint("WrongConstant")
    public GroupinFormation_Adapter(GroupinFormation_Activity context1, List<Data>data){
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

    public void setOnitemClickListener(GroupinFormation_Adapter.OnitemClickListener onitemClickListener) {
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

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.groupinformation_data, parent, false);
            GroupinFormation_Adapter.LinearViewHolder linearViewHolder = new GroupinFormation_Adapter.LinearViewHolder(baseView);
            baseView.setOnClickListener(this);
            return linearViewHolder;

        }
        else {

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.groupinformation_data, parent, false);
            GroupinFormation_Adapter.GridViewHolder gridViewHolder = new GroupinFormation_Adapter.GridViewHolder(baseView);
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
            final GroupinFormation_Adapter.LinearViewHolder linearViewHolder= (GroupinFormation_Adapter.LinearViewHolder) holder;
            linearViewHolder.companyCount.setText(datas.get(position).getCompanyCount());
            //linearViewHolder.createTime.setText(insertComma.stampToDate(data.get(position).getCreateTime()));
            //linearViewHolder.expireTime.setText(insertComma.stampToDate(data.get(position).getExpireTime()));
            linearViewHolder.createTime.setText(datas.get(position).getCreateTime());
            linearViewHolder.expireTime.setText(datas.get(position).getExpireTime());
            linearViewHolder.groupCode.setText(datas.get(position).getGroupCode());
            linearViewHolder.groupName.setText(datas.get(position).getGroupName());
            try {
                if(datas.get(position).getList()!=null){
                    JSONArray companylist = new JSONArray(datas.get(position).getList());
                    for(int i=0;i<companylist.length();i++){
                        JSONObject object1=companylist.getJSONObject(i);
                        dicts1.add(new AllData(object1.getString("key"),object1.getString("value")));

                    }
                    for(int j=0;j<dicts1.size();j++){
                        if(datas.get(position).getUseStatus().equals(dicts1.get(j).getStr())){
                            linearViewHolder.useStatus.setText(dicts1.get(j).getText());
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(datas.get(position).getUseStatus().equals("newly")){
                linearViewHolder.switch_btn.setChecked(false);
                linearViewHolder.del_btn.setVisibility(View.VISIBLE);
                linearViewHolder.switch_btn.setText("停用");
                linearViewHolder.switch_btn.setVisibility(View.VISIBLE);
            }
            if(datas.get(position).getUseStatus().equals("closed")){
                linearViewHolder.switch_btn.setChecked(false);
                linearViewHolder.del_btn.setVisibility(View.GONE);
                linearViewHolder.switch_btn.setText("停用");
                linearViewHolder.switch_btn.setVisibility(View.VISIBLE);
            }
            if(datas.get(position).getUseStatus().equals("normal")){
                linearViewHolder.switch_btn.setChecked(true);
                linearViewHolder.switch_btn.setText("启用");
                linearViewHolder.del_btn.setVisibility(View.GONE);
                linearViewHolder.switch_btn.setVisibility(View.VISIBLE);
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

                   Intent intent=new Intent(context,AddGroupinformation_Activity.class);
                   intent.putExtra("id",datas.get(position).getId());
                   intent.putExtra("groupCode",datas.get(position).getGroupCode());
                    intent.putExtra("groupName",datas.get(position).getGroupName());
                    intent.putExtra("companyCount",datas.get(position).getCompanyCount());
                    intent.putExtra("expireTime",datas.get(position).getExpireTime());
                    context.startActivityForResult(intent,REQUEST_CODE);
                }
            });


            //权限分配
            linearViewHolder.Permission_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.shouw(datas.get(position).getId());

                }
            });

            //是否启用
            linearViewHolder.switch_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        changestatue(position,datas.get(position).getId());
                            linearViewHolder.switch_btn.setText("启用");
                    }else {
                        linearViewHolder.switch_btn.setText("停用");
                        changestatue(position,datas.get(position).getId());
                    }
                }
            });

        }
    }


    private void changestatue(final int position, final String id){
        context.changeuserstatue(position,id);
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
                        new HttpUtils().Post(Constant.APPURLS+"/group/groupbase/delete"+"/"+id,token,new HttpUtils.HttpCallback() {

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

        private TextView companyCount,createTime,expireTime,groupCode,groupName,useStatus;
        private Button edit_btn,del_btn,Permission_btn,ok_btn;
        private Switch switch_btn;
        private LinearLayout lin;
        public LinearViewHolder(View itemView) {
            super(itemView);
            companyCount=(TextView)itemView.findViewById(R.id.companyCount);
            createTime=(TextView)itemView.findViewById(R.id.createTime);
            expireTime=(TextView)itemView.findViewById(R.id.expireTime);
            groupCode=(TextView)itemView.findViewById(R.id.groupCode);
            groupName=(TextView)itemView.findViewById(R.id.groupName);
            useStatus=(TextView)itemView.findViewById(R.id.useStatus);
            edit_btn=(Button)itemView.findViewById(R.id.edit_btn);
            lin=(LinearLayout)itemView.findViewById(R.id.lin_btn);
            del_btn=(Button)itemView.findViewById(R.id.del_btn);
            Permission_btn=(Button)itemView.findViewById(R.id.Permission_btn);
            ok_btn=(Button)itemView.findViewById(R.id.ok_btn);
            switch_btn=(Switch)itemView.findViewById(R.id.switch_btn);
        }
    }

    public static class GridViewHolder extends BaseRecyclerViewHolder {


        public GridViewHolder(View itemView) {
            super(itemView);

        }
    }

}