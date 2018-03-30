package com.example.zhujia.dx_dms.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhujia.dx_dms.Activity.GroupManagementActivity;
import com.example.zhujia.dx_dms.Activity.SettingActivity;
import com.example.zhujia.dx_dms.Activity.SystemManagementActivity;
import com.example.zhujia.dx_dms.Activity.productmanagementActivity;
import com.example.zhujia.dx_dms.R;
import com.example.zhujia.dx_dms.Tools.FixedGridLayout;
import com.example.zhujia.dx_dms.Tools.MaskableImageView;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DXSW5 on 2018/3/14.
 */

public class HomePage extends Fragment implements View.OnClickListener {
    private MaskableImageView groupinformation_btn,systemmanagement_btn,productmanagement_btn;
    private TextView balance;
    private String userName,comp,business_id,password;
    private CircleImageView profile_image;
    private SharedPreferences sharedPreferences,sharedPreferences1;
    Map<String,String> params;
    private int pageindex=1;
    private View view;
    private FixedGridLayout grid;
    android.app.AlertDialog.Builder builder;
    ProgressDialog progressDialog;

   // private List<String> list=new ArrayList<>();
   // private List<String> Mlist=new ArrayList<>();
    //private LinearLayout pdsc,plck,xhck,shck,cgkl,gnch,gwch,rsgl,wpgl,zlck,sbgl,wdgz;
    private ViewGroup.LayoutParams lp;


    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.home_page_xml,container,false);
        sharedPreferences =getActivity().getSharedPreferences("Session",
                Context.MODE_APPEND);
        userName=sharedPreferences.getString("userName","");
        initUI();
        return view;
    }




    private void initUI(){
        groupinformation_btn=(MaskableImageView)view.findViewById(R.id.groupinformation_btn);
        groupinformation_btn.setOnClickListener(grouplistener);
        profile_image=(CircleImageView)view.findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);
        systemmanagement_btn=(MaskableImageView)view.findViewById(R.id.systemmanagement_btn);
        systemmanagement_btn.setOnClickListener(syslistener);
        productmanagement_btn=(MaskableImageView)view.findViewById(R.id.productmanagement_btn);
        productmanagement_btn.setOnClickListener(produlistener);
        balance=(TextView)view.findViewById(R.id.balance);
        balance.setText(userName);
    }


    //集团管理
    MaskableImageView.OnClickListener grouplistener=new MaskableImageView.OnClickListener() {
        @Override
        public void onClick() {
            Intent intent=new Intent(getContext(), GroupManagementActivity.class);
            startActivity(intent);
        }
    };

    //系统管理
    MaskableImageView.OnClickListener syslistener=new MaskableImageView.OnClickListener() {
        @Override
        public void onClick() {
            Intent intent=new Intent(getContext(), SystemManagementActivity.class);
            startActivity(intent);
        }
    };

    //产品管理
    MaskableImageView.OnClickListener produlistener=new MaskableImageView.OnClickListener() {
        @Override
        public void onClick() {
            Intent intent=new Intent(getContext(), productmanagementActivity.class);
            startActivity(intent);
        }
    };




    @Override
    public void onClick(View v) {
        if(v==profile_image){
           Intent intent=new Intent(getActivity(),SettingActivity.class);
           startActivity(intent);
        }
    }









}
