package com.example.zhujia.dx_dms.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhujia.dx_dms.Data.AllData;
import com.example.zhujia.dx_dms.R;
import com.example.zhujia.dx_dms.Tools.ImageService;
import com.example.zhujia.dx_dms.Tools.MyImageView;
import com.example.zhujia.dx_dms.Tools.Net.Constant;
import com.example.zhujia.dx_dms.Tools.Net.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ZHUJIA on 2018/3/15.
 * 新增充值列表
 */

public class AddPartnerRechargeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text1;
    private Toolbar toolbar;
    private EditText paymentAccount,paymentNo,paymentNote,receiveAccount,receiveNo,totalFee;
    private MyImageView filePath;
    private java.util.Calendar cal;
    private Spinner partner,paymentType,receiveType,rechargeType;
    private int year,month,day,hour,minute;
    TimePickerDialog dialog1;
    String time;
    Intent intent;
    String date;
    JSONObject object;
    private SharedPreferences sharedPreferences;
    private String token;
    File outputImage;
    public Uri imageUri;
    public static final int HANDLE_MSG_LOAD_IMAGE = 10;
    public static final int HANDLE_MSG_LOAD_IMAGE2 = 12;
    public static  final int HANDLE_MSG_LOAD_IMAGE1=11;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int CHOOSE_PHOTO = 3;
    private Bitmap photo;
    private Bitmap photo1;
    private File FilePath;
    private  String filename,partnerId,paymentTypeId,receiveTypeId,rechargeTypeId;
    private String mainImgUrl;
    private List<AllData> dicts1 = new ArrayList<AllData>();
    private ArrayAdapter<AllData> arrAdapterpay1;
    private List<AllData> dicts2 = new ArrayList<AllData>();
    private  ArrayAdapter<AllData> arrAdapterpay2;
    private List<AllData> dicts3 = new ArrayList<AllData>();
    private  ArrayAdapter<AllData> arrAdapterpay3;
    private List<AllData> dicts4 = new ArrayList<AllData>();
    private  ArrayAdapter<AllData> arrAdapterpay4;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpartnerrecharge_xml);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent=getIntent();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        token=sharedPreferences.getString("token","");
        initUI();
        loadpartnerinfo();//客户名称
        loadpaymenttype();//付款方式
        loadrechargetype();//付款方式
        getDate();
    }

    @SuppressLint("WrongConstant")
    private void getDate(){
        cal= java.util.Calendar.getInstance();
        year=cal.get(java.util.Calendar.YEAR);
        month=cal.get(java.util.Calendar.MONTH);
        day=cal.get(java.util.Calendar.DAY_OF_MONTH);

    }

    private void loadpartnerinfo(){
        new HttpUtils().postJson(Constant.APPURLS+"partner/partnerinfo/list","{}",token,new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,3,data
                );
                mHandler.sendMessage(msg);
            }

        });
    }

    //付款方式
    private void  loadpaymenttype(){
        new HttpUtils().Post(Constant.APPURLS+"partner/partnerbank/paymentTypeEnums",token,new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,4,data
                );
                mHandler.sendMessage(msg);
            }

        });
    }
    //充值类型
    private void loadrechargetype(){
        new HttpUtils().Post(Constant.APPURLS+"partner/partnerrecharge/getPartnerRechargeTypes",token,new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,5,data
                );
                mHandler.sendMessage(msg);
            }

        });
    }
    private void initUI(){
        text1=(TextView)findViewById(R.id.text1);
        paymentAccount=(EditText)findViewById(R.id.paymentAccount);
        paymentNo=(EditText)findViewById(R.id.paymentNo);
        paymentNote=(EditText)findViewById(R.id.paymentNote);
        receiveAccount=(EditText)findViewById(R.id.receiveAccount);
        receiveNo=(EditText)findViewById(R.id.receiveNo);
        totalFee=(EditText)findViewById(R.id.totalFee);
        filePath=(MyImageView)findViewById(R.id.paymentImg);
        filePath.setOnClickListener(this);

        partner=(Spinner)findViewById(R.id.partner);
        partner.setOnItemSelectedListener(partnerlistener);
        paymentType=(Spinner)findViewById(R.id.paymentType);
        paymentType.setOnItemSelectedListener(paymentTypelistener);
        receiveType=(Spinner)findViewById(R.id.receiveType);
        receiveType.setOnItemSelectedListener(receiveTypelistener);
        rechargeType=(Spinner)findViewById(R.id.rechargeType);
        rechargeType.setOnItemSelectedListener(rechargeTypelistener);

        if(intent.getStringExtra("type").equals("2")){
            text1.setText("修改");
            loadGet(intent.getStringExtra("id"));
        }else if(intent.getStringExtra("type").equals("1")) {
            text1.setText("新增");
        }


    }
    private void loadGet(String id){
        System.out.print(id);
        new HttpUtils().Post(Constant.APPURLS+"/partner/partnerrecharge/get"+"/"+id,token,new HttpUtils.HttpCallback() {

            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,2,data
                );
                mHandler.sendMessage(msg);
            }

        });

    }

    Spinner.OnItemSelectedListener partnerlistener=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            partnerId=((AllData)partner.getSelectedItem()).getStr();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    Spinner.OnItemSelectedListener paymentTypelistener=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            paymentTypeId=((AllData)paymentType.getSelectedItem()).getStr();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    Spinner.OnItemSelectedListener receiveTypelistener=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            receiveTypeId=((AllData)receiveType.getSelectedItem()).getStr();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    Spinner.OnItemSelectedListener rechargeTypelistener=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            rechargeTypeId=((AllData)rechargeType.getSelectedItem()).getStr();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.save_btn){

            object = new JSONObject();
            try
            {
                object.put("partnerId",partnerId);
                object.put("paymentAccount",paymentAccount.getText().toString());
                object.put("paymentNo",paymentNo.getText().toString());
                object.put("paymentNote",paymentNote.getText().toString());
                object.put("paymentType",paymentTypeId);
                object.put("receiveAccount",receiveAccount.getText().toString());
                object.put("receiveNo",receiveNo.getText().toString());
                object.put("receiveType",receiveTypeId);
                object.put("rechargeType",rechargeTypeId);
                object.put("totalFee",totalFee.getText().toString());
                object.put("paymentImg",mainImgUrl);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String params=object.toString();
            //保存
            if(partnerId==null||partnerId.equals("0")){
                Toast.makeText(getApplicationContext(),"客户名称不能为空",Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(paymentAccount.getText().toString())){
                Toast.makeText(getApplicationContext(),"付款账户不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(paymentNo.getText().toString())){
                Toast.makeText(getApplicationContext(),"付款账号不能为空",Toast.LENGTH_SHORT).show();
            }else if(paymentTypeId==null||paymentTypeId.equals("0")){
                Toast.makeText(getApplicationContext(),"付款方式不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(receiveAccount.getText().toString())){
                Toast.makeText(getApplicationContext(),"收款账户不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(receiveNo.getText().toString())){
                Toast.makeText(getApplicationContext(),"收款账号不能为空",Toast.LENGTH_SHORT).show();
            }else if(receiveType==null||receiveType.equals("0")){
                Toast.makeText(getApplicationContext(),"收款方式不能为空",Toast.LENGTH_SHORT).show();
            }else if(rechargeType==null||rechargeType.equals("0")){
                Toast.makeText(getApplicationContext(),"充值类型不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(totalFee.getText().toString())){
                Toast.makeText(getApplicationContext(),"充值金额不能为空",Toast.LENGTH_SHORT).show();
            }else if(mainImgUrl==null){
                Toast.makeText(getApplicationContext(),"付款凭证必须上传",Toast.LENGTH_SHORT).show();
            }else if(intent.getStringExtra("type").equals("2")) {

                //修改
                new HttpUtils().postJson(Constant.APPURLS + "partner/partnerrecharge/update" + "/" + intent.getStringExtra("id"),params,token, new HttpUtils.HttpCallback() {

                    @Override
                    public void onSuccess(String data) {
                        // TODO Auto-generated method stub
                        com.example.zhujia.dx_dms.Tools.Log.printJson("tag", data, "header");

                        Message msg = Message.obtain(
                                mHandler, 0, data
                        );
                        mHandler.sendMessage(msg);
                    }

                });

            }
            else if(intent.getStringExtra("type").equals("1")) {



                Log.e("TAG", "login: "+object );
                //新增

                new HttpUtils().postJson(Constant.APPURLS+"partner/partnerrecharge/add",params,token,new HttpUtils.HttpCallback() {

                    @Override
                    public void onSuccess(String data) {
                        // TODO Auto-generated method stub
                        com.example.zhujia.dx_dms.Tools.Log.printJson("tag",data,"header");

                        Message msg= Message.obtain(
                                mHandler,0,data
                        );
                        mHandler.sendMessage(msg);
                    }

                });
            }

        }



        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what) {

                    case 0:
                        //返回item类型数据
                        JSONObject reslutJSONObject=new JSONObject(msg.obj.toString());
                        if(reslutJSONObject.getString("code").equals("200")){
                            Toast.makeText(AddPartnerRechargeActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            Intent data=new Intent();
                            data.putExtra("freshen","y");
                            setResult(RESULT_OK,data);
                            finish();
                        }

                        break;
                    case 1:
                        JSONObject header=new JSONObject(msg.obj.toString());
                        if(header.getString("code").equals("200")){
                            mainImgUrl=header.getString("message");
                        }
                        break;

                    case 2:
                        JSONObject object=new JSONObject(msg.obj.toString());
                        paymentAccount.setText(object.getString("paymentAccount"));
                        paymentNo.setText(object.getString("paymentNo"));
                        paymentNote.setText(object.getString("paymentNote"));
                        receiveAccount.setText(object.getString("receiveAccount"));
                        receiveNo.setText(object.getString("receiveNo"));
                        totalFee.setText(object.getString("totalFee"));
                        if(!object.getString("paymentImg").equals("")){
                            Glide.with(getApplicationContext()).load(Constant.loadimag+object.getString("paymentImg")).into(filePath);
                        }else {
                            filePath.setImageDrawable(getResources().getDrawable(R.mipmap.def));
                        }
                        break;
                    case 3:
                        JSONArray partnerInfoarry=new JSONArray(msg.obj.toString());
                        dicts2.add(new AllData("0","请选择"));
                        for(int i=0;i<partnerInfoarry.length();i++){
                            JSONObject object1=partnerInfoarry.getJSONObject(i);
                            dicts2.add(new AllData(object1.getString("id"),object1.getString("partnerName")));
                            arrAdapterpay2 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts2);
                            //设置样式
                            arrAdapterpay2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            partner.setAdapter(arrAdapterpay2);
                        }
                        if(intent.getStringExtra("type").equals("2")){
                            int d=arrAdapterpay2.getCount();
                            for(int j=0;j<d;j++){
                                if(intent.getStringExtra("partnerInfoId").equals(arrAdapterpay2.getItem(j).getStr())){
                                    partner.setAdapter(arrAdapterpay2);
                                    partner.setSelection(j,true);
                                }
                            }
                        }

                        break;
                    case 4:
                        JSONArray paymentTypearry=new JSONArray(msg.obj.toString());
                        //付款方式
                        dicts1.add(new AllData("0","请选择"));
                        for(int i=0;i<paymentTypearry.length();i++){
                            JSONObject object1=paymentTypearry.getJSONObject(i);
                            dicts1.add(new AllData(object1.getString("key"),object1.getString("value")));
                            arrAdapterpay1 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts1);
                            //设置样式
                            arrAdapterpay1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            paymentType.setAdapter(arrAdapterpay1);
                        }
                        if(intent.getStringExtra("type").equals("2")){
                            int d=arrAdapterpay1.getCount();
                            for(int j=0;j<d;j++){
                                if(intent.getStringExtra("paytype").equals(arrAdapterpay1.getItem(j).getStr())){
                                    paymentType.setAdapter(arrAdapterpay1);
                                    paymentType.setSelection(j,true);
                                }
                            }
                        }

                        //收款方式
                        dicts3.add(new AllData("0","请选择"));
                        for(int i=0;i<paymentTypearry.length();i++){
                            JSONObject object1=paymentTypearry.getJSONObject(i);
                            dicts3.add(new AllData(object1.getString("key"),object1.getString("value")));
                            arrAdapterpay3 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts3);
                            //设置样式
                            arrAdapterpay3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            receiveType.setAdapter(arrAdapterpay3);
                        }
                        if(intent.getStringExtra("type").equals("2")){
                            int d=arrAdapterpay3.getCount();
                            for(int j=0;j<d;j++){
                                if(intent.getStringExtra("receiveType").equals(arrAdapterpay3.getItem(j).getStr())){
                                    receiveType.setAdapter(arrAdapterpay3);
                                    receiveType.setSelection(j,true);
                                }
                            }
                        }
                        break;
                    case 5:
                        JSONArray partnerrecharge=new JSONArray(msg.obj.toString());
                        //付款方式
                        dicts4.add(new AllData("0","请选择"));
                        for(int i=0;i<partnerrecharge.length();i++){
                            JSONObject object1=partnerrecharge.getJSONObject(i);
                            dicts4.add(new AllData(object1.getString("key"),object1.getString("value")));
                            arrAdapterpay4 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts4);
                            //设置样式
                            arrAdapterpay4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            rechargeType.setAdapter(arrAdapterpay4);
                        }
                        if(intent.getStringExtra("type").equals("2")){
                            int d=arrAdapterpay4.getCount();
                            for(int j=0;j<d;j++){
                                if(intent.getStringExtra("rechargeType").equals(arrAdapterpay4.getItem(j).getStr())){
                                    rechargeType.setAdapter(arrAdapterpay4);
                                    rechargeType.setSelection(j,true);
                                }
                            }
                        }
                        break;

                    case HANDLE_MSG_LOAD_IMAGE:
                        FilePath = ImageService.compressImage1(photo,filename);
                        upload();
                        break;

                    case HANDLE_MSG_LOAD_IMAGE2:
                        filePath.setImageBitmap(photo1);
                        break;
                    default:
                        Toast.makeText(AddPartnerRechargeActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    };


    private void upload(){

        new HttpUtils().postUpload(Constant.APPURLS+"/base/file/upload?path=product/sku",token,FilePath, filename + ".png",new HttpUtils.HttpCallback() {
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



    @Override
    public void onClick(View v) {
        if(v==filePath){
            //上传图片
            //上传图片
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date(System.currentTimeMillis());
            filename = format.format(date);
            showPopwindows();

        }
    }


    //打开相机
    private void showPopwindows(){
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(this, R.layout.camera_pop_menu, null);

        Button btnCamera = (Button) popView.findViewById(R.id.btn_camera_pop_camera);
        Button btnAlbum = (Button) popView.findViewById(R.id.btn_camera_pop_album);
        Button btnCancel = (Button) popView.findViewById(R.id.btn_camera_pop_cancel);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        final PopupWindow popWindow = new PopupWindow(popView, width, height);
        popWindow.setAnimationStyle(R.style.AnimBottom);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                switch (v.getId()) {
                    case R.id.btn_camera_pop_camera:

                        //创建file对象，用于存储拍照后的图片
                        outputImage = new File(Environment.getExternalStorageDirectory(), "output_Image.jpg");
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }

                            outputImage.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int permissionCheck = ContextCompat.checkSelfPermission(AddPartnerRechargeActivity.this,
                                Manifest.permission.CAMERA);
                        //存储权限
                        int permissionCheck_storage = ContextCompat.checkSelfPermission(AddPartnerRechargeActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (permissionCheck_storage == PackageManager.PERMISSION_GRANTED) {
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    imageUri = FileProvider.getUriForFile(getApplicationContext(),"com.example.zhujia.dx_dms.fileProvider",outputImage);
                                }else{
                                    imageUri = Uri.fromFile(outputImage);
                                }
                                intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                                //启动相机程序
                                startActivityForResult(intent, TAKE_PHOTO);
                            } else {
                                // 没有权限，跳到设置界面，调用Android系统“应用程序信息（Application Info）”界面
                                new AlertDialog.Builder(AddPartnerRechargeActivity.this)
                                        .setMessage("app需要读取存储权限")
                                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.parse("package:" + getPackageName()));
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .create()
                                        .show();
                            }

                        } else {

                            // 没有权限，跳到设置界面，调用Android系统“应用程序信息（Application Info）”界面
                            new AlertDialog.Builder(AddPartnerRechargeActivity.this)
                                    .setMessage("app需要开启相机权限")
                                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getPackageName()));
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .create()
                                    .show();

                        }


                        break;

                    case R.id.btn_camera_pop_album:
                        //存储权限
                        int permissionCheck_storage_xc = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (permissionCheck_storage_xc == PackageManager.PERMISSION_GRANTED) {


//                            intent = new Intent("android.intent.action.GET_CONTENT");
//							String Action = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT;
                            String Action = Intent.ACTION_GET_CONTENT;
                            intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Action);


//                             打开相册*/
                            startActivityForResult(intent, CHOOSE_PHOTO);

                        } else {
                            // 没有权限，跳到设置界面，调用Android系统“应用程序信息（Application Info）”界面
                            new AlertDialog.Builder(AddPartnerRechargeActivity.this)
                                    .setMessage("app需要读取存储权限")
                                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getPackageName()));
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .create()
                                    .show();
                        }

                        break;
                    case R.id.btn_camera_pop_cancel:

                        break;

                }
                popWindow.dismiss();
            }
        };

        btnCamera.setOnClickListener(listener);
        btnAlbum.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);

        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case TAKE_PHOTO:
                    if (resultCode == RESULT_OK) {
                        this.startPhotoZoom(imageUri);
                    }
                    break;
                case CHOOSE_PHOTO:
                    if (resultCode == RESULT_OK) {


                        photo=null;
                        photo1=null;
                        photo= ImageService.loadImgFromLocal(AddPartnerRechargeActivity.this,data.getData());
                        photo1=ImageService.loadImgFromLocal(AddPartnerRechargeActivity.this,data.getData());
                        if(photo!=null){
                            Message msg = Message.obtain(
                                    mHandler, HANDLE_MSG_LOAD_IMAGE, photo
                            );
                            mHandler.sendMessage(msg);
                        }
                        if(photo1!=null){
                            Message msg = Message.obtain(
                                    mHandler, HANDLE_MSG_LOAD_IMAGE2, photo1
                            );
                            mHandler.sendMessage(msg);
                        }
                    }
                    break;
                case CROP_PHOTO:
                    if (resultCode == RESULT_OK) {

                        photo1=null;
                        photo=null;
                        photo1=photo;
                        photo= ImageService.loadImgFromLocal(outputImage.getAbsolutePath());
                        photo1= ImageService.loadImgFromLocal(outputImage.getAbsolutePath());
                        if(photo!=null){
                            Message msg = Message.obtain(
                                    mHandler, HANDLE_MSG_LOAD_IMAGE, photo
                            );
                            mHandler.sendMessage(msg);
                        }
                        if(photo1!=null){
                            Message msg = Message.obtain(
                                    mHandler, HANDLE_MSG_LOAD_IMAGE2, photo1
                            );
                            mHandler.sendMessage(msg);
                        }



                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startPhotoZoom(Uri uri) {
        //imageUri=FileProvider.getUriForFile(getApplicationContext(), "com.uroad.cargo.alpha.fileprovider", new File(ImageService.getImageAbsolutePath(getApplicationContext(), uri)));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // aspectX aspectY 是宽高的比例
        //intent.putExtra("aspectX", 1);
        //intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        //intent.putExtra("outputX", 800);
        //intent.putExtra("outputY", 600);
        // 启动裁剪程序
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_PHOTO);
    }





}
