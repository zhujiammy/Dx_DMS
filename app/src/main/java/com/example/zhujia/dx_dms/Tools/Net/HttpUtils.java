package com.example.zhujia.dx_dms.Tools.Net;

/**
 * Created by ZHUJIA on 2018/1/23.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.zhujia.dx_dms.Tools.MyApplication;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.valueOf;

/**
 * okhttp请求
 * @author Flyjun
 *
 */
public class HttpUtils{


    private Context mContext;
    private SharedPreferences sharedPreferences;
    public static String TAG="debug-okhttp";
    public static boolean isDebug=true;
    private OkHttpClient client;
    // 超时时间
    public static final int TIMEOUT = 1000 * 60;
    public static final MediaType FORM_CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    //json请求
    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    private String companyId;
    private Handler handler = new Handler(Looper.getMainLooper());

    @SuppressLint("WrongConstant")
    public HttpUtils() {
        // TODO Auto-generated constructor stub
        mContext= MyApplication.getContext();
        sharedPreferences =mContext.getSharedPreferences("Session",
                Context.MODE_APPEND);
       companyId=sharedPreferences.getString("companyId","");
        this.init();
    }

    @SuppressLint("WrongConstant")
    private void init() {

        client = new OkHttpClient();
        client.sslSocketFactory();

        // 设置超时时间
        client.newBuilder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS).build();
    }

    /**
     * post请求，json数据为body
     *
     * @param url
     * @param json
     * @param callback
     */
    public void postJson(String url, String json,String token,final HttpCallback callback) {
        Log.e("TAG", "HttpUtils: "+companyId );
        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).addHeader("token",token).addHeader("companyId",companyId).post(body).build();
        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     * post请求，json数据为body
     *
     * @param url
     * @param json
     * @param callback
     */
    public void LoginPost(String url, String json, final HttpCallback callback) {

        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).post(body).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }



    /**
     * post请求，json数据为body
     *
     * @param url
     * @param callback
     */
    public void LogoutPost(String url,String token,final HttpCallback callback) {

        RequestBody body = RequestBody.create(null,"");
        Request request = new Request.Builder().url(url).addHeader("token",token).addHeader("companyId",companyId).post(body).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     *
     *
     * @param url
     * @param callback
     */
    public void Post(String url,String token,final HttpCallback callback) {

        RequestBody body = RequestBody.create(null,"");
        Request request = new Request.Builder().url(url).addHeader("token",token).addHeader("companyId",companyId).post(body).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }



    /**
     *
     *
     * @param url
     * @param callback
     */
    public void Get(String url,String token,final HttpCallback callback) {

        Request request = new Request.Builder().url(url).addHeader("token",token).addHeader("companyId",companyId).get().build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     * put请求，json数据为body
     *
     * @param url
     * @param json
     * @param callback
     */
    public void putJson(String url, String json,String token, final HttpCallback callback) {

        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).addHeader("token",token).addHeader("companyId",companyId).put(body).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     * delete请求，json数据为body
     *
     * @param url
     * @param callback
     */
    public void deleteJson(String url,String token,final HttpCallback callback) {

        //RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).addHeader("token",token).addHeader("companyId",companyId).delete().build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     * update请求，json数据为body
     *
     * @param url
     * @param callback
     */
    public void update(String url,String json,String token,final HttpCallback callback) {

        //RequestBody body = RequestBody.create(JSON,json);
        RequestBody body=RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).addHeader("token",token).put(body).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }

    /**
     * post请求 map为body
     *
     * @param url
     * @param map
     * @param callback
     */
    public void post(String url, Map<String, String> map,String token,final HttpCallback callback) {

       /* FormBody.Builder builder = new FormBody.Builder();
         FormBody body=new FormBody.Builder().add("key", "value").build();*/

        /**
         * 创建请求的参数body
         *
         */
        FormBody.Builder builder = new FormBody.Builder();

        /**
         * 遍历key
         */
        if (null != map) {
            for ( Map.Entry<String, String> entry : map.entrySet() ) {
                builder.add( entry.getKey(), entry.getValue() );
            }
        }
        RequestBody formBody = builder.build();
        Request request = new Request.Builder().url(url).addHeader("token",token).post(formBody).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                onError(callback, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    onSuccess(callback, response.body().string());

                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     *
     * put请求
     *
     *
     */


    public void doPut(String url, Map<String, String> map,final HttpCallback callback) {

        // FormBody.Builder builder = new FormBody.Builder();
        // FormBody body=new FormBody.Builder().add("key", "value").build();

        /**
         * 创建请求的参数body
         *
         */
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        /**
         * 遍历key
         */
        if (null != map) {
            for (Map.Entry<String,String> entry : map.entrySet()) {

                System.out.println("Key = " + entry.getKey() + ", Value = "
                        + entry.getValue());
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));

            }
        }

        Request request = new Request.Builder().url(url).put(requestBody.build()).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                onError(callback, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    onSuccess(callback, response.body().string());

                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     *
     * doDelete请求
     *
     *
     */


    public void doDelete(String url, Map<String, String> map,final HttpCallback callback) {

        // FormBody.Builder builder = new FormBody.Builder();
        // FormBody body=new FormBody.Builder().add("key", "value").build();

        /**
         * 创建请求的参数body
         *
         */
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        /**
         * 遍历key
         */
        if (null != map) {
            for (Map.Entry<String,String> entry : map.entrySet()) {

                System.out.println("Key = " + entry.getKey() + ", Value = "
                        + entry.getValue());
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));

            }
        }

        Request request = new Request.Builder().url(url).delete(requestBody.build()).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                onError(callback, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    onSuccess(callback, response.body().string());

                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     * get请求
     * @param url
     * @param callback
     */
    public void get(String url, final HttpCallback callback) {

        Request request = new Request.Builder().url(url).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                onError(callback, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    onSuccess(callback, response.body().string());

                } else {
                    onError(callback, response.message());
                }
            }
        });

    }

    //文件上传
    public void postUpload(String url,String token, File fileList, String newfilename,final HttpCallback callback) {
        /**
         * 创建请求的参数body
         */
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);


        RequestBody fileBody1 = RequestBody.create(MediaType.parse("image/png") , fileList);

        requestBody.addFormDataPart("file",newfilename,fileBody1);
        /* 下边的就和post一样了 */

        Request request = new Request.Builder().url(url).addHeader("token",token).addHeader("companyId",companyId).post(requestBody.build()).build();
        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                onError(callback, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    onSuccess(callback, response.body().string());

                } else {
                    onError(callback, response.message());
                }
            }


        });


    }

    /**
     * log信息打印
     * @param params
     */
    public void debug(String params){
        if(false == isDebug){
            return;
        }

        if(null == params){
            Log.d(TAG, "params is null");
        }else{
            Log.d(TAG, params);
        }
    }

    private void onStart(HttpCallback callback) {
        if (null != callback) {
            callback.onStart();
        }
    }

    private void onSuccess(final HttpCallback callback, final String data) {

        debug(data);

        if (null != callback) {
            handler.post(new Runnable() {
                public void run() {
                    // 需要在主线程的操作。
                    callback.onSuccess(data);
                }
            });
        }
    }

    private void onError(final HttpCallback callback,final String msg) {
        if (null != callback) {
            handler.post(new Runnable() {
                public void run() {
                    // 需要在主线程的操作。
                    callback.onError(msg);
                }
            });
        }
    }


    /**
     * http请求回调
     *
     * @author Flyjun
     *
     */
    public static abstract class HttpCallback {
        // 开始
        public void onStart() {};

        // 成功回调
        public abstract void onSuccess(String data);

        // 失败回调
        public void onError(String msg) {};
    }

}
