package com.example.zhujia.dx_dms;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.zhujia.dx_dms.Fragment.HomePage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.item.NormalItemView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

public class MainActivity extends AppCompatActivity {

    private FragmentManager manager;
    private HomePage homePage;
    // private OrderList orderMG;

    private Toolbar toolbar;
    private String Id;
    private String user,comp,roleId;
    private SharedPreferences sharedPreferences,sharedPreferences1;
    Map<String,String> params;
    private List<String> list=new ArrayList<>();
    private NavigationController navigationController;
    private TextView tvToolTitle;
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.zhujia.dxracer_factory.dxracersupplierafe.MESSAGE_RECEIVED_ACTION";
    public static boolean isForeground = false;
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("首页");
        tvToolTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        sharedPreferences1=getSharedPreferences("quanxian",MODE_PRIVATE);
        roleId=sharedPreferences.getString("roleId","");
        user=sharedPreferences.getString("username","");
        comp=sharedPreferences.getString("companyName","");
        registerMessageReceiver();
        initUI();
    }

    private void initUI(){
        tvToolTitle.setText(getResources().getString(R.string.homepage));
        tvToolTitle.setTextColor(Color.WHITE);
        PageBottomTabLayout tab = (PageBottomTabLayout) findViewById(R.id.tab);
        navigationController =tab.custom()
                .addItem(newItem(R.drawable.home,R.drawable.home_selected,getResources().getString(R.string.homepage)))
                //.addItem(newItem(R.drawable.ordericon,R.drawable.ordericon_selected,getResources().getString(R.string.Order)))
                .build();
        navigationController.addTabItemSelectedListener(listener);
      /*  username=(TextView)view.findViewById(R.id.username);
        companyName=(TextView)view.findViewById(R.id.companyName);
        //imageView=(ImageView)view.findViewById(R.id.imageView);
        //imageView.setOnClickListener(this);
        username.setText(user);
        companyName.setText(comp);*/
        homePage=new HomePage();
        //orderMG=new OrderList();

        manager=getSupportFragmentManager();
        //初次登陆，显示首页，隐藏其他
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.main_content,homePage);
        //transaction.add(R.id.main_content,orderMG);
        transaction.show(homePage);

        //transaction.hide(orderMG);
        transaction.commit();
    }
/*    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    OnTabItemSelectedListener listener=new OnTabItemSelectedListener() {
        @Override
        public void onSelected(int index, int old) {
            FragmentTransaction transaction=manager.beginTransaction();
            switch (index){
                //当选中首页id时，显示framelayout加载首页fragment
                case 0:
                    transaction.show(homePage);
                  /*  transaction.hide(announcement);*/
                    //transaction.hide(orderMG);
                    tvToolTitle.setText(getResources().getString(R.string.homepage));
                    transaction.commit();
                    break;

               /* case 1:
                    transaction.hide(homePage);
                    transaction.hide(announcement);
                   // transaction.show(orderMG);
                    tvToolTitle.setText(getResources().getString(R.string.Order));
                    transaction.commit();
                    break;*/
        /*        case 1:
                    transaction.hide(homePage);
                    transaction.show(announcement);
                    //transaction.show(orderMG);
                    tvToolTitle.setText(getResources().getString(R.string.Notice));
                    transaction.commit();
                    break;*/


            }
        }

        @Override
        public void onRepeat(int index) {

        }
    };


    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // DXApp.getInstance().hasNews = true;
        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.clear();
        editor1.commit();

        super.onDestroy();
    }
    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        //init();
    }

  /*  private void init() {
        JPushInterface.init(getApplicationContext());
        JPushInterface.resumePush(getApplicationContext());
        // 设置JPush别名
        new JPushInterface().setAliasAndTags(getApplicationContext(), JPushUtil.getImei(getApplicationContext()), null);
    }*/

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }


    private BaseTabItem newItem(int drawable, int checkedDrawable, String text){
        NormalItemView normalItemView =new NormalItemView(this);
        normalItemView.initialize(drawable,checkedDrawable,text);
        normalItemView.setTextDefaultColor(Color.GRAY);
        normalItemView.setTextCheckedColor(0xFF009688);
        return  normalItemView;

    }
}
