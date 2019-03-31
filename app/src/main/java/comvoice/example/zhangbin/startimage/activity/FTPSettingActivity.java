package comvoice.example.zhangbin.startimage.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.litepal.LitePal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.FTPAccountLitepal;
import comvoice.example.zhangbin.startimage.utils.LoadingDialog;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;
import comvoice.example.zhangbin.startimage.utils.ToastUtils;

import static rx.subjects.UnicastSubject.create;

public class FTPSettingActivity extends AppCompatActivity {

    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_screen)
    ImageView ivScreen;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.et_ftp_name)
    EditText etFtpName;
    @BindView(R.id.et_ftp_password)
    EditText etFtpPassword;
    @BindView(R.id.bt_ftp_cancel)
    Button btFtpCancel;
    @BindView(R.id.bt_ftp_save)
    Button btFtpSave;
    private List<FTPAccountLitepal>ftpAccountLitepals;
    private int isFont=1;//页面销毁时，不执行子线程的返回结果
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ftpsetting);
        ButterKnife.bind(this);
        initView();
        initShow();
    }

    private void initShow() {
        ftpAccountLitepals= LitePal.findAll(FTPAccountLitepal.class);
        if(ftpAccountLitepals.size()>0){
            etFtpName.setText(ftpAccountLitepals.get(0).getFTPName());
            etFtpPassword.setText(ftpAccountLitepals.get(0).getFTPPassword());
        }
    }

    private void initView() {
        tvTitle.setText(getString(R.string.ftpsetting));
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setText(R.string.case_return);
        ftpClient=new FTPClient();

    }

    @OnClick({R.id.btn_left, R.id.bt_ftp_cancel, R.id.bt_ftp_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.bt_ftp_cancel:
                break;
            case R.id.bt_ftp_save:
                initSave();
                break;
        }
    }
    private boolean isSave=false;

    //保存FTP服务器账号密码
    private void initSave(){
        if(etFtpName.getText().toString().trim()!=null&&etFtpPassword.getText().toString().trim()!=null&& !TextUtils.isEmpty(etFtpName.getText().toString().trim())&&!TextUtils.isEmpty(etFtpPassword.getText().toString().trim())){

            initSaveFTP();
            if(isSave){
                if(isNetworkAvailable()){
                    getUrl();
                }else {
                    ToastUtils.showToast(this,"请先设置局域网WIFI");
                }
            }
        }else {
            ToastUtils.showToast(this,"请将信息填写完整");
        }

    }
    /**
     * 保存ftp服务器账户密码
     */
    private void initSaveFTP(){
        if(ftpAccountLitepals.size()>0){
            ftpAccountLitepals.get(0).setFTPName(etFtpName.getText().toString().trim());
            ftpAccountLitepals.get(0).setFTPPassword(etFtpPassword.getText().toString().trim());
            isSave=ftpAccountLitepals.get(0).save();
        }else {
            FTPAccountLitepal ftpBean=new FTPAccountLitepal();
            ftpBean.setFTPName(etFtpName.getText().toString().trim());
            ftpBean.setFTPPassword(etFtpPassword.getText().toString().trim());
            isSave=ftpBean.save();
        }
        ToastUtils.showToast(FTPSettingActivity.this,"保存成功");
    }

    /**
     * 测试ftp服务器是否登录成功
     */
    private static final String TestUrl="http://www.baidu.com";
    private URL url;
    private static int state=-1;//网络请求返回值
    private static HttpURLConnection urlConnection;
    private FTPClient ftpClient;//FTP连接
    private String strIp="118.25.70.83";//ip地址
    private int intPort=21;//端口号
    private boolean isLogin=false;
    private int netCount=0;//测试网络连接的次数
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(isFont==1){
                stopThread();
                if(loadingDialog!=null){
                    loadingDialog.dismiss();
                }
                if(ftpClient.isConnected()){
                    try {
                        ftpClient.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(msg.what==0){
//                    if(myDialog!=null){
//                        myDialog.dismiss();
//                    }
//                    SouthUtil.showToast(FTPSettingActivity.this,getString(R.string.ftpLoginSuccess));
                    ToastUtils.showToast(FTPSettingActivity.this,"设置成功");
                }else if(msg.what==1){

                    initMyDialogShow(getString(R.string.ftpLoginFaild));

                }else if(msg.what==-1){
                    if(netCount<3){
                        getUrl();
                    }else {
                        netCount = 0;
                        initMyDialogShow(getString(R.string.errornet));
                    }

                }
                if(loadingDialog!=null){
                    loadingDialog.dismiss();
                }

            }

        }

        private void initMyDialogShow(String string) {
            alertDialog=new AlertDialog.Builder(FTPSettingActivity.this).setTitle("提示框").setMessage(string).setPositiveButton("确定",null).create();
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
    };

    /**
      * 检测网络是否连接
      *
      * @return
      */
    public boolean isNetworkAvailable() {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        // 去进行判断网络是否连接
        return (info != null && info.isAvailable());
    }
    private LoadingDialog loadingDialog;
    private Thread thread=null;

    public void getUrl() {//测试网络是否可用与上网

        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.setMessage(getString(R.string.ftpLoginTest));
        loadingDialog.dialogShow();

        thread= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message message=handler.obtainMessage();
                    url = new URL(TestUrl);
                    urlConnection= (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(5000);//设置连接超时时间
                    state=urlConnection.getResponseCode();
                    Log.e("netTest",state+"");
                    if(state==302){
                        Log.e("netTest2",state+"");
                        FTPClientConfig ftpClientConfig = new FTPClientConfig();
                        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
                        ftpClient.setControlEncoding("utf-8");
                        ftpClient.configure(ftpClientConfig);

                        if (intPort > 0) {
                            ftpClient.connect(strIp, intPort);
                        }else {
                            ftpClient.connect(strIp);
                        }
                        // FTP服务器连接回答
                        int reply = ftpClient.getReplyCode();
                        if (!FTPReply.isPositiveCompletion(reply)) {
                            ftpClient.disconnect();
                            Log.e("faild","登录FTP服务失败！");
                        }
                        isLogin=ftpClient.login(etFtpName.getText().toString().trim(), etFtpPassword.getText().toString().trim());
                        // 设置传输协议
                        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                        Log.e("netTest1",isLogin+"");
                        if(isLogin){
                            message.what=0;
                        }else {
                            message.what=1;
                        }

                        handler.sendMessage(message);
                        return;
                    }else {
                        message.what=-1;
                        netCount++;
                        handler.sendMessage(message);
                        return;
                    }

                } catch (Exception e) {
                    Log.e("netTest3",state+",,,"+e.getMessage().toString());
                    Message message=handler.obtainMessage();
                    message.what=-1;
                    netCount++;
                    handler.sendMessage(message);
                    return;
                }
            }
        });
        thread.start();
    }
    private void stopThread() {
        if (thread != null) {
            thread.interrupt();
        }
    }
}
