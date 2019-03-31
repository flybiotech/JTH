package comvoice.example.zhangbin.startimage.utils;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.service.UpLoadService;

/**
 * 判断是否连接wifi,wifi连接是否可上网，ftp是否连接成功
 */
public class NetWorkUtils {
    private Context context;
    private static URL url;//请求的url地址
    private static int state=-1;//网络请求返回值
    private static HttpURLConnection urlConnection;
    private static final String TestUrl="http://www.baidu.com";
    private UploadUtils uploadUtils;
    private ConnectivityManager connectManager;
    public NetWorkUtils(Context context){
        this.context=context;
        uploadUtils=new UploadUtils(context);
    }
    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stopThread();
            if(msg.what==0){
                Intent intent=new Intent(context, UpLoadService.class);
                context.startService(intent);
            }else if(msg.what==-1){
                ToastUtils.showToast(context,context.getString(R.string.errornet));
            }
           dismissDiolog();
        }
    };

    private LoadingDialog loadingDialog0;
    private LoadingDialog loadingDialog1;
    private int COUNT = 0;
    private int msgWhat = -2;
    private Thread thread;
    public void getUrl() {//测试网络是否可用与上网
        showDiolog();
        if (connectManager == null) {
            connectManager = (ConnectivityManager) context. getSystemService(Context.CONNECTIVITY_SERVICE);
        }
      thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    SystemClock.sleep(1000);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            NetworkCapabilities networkCapabilities = connectManager.getNetworkCapabilities(connectManager.getActiveNetwork());
                            /**
                             * NOT_METERED&INTERNET&NOT_RESTRICTED&TRUSTED&NOT_VPN
                             *
                             */

                            boolean valudated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                            Log.e("TAG_Avalible", "NetworkCapalbilities:" +valudated);
                            if (valudated) {
                                msgWhat = 0;
                            } else {
                                msgWhat = -1;
                            }

                        } catch (Exception e) {
                            msgWhat = -1;
                        }

                    } else { //6.0以下

                        url = new URL("http");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setConnectTimeout(5000);//设置连接超时时间
                        state = urlConnection.getResponseCode();
                        if (state == 302) {
                            msgWhat = 0;
                        } else {
                            msgWhat = -1;
                        }

                    }


                } catch (Exception e) {
                    msgWhat = -1;
                }finally {
                    Log.e("TAG_Avalible", "run: COUNT = "+COUNT );
                    if (msgWhat == -1&&COUNT<3) {
                        COUNT++;
                        getUrl();
                    } else {
                        COUNT = 0;
                        mHandler.sendEmptyMessage(msgWhat);
                    }

                }
            }
        });
        thread.start();
    }

    private void stopThread(){
        if(thread!=null){
            thread.interrupt();
        }
    }
    private void showDiolog() {
        String msg = context.getString(R.string.testWifi);

        show1(msg);

    }

    //1是系统中心的上传界面
    private void show1(String msg1) {
        try {
//            Log.e("TAG_show0", "showDiolog:  thread = "+Thread.currentThread() );
            if (loadingDialog1 != null && loadingDialog1.isShow()) {
                loadingDialog1.setMessage(msg1);
            } else {
                if (loadingDialog1 == null) {
                    loadingDialog1 = new LoadingDialog(context);
                }
                loadingDialog1.setMessage(msg1);
                loadingDialog1.dialogShow();
            }
        } catch (Exception e) {
//            Log.e("TAG_show0", "showDiolog: "+e.getMessage() );
            e.printStackTrace();
        }
    }
    private void dismissDiolog() {
        if (loadingDialog0 != null) {
            loadingDialog0.dismiss();
        }

        if (loadingDialog1 != null) {
            loadingDialog1.dismiss();
        }
    }
}
