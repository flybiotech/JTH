package comvoice.example.zhangbin.startimage.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.white.progressview.CircleProgressView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.service.UpLoadService;
import comvoice.example.zhangbin.startimage.sp.SPUtils;
import comvoice.example.zhangbin.startimage.utils.Const;
import comvoice.example.zhangbin.startimage.utils.FileUtils;
import comvoice.example.zhangbin.startimage.utils.LoadingDialog;
import comvoice.example.zhangbin.startimage.utils.NetWorkUtils;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;
import comvoice.example.zhangbin.startimage.utils.ToastUtils;
import comvoice.example.zhangbin.startimage.wifiinfo.WifiConnectManager;

public class UploadActivity extends AppCompatActivity implements UpLoadService.UpLoadFileProcess,WifiConnectManager.WifiConnectListener {

    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.btn_right)
    Button btnRight;
    @BindView(R.id.circle_progress_normal)
    CircleProgressView circleProgressNormal;
    @BindView(R.id.bt_commit)
    Button btCommit;
    private FileUtils fileUtils;
    private NetWorkUtils netWorkUtils;
    private LoadingDialog mDialog;
    private String LAN_WIFI_SSID = "";
    private String LAN_WIFI_PASS = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        fileUtils=new FileUtils(this);
        netWorkUtils=new NetWorkUtils(this);
        btnRight.setVisibility(View.GONE);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setText(R.string.case_return);
        UpLoadService.setUpLoadFileProcessListener(this);
    }

    @OnClick({R.id.btn_left, R.id.bt_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.bt_commit:
                //判断本地文件是否存在
                Const.SPscreenId="";
                if(fileUtils.isCopy(new File(Const.fromPath))){
                    LAN_WIFI_SSID= (String) SPUtils.get(this, Const.LAN_WIFI_SSID_KEY, "");
                    LAN_WIFI_PASS= (String) SPUtils.get(this, Const.LAN_WIFI_PASS_KEY,"" );
                    WifiConnectManager.getInstance().connectWifi(LAN_WIFI_SSID,LAN_WIFI_PASS,Const.WIFI_TYPE_LAN,this);

                }else {
                    ToastUtils.showToast(this,getString(R.string.file_no_exist));
                }
                break;
        }
    }




    private void showDiolog(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialog != null && mDialog.isShow()) {
                    mDialog.setMessage(msg);
                } else {
                    if (mDialog == null) {
                        mDialog = new LoadingDialog(UploadActivity.this, true);
                    }
                    mDialog.setMessage(msg);
                    mDialog.dialogShow();
                }
            }
        });

    }


    private void dismissDiolog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }

    }







    @Override
    public void getUpLoadStart() {

    }

    @Override
    public void getUpLoadFileProcessPrecent(double percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                circleProgressNormal.setProgress((int) percent);
                if (percent >= 100) {
                    ToastUtils.showToast(UploadActivity.this, getString(R.string.import_Success));
                }

            }
        });

    }

    @Override
    public void getUpLoadSuccess() {

    }

    @Override
    public void getUpLoadFaild() {
        ToastUtils.showToast(this,getString(R.string.upLoadFaild));
    }

    @Override
    public void loginOut(boolean outResult) {

    }

    @Override
    public void startWifiConnecting(String type) {
        showDiolog(getString(R.string.wifiProcessMsg));
    }

    @Override
    public void wifiConnectSuccess(String type) {
        dismissDiolog();
        if (type.equals(Const.WIFI_TYPE_LAN)) {
            netWorkUtils.getUrl();
        }


    }

    @Override
    public void wifiConnectFalid(String type) {
        dismissDiolog();
        SouthUtil.showToast(this, getString(R.string.wifiFaildMSg));
    }

    @Override
    public void wifiCycleSearch(String type,  boolean isSSID,int count) {
//        if (count <= 2) {
//            showDiolog(getString(R.string.wifiLANFailMsg));
//        }

        if (type.equals(Const.WIFI_TYPE_LAN) ) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isSSID) {
                        WifiConnectManager.getInstance().connectWithWpa(LAN_WIFI_SSID,LAN_WIFI_PASS);
                    } else {
                        if (count <= 2) {
                            showDiolog(getString(R.string.wifiLANFailMsg));
                        }
                    }

                }
            });
        }



    }

    @Override
    public void wifiInputNameEmpty(String type) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissDiolog();
        WifiConnectManager.getInstance().stopThreadConnectWifi();
    }
}
