package com.example.zhujia.dx_dms.Activity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhujia.dx_dms.R;
import com.example.zhujia.dx_dms.Tools.Net.Constant;
import com.example.zhujia.dx_dms.Tools.Net.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ZHUJIA on 2018/3/15.
 * 新增集团信息
 */

public class AddGroupinformation_Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView text1;
    private Toolbar toolbar;
    private EditText groupCode,groupName;
    private java.util.Calendar cal;
    private int year,month,day,hour,minute;
    TimePickerDialog dialog1;
    String time;
    Intent intent;
    String date;
    JSONObject object;
    private SharedPreferences sharedPreferences;
    private String token;



    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addgroupinformation_xml);
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
        getDate();
    }

    @SuppressLint("WrongConstant")
    private void getDate(){
        cal= java.util.Calendar.getInstance();
        year=cal.get(java.util.Calendar.YEAR);
        month=cal.get(java.util.Calendar.MONTH);
        day=cal.get(java.util.Calendar.DAY_OF_MONTH);

    }

    private void initUI(){
        text1=(TextView)findViewById(R.id.text1);
        groupCode=(EditText)findViewById(R.id.groupCode);
        groupName=(EditText)findViewById(R.id.groupName);


        if(intent.getStringExtra("type")==null){
            text1.setText("修改信息");
            groupCode.setText(intent.getStringExtra("groupCode"));
            groupName.setText(intent.getStringExtra("groupName"));
        }else {
            text1.setText("新增信息");
        }


    }

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
            try {
                object.put("groupCode",groupCode.getText().toString());
                object.put("groupName",groupName.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String params=object.toString();
            //保存
            if(TextUtils.isEmpty(groupCode.getText().toString())){
                Toast.makeText(getApplicationContext(),"集团编号不能为空",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(groupName.getText().toString())){
                Toast.makeText(getApplicationContext(),"集团名称不能为空",Toast.LENGTH_SHORT).show();
            }else if(intent.getStringExtra("type")==null) {

                //修改
                new HttpUtils().postJson(Constant.APPURLS + "/group/groupbase/update" + "/" + intent.getStringExtra("id"),params,token, new HttpUtils.HttpCallback() {

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
                else {



                Log.e("TAG", "login: "+object );
                    //新增

                    new HttpUtils().postJson(Constant.APPURLS+"/group/groupbase/add",params,token,new HttpUtils.HttpCallback() {

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
                            Toast.makeText(AddGroupinformation_Activity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            Intent data=new Intent();
                            data.putExtra("freshen","y");
                            setResult(RESULT_OK,data);
                            finish();
                        }

                        break;

                    default:
                        Toast.makeText(AddGroupinformation_Activity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onClick(View v) {
    /*    if(v==expireTime){
            //选择日期
            final DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    date=i+"-"+(++i1)+"-"+i2;
                    dialog1.show();
                }
            };

            TimePickerDialog.OnTimeSetListener listener1=new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    time=hourOfDay+":"+minute;
                    DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date1 = null;
                    try {
                        date1 = format1.parse(date+" "+time);
                        String startime = format1.format(date1);
                        expireTime.setText(startime+":"+"00");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                ;            };
            dialog1=new TimePickerDialog(this,TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT,listener1,hour,minute,true);
            DatePickerDialog dialog=new DatePickerDialog(this,DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT,listener,year,month,day);
            dialog.show();

        }*/
    }
}
