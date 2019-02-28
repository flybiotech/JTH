package comvoice.example.zhangbin.startimage.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.litepal.LitePal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.TimeZone;

import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.activity.FTPSettingActivity;
import comvoice.example.zhangbin.startimage.model.FTPAccountLitepal;
import comvoice.example.zhangbin.startimage.model.User;
import comvoice.example.zhangbin.startimage.service.UpLoadService;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class UploadUtils {
    private Context mContext;
    private List<User>userList;
    private LoadingDialog loadingDialog;//导出时进度条
    private AlertDialog alertDialog;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(loadingDialog!=null){
                dismissDiolog();
            }
            switch (msg.what){
                case 0:
//                    UpLoadService upLoadService=new UpLoadService();
                    Intent intent=new Intent(mContext, UpLoadService.class);
                    mContext.startService(intent);
                    break;
                case 1:
                    initMyDialogShow(mContext.getString(R.string.ftpLoginFaild));
                    break;
                case 2:

                    break;
                case 3:
                    break;
                    default:break;
            }
        }
    };
    public UploadUtils(Context context){
        this.mContext=context;
        loadingDialog=new LoadingDialog(mContext);
    }
    private void showDiolog() {

            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(mContext);
            }
            loadingDialog.setMessage(mContext.getString(R.string.ftpLoginTest));
            loadingDialog.dialogShow();
    }

    private void dismissDiolog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
    private void initMyDialogShow(String string) {
        alertDialog=new AlertDialog.Builder(mContext).setTitle("提示框").setMessage(string).setPositiveButton("确定",null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button bt_cancel=alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        alertDialog.show();
    }
    public void updateFile() {
        showDiolog();
        Observable.create(new Observable.OnSubscribe<List<User>>() {
            @Override
            public void call(Subscriber<? super List<User>> subscriber) {
                userList = LitePal.findAll(User.class);
                subscriber.onNext(userList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<User>>() {
                    @Override
                    public void call(List<User> listMessages) {
                        if (listMessages.size() > 0) {
//                            boolean isFTP= getFTP();
//                            if(isFTP){
//                                getUrl();
//                            }else {
////                                initMyDialog(mContext.getString(R.string.ftpAccountPasswordNull),0);
//                                initMyDialogShow(mContext.getString(R.string.ftpAccountPasswordNull));
//                            }
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.patient_data_no) + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //判断是否已有FTP账号密码
    public boolean getFTP(){
        boolean isTrue=false;
        List<FTPAccountLitepal> ftpBeanList= LitePal.findAll(FTPAccountLitepal.class);
        if(ftpBeanList.size()>0&&!ftpBeanList.get(0).getFTPName().equals("")&&!ftpBeanList.get(0).getFTPPassword().equals("")){
            isTrue=true;
        }
        return isTrue;
    }
    private static HttpURLConnection urlConnection;
    private String strIp="118.25.70.83";//ip地址
    private int intPort=21;//端口号
    private boolean isLogin=false;
    private FTPClient ftpClient;//FTP连接
    /**
     * @退出关闭服务器链接
     * */
    public void ftpLogOut() {

        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                boolean reuslt = this.ftpClient.logout();// 退出FTP服务器
                if (reuslt) {
                    Log.e("success","成功退出服务器");
                }
            }catch (IOException e) {
                e.printStackTrace();
                Log.e("success","退出FTP服务器异常！" + e.getMessage());
            }finally {
                try {
                    this.ftpClient.disconnect();// 关闭FTP服务器的连接
                }catch (IOException e) {
                    e.printStackTrace();
                    Log.e("success","关闭FTP服务器的连接异常！");
                }
            }
        }
    }

    private void stopThread(){
        if(thread!=null){
            thread.interrupt();
        }
    }


    private Thread thread=null;
    public void getUrl() {//测试ftp账户是否成功登录
        if(ftpClient==null){
            ftpClient=new FTPClient();
        }
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<FTPAccountLitepal> ftpBeanList= LitePal.findAll(FTPAccountLitepal.class);
                    FTPClientConfig ftpClientConfig = new FTPClientConfig();
                    ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
                    ftpClient.setControlEncoding("utf-8");
                    ftpClient.configure(ftpClientConfig);
                    ftpClient.enterLocalPassiveMode();
                    Message message=handler.obtainMessage();
                    Log.e("faild11","登录FTP服务失败！"+intPort+",,"+ftpBeanList.get(0).getFTPName()+",,,,"+ftpBeanList.get(0).getFTPPassword()+",,,"+ftpBeanList.size());

                    if (intPort > 0) {
                        if(!ftpClient.isConnected()){
                            Log.e("faild55","登录FTP服务失败！"+intPort);
                            ftpClient.connect(strIp, intPort);
                        }
//                            ftpClient.connect(strIp, intPort);
                    }else {
                        if(!ftpClient.isConnected()){
                            Log.e("faild51","登录FTP服务失败！"+intPort);
                            ftpClient.connect(strIp);
                        }
//                            ftpClient.connect(strIp);
                        Log.e("faild66","登录FTP服务失败！"+intPort);
                    }
                    // FTP服务器连接回答
                    int reply = ftpClient.getReplyCode();
                    if (!FTPReply.isPositiveCompletion(reply)) {
                        ftpClient.disconnect();
                        message.what=1;
                        ftpLogOut();
                        handler.sendMessage(message);
                        return;

                    }
                    isLogin=ftpClient.login(ftpBeanList.get(0).getFTPName(), ftpBeanList.get(0).getFTPPassword());
                    // 设置传输协议
                    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    Log.e("faild33","登录FTP服务失败！"+ftpBeanList.get(0).getFTPName()+",,,,"+ftpBeanList.get(0).getFTPPassword());
                    if(isLogin){
                        Log.e("faild21","登录FTP服务成功！");
                        ftpLogOut();
                        message.what=0;
                    }else {
                        Log.e("faild22","登录FTP服务失败！");
                        ftpLogOut();
                        message.what=1;
                    }
                    handler.sendMessage(message);
                    return;

                } catch (Exception e) {
                    Message message=handler.obtainMessage();
                    message.what=1;
                    ftpLogOut();
                    handler.sendMessage(message);
                    Log.e("faild00","登录FTP服务失败！"+e.getMessage().toString());
                    return;
                }
            }
        });
        thread.start();
    }
}
