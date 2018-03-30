package com.example.zhujia.dx_dms.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhujia.dx_dms.MainActivity;
import com.example.zhujia.dx_dms.R;
import com.example.zhujia.dx_dms.Tools.CheckExitService;
import com.example.zhujia.dx_dms.Tools.MyApplication;
import com.example.zhujia.dx_dms.Tools.Net.Constant;
import com.example.zhujia.dx_dms.Tools.Net.HttpUtils;
import com.example.zhujia.dx_dms.Tools.PermissionsActivity;
import com.example.zhujia.dx_dms.Tools.PermissionsChecker;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by DXSW5 on 2017/7/6.
 *
 *
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_CODE = 0; // 请求码
    private EditText input_account,input_password;
    private Button _loginButton;
    private TextView _signupLink;
    private CheckBox remember;
   //Map<String,String> params;
    Map<String,String> params1;
    private String companyId;
    JSONObject jsonObj;
    SharedPreferences sharedPreferences;
    String json;
    private String user_name,password,CompanyId;
    ProgressDialog progressDialog;
    private  SharedPreferences.Editor editors;


    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            // Manifest.permission.ACCESS_COARSE_LOCATION,
            //Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            //Manifest.permission.MODIFY_AUDIO_SETTINGS
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivitys_xml);
       // LoadID();
        Intent intent=new Intent(this,CheckExitService.class);
        getApplicationContext().startService(intent);
        initUI();
        mPermissionsChecker = new PermissionsChecker(this);
        updataapk();
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }


    //检查更新
    private void updataapk(){
        Beta.autoInit = true;
        Beta.autoCheckUpgrade = true;
        Beta.largeIconId = R.mipmap.ic_launcher;
        Bugly.init(getApplicationContext(), "9e3fff37ad", false);
    }


    private void initUI(){
        input_account=(EditText)findViewById(R.id.input_account);
        input_password=(EditText)findViewById(R.id.input_password);
        _loginButton=(Button)findViewById(R.id.btn_login);
        remember=(CheckBox)findViewById(R.id.remember);
        //初始化用户名、密码，记住密码
        sharedPreferences=getSharedPreferences("userInfo",0);
        String name=sharedPreferences.getString("USER_NAME","");
        String pass=sharedPreferences.getString("PASSWORD","");
        if(!sharedPreferences.getString("CompanyId","").isEmpty()){
            CompanyId=sharedPreferences.getString("CompanyId","");
        }

        boolean choseRemember=sharedPreferences.getBoolean("remember",false);
        //如果上次记住了密码，那进入登陆页面自动勾选记住密码，并填上用户名密码


        if(choseRemember){
            input_account.setText(name);
            input_password.setText(pass);
            remember.setChecked(true);
        }
 
    }

    //登陆
    public void login() {
        Log.d(TAG, "Login");

        user_name =input_account.getText().toString();
        password =input_password.getText().toString();
        editors =sharedPreferences.edit();
        JSONObject object = new JSONObject();
        try {
            object.put("userName",user_name);
            object.put("password",password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String params=object.toString();
        Log.e(TAG, "login: "+object );
        // TODO: Implement your own authentication logic here.
        //final String params="token=" +token+ "&json=" +json;
        if(TextUtils.isEmpty(user_name)){
            Toast.makeText(getApplicationContext(),"用户名不能为空",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
        }else {
            progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.Loggingin));
            progressDialog.show();
            new HttpUtils().LoginPost(Constant.APPURLS+"/system/user/login",params,new HttpUtils.HttpCallback() {

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



    }

    /**
     * 消息处理Handler
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:// 解析返回数据
                    //toMainActivity();
                    break;
                case 1:

                    try{
                        JSONObject reslutJSONObject=new JSONObject(msg.obj.toString());
                        SharedPreferences sp= getSharedPreferences("Session", Activity.MODE_PRIVATE);
                        String result_code=reslutJSONObject.getString("code");
                        String msgs=reslutJSONObject.getString("message");

                        if(result_code.equals("200")){
                            JSONObject message=reslutJSONObject.getJSONObject("message");
                            JSONObject user=message.getJSONObject("user");
                            JSONArray groupCompanys=user.getJSONArray("groupCompanys");
                            JSONObject object=groupCompanys.getJSONObject(0);
                            companyId=object.getString("id");
                            MyApplication myApplication=(MyApplication)getApplication();
                            myApplication.setCompanyId(companyId);
                            //存储TOKEN信息
                            SharedPreferences.Editor editor=sp.edit();
                            //系统用户
                            editor.putString("token",message.getString("token"));
                            if(!companyId.equals(companyId)){
                                editor.putString("companyId",companyId);
                            }
                            editor.putString("userName",user.getString("userName"));
                            editor.putString("groupCompanys",user.getString("groupCompanys"));
                            editor.commit();
                            //保存密码
                            editors.putString("USER_NAME",user_name);
                            editors.putString("PASSWORD",password);
                            //是否记住密码
                            if(remember.isChecked())
                            {
                                editors.putBoolean("remember",true);
                            }else
                            {
                                editors.putBoolean("remember",false);
                            }

                            editors.commit();
                            //跳转
                            mHandler.postDelayed(runnable,2000);


                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),msgs, Toast.LENGTH_SHORT).show();

                        }

                        Log.d("code","result_code"+result_code);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    break;

                default:
                    Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            progressDialog.dismiss();
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }else {
            updataapk();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED)
        {
            finish();
        }
    }

    /**
     * 获取ip地址
     * @return
     */
    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }
}
