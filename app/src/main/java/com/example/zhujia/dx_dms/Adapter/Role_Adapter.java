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

import com.example.zhujia.dx_dms.Activity.AddRoleAcitivity;
import com.example.zhujia.dx_dms.Activity.RoleAcitivity;
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

public class Role_Adapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder> implements View.OnClickListener  {

    private List<Data> datas;
    public RoleAcitivity context;
    private int type=0;
    private SharedPreferences sharedPreferences;
    private String token;
    private Handler mHandler;
    private List<AllData> dicts1 = new ArrayList<AllData>();
    private Role_Adapter.OnitemClickListener onitemClickListener=null;
    @SuppressLint("WrongConstant")
    public Role_Adapter(RoleAcitivity context1, List<Data>data){
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

    public void setOnitemClickListener(Role_Adapter.OnitemClickListener onitemClickListener) {
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

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.role_data_xml, parent, false);
            Role_Adapter.LinearViewHolder linearViewHolder = new Role_Adapter.LinearViewHolder(baseView);
            baseView.setOnClickListener(this);
            return linearViewHolder;

        }
        else {

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.role_data_xml, parent, false);
            Role_Adapter.GridViewHolder gridViewHolder = new Role_Adapter.GridViewHolder(baseView);
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
            final Role_Adapter.LinearViewHolder linearViewHolder= (Role_Adapter.LinearViewHolder) holder;

            linearViewHolder.roleName.setText(datas.get(position).getRoleName());


                try {
                    if(datas.get(position).getList()!=null){
                        JSONArray companylist = new JSONArray(datas.get(position).getList());
                        for(int i=0;i<companylist.length();i++){
                            JSONObject object1=companylist.getJSONObject(i);
                            dicts1.add(new AllData(object1.getString("id"),object1.getString("groupName")));

                        }
                        for(int j=0;j<dicts1.size();j++){
                            if(datas.get(position).getGroupId().equals(dicts1.get(j).getStr())){
                                linearViewHolder.groupId.setText(dicts1.get(j).getText());
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
                    Intent intent=new Intent(context,AddRoleAcitivity.class);
                    intent.putExtra("id",datas.get(position).getId());
                    intent.putExtra("GroupId",datas.get(position).getGroupId());
                    intent.putExtra("roleName",datas.get(position).getRoleName());
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
                        new HttpUtils().Post(Constant.APPURLS+"/system/systemrole//delete"+"/"+id,token,new HttpUtils.HttpCallback() {

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

        private TextView groupId,roleName;
        private Button edit_btn,del_btn;
        private LinearLayout lin;
        public LinearViewHolder(View itemView) {
            super(itemView);
            groupId=(TextView)itemView.findViewById(R.id.groupId);
            roleName=(TextView)itemView.findViewById(R.id.roleName);
            edit_btn=(Button)itemView.findViewById(R.id.edit_btn);
            lin=(LinearLayout)itemView.findViewById(R.id.lin_btn);
            del_btn=(Button)itemView.findViewById(R.id.del_btn);
        }
    }

    public static class GridViewHolder extends BaseRecyclerViewHolder {


        public GridViewHolder(View itemView) {
            super(itemView);

        }
    }

}