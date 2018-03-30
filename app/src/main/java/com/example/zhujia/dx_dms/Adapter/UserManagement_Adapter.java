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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zhujia.dx_dms.Activity.AddUserActivity;
import com.example.zhujia.dx_dms.Activity.UserManagementAcitvity;
import com.example.zhujia.dx_dms.Data.Data;
import com.example.zhujia.dx_dms.R;
import com.example.zhujia.dx_dms.Tools.BaseRecyclerAdapter;
import com.example.zhujia.dx_dms.Tools.Net.Constant;
import com.example.zhujia.dx_dms.Tools.Net.HttpUtils;

import org.json.JSONException;

import java.util.List;
import java.util.Map;

import static com.example.zhujia.dx_dms.Activity.GroupinFormation_Activity.REQUEST_CODE;

/**
 * Created by ZHUJIA on 2018/3/14.
 */

public class UserManagement_Adapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder> implements View.OnClickListener  {

    private List<Data> datas;
    public UserManagementAcitvity context;
    private int type=0;
    private SharedPreferences sharedPreferences;
    private String token;
    Map<String,String> params;
    private Handler mHandler;
    private UserManagement_Adapter.OnitemClickListener onitemClickListener=null;
    @SuppressLint("WrongConstant")
    public UserManagement_Adapter(UserManagementAcitvity context1, List<Data>data){
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

    public void setOnitemClickListener(UserManagement_Adapter.OnitemClickListener onitemClickListener) {
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

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.usermanagement_data_xml, parent, false);
            UserManagement_Adapter.LinearViewHolder linearViewHolder = new UserManagement_Adapter.LinearViewHolder(baseView);
            baseView.setOnClickListener(this);
            return linearViewHolder;

        }
        else {

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.usermanagement_data_xml, parent, false);
            UserManagement_Adapter.GridViewHolder gridViewHolder = new UserManagement_Adapter.GridViewHolder(baseView);
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
            final UserManagement_Adapter.LinearViewHolder linearViewHolder= (UserManagement_Adapter.LinearViewHolder) holder;
            linearViewHolder.userName.setText(datas.get(position).getUserName());
            linearViewHolder.userTypeEnum.setText(datas.get(position).getUserType());
            linearViewHolder.realName.setText(datas.get(position).getRealName());
            linearViewHolder.mobilePhone.setText(datas.get(position).getMobilePhone());
            linearViewHolder.roleName.setText(datas.get(position).getRoleName());
            linearViewHolder.companyName.setText(datas.get(position).getCompanyName());

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
                    Intent intent=new Intent(context,AddUserActivity.class);
                    intent.putExtra("id",datas.get(position).getId());
                    intent.putExtra("type","2");
                    context.startActivityForResult(intent,REQUEST_CODE);
                }
            });

            //解锁
            linearViewHolder.unlock_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.Unlock(datas.get(position).getId());
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
                        new HttpUtils().Post(Constant.APPURLS+"/system/systemuser/delete"+"/"+id,token,new HttpUtils.HttpCallback() {

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

        private TextView userName,userTypeEnum,realName,mobilePhone,companyName,roleName;
        private Button edit_btn,del_btn,unlock_btn;
        private LinearLayout lin;
        public LinearViewHolder(View itemView) {
            super(itemView);
            userName=(TextView)itemView.findViewById(R.id.userName);
            userTypeEnum=(TextView)itemView.findViewById(R.id.userTypeEnum);
            companyName=(TextView)itemView.findViewById(R.id.companyName);
            roleName=(TextView)itemView.findViewById(R.id.roleName);
            realName=(TextView)itemView.findViewById(R.id.realName);
            mobilePhone=(TextView)itemView.findViewById(R.id.mobilePhone);
            edit_btn=(Button)itemView.findViewById(R.id.edit_btn);
            lin=(LinearLayout)itemView.findViewById(R.id.lin_btn);
            del_btn=(Button)itemView.findViewById(R.id.del_btn);
            unlock_btn=(Button)itemView.findViewById(R.id.unlock_btn);
        }
    }

    public static class GridViewHolder extends BaseRecyclerViewHolder {


        public GridViewHolder(View itemView) {
            super(itemView);

        }
    }

}