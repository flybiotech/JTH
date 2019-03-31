package comvoice.example.zhangbin.startimage.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.UserBean;
import comvoice.example.zhangbin.startimage.service.UpLoadService;
import comvoice.example.zhangbin.startimage.sp.SPUtils;
import comvoice.example.zhangbin.startimage.utils.AlignedTextUtils;
import comvoice.example.zhangbin.startimage.utils.Const;
import comvoice.example.zhangbin.startimage.utils.FileUtils;
import comvoice.example.zhangbin.startimage.utils.InstallApk;
import comvoice.example.zhangbin.startimage.utils.LitepalUtils;
import comvoice.example.zhangbin.startimage.utils.LoadingDialog;
import comvoice.example.zhangbin.startimage.utils.NetWorkUtils;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;
import comvoice.example.zhangbin.startimage.wifiinfo.WifiConnectManager;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class ShowActivity extends AppCompatActivity implements UpLoadService.UpLoadFileProcess, WifiConnectManager.WifiConnectListener ,FileUtils.FileCopyAndDelListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    //    @BindView(R.id.iv_screen)
//    ImageView ivScreen;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.et_pName)
    EditText etPName;
    @BindView(R.id.tr)
    TableRow tr;
    @BindView(R.id.tv_2)
    TextView tv2;
    @BindView(R.id.et_pPhone)
    EditText etPPhone;
    @BindView(R.id.tv_3)
    TextView tv3;
    @BindView(R.id.tv_4)
    TextView tv4;
    @BindView(R.id.et_pHPV)
    EditText etPHPV;
    @BindView(R.id.iv_scanhpv)
    ImageView ivScanhpv;
    //    @BindView(R.id.tv_5)
//    TextView tv5;
    @BindView(R.id.ll_main)
    LinearLayout llMain;
    @BindView(R.id.bt_clear)
    Button btClear;
    @BindView(R.id.bt_img)
    Button btImg;
    Unbinder unbinder;
    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.et_pScrennID)
    EditText etPScrennID;
    @BindView(R.id.et_pAge)
    EditText etPAge;
    @BindView(R.id.tv_6)
    TextView tv6;
    @BindView(R.id.tl)
    TableLayout tl;
    @BindView(R.id.view1)
    View view1;
    private String[] strings;
    private LitepalUtils litepalUtils;
    private InstallApk installApk;
    private FileUtils fileUtils;
    private String screeningId, name, age, mobile, createAt, hpv;
    private boolean isReview;//判断第几次筛查
    private boolean isCopy;//判断是否已复制
    private String msg;
    private LoadingDialog mDialog;
    private String LAN_WIFI_SSID = "";
    private String LAN_WIFI_PASS = "";
    private String SZB_WIFI_SSID = "";
    private String SZB_WIFI_PASS = "";
    String ssid = "";
    String pass = "";
    String msgStr = "";
    private NetWorkUtils netWorkUtils;

    private int isHaveSave=0;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dismissDiolog();
                    SouthUtil.showToast(ShowActivity.this, getString(R.string.register_verify_failed));
                    break;
                case -1: //核销失败，的各种情况
                    String str = (String) msg.obj;
                    SouthUtil.showToast(ShowActivity.this, str);
                    dismissDiolog();
                    break;

                case 1://核销成功
                    //保存数据
                    String ss = (String) msg.obj;
                    Log.e("TAG_1", "handleMessage: 核销成功 = " + ss);
                    SouthUtil.showToast(ShowActivity.this, getString(R.string.register_verify_success));
                    if(isHaveSave==0){
                        initSaveMsg();
                    }

                    break;
                default:
                    break;

            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show);
        unbinder = ButterKnife.bind(this);
        msg = getIntent().getStringExtra("msg");
        initView();
        initTextView();
    }

    private void initView() {
        tvTitle.setText(this.getString(R.string.patients_detail));
        tr.requestFocus();
        litepalUtils = new LitepalUtils(this);
        installApk = new InstallApk(this);
        fileUtils = new FileUtils(this,this);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setText(R.string.case_return);
        UpLoadService.setUpLoadFileProcessListener(this);
        netWorkUtils = new NetWorkUtils(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Const.SPscreenId = initGetSP();
        Log.e("ceshi11111", Const.SPscreenId);
        getWifiSSID();
        if (msg != "") {
            jsonMsg(msg);
        } else {
            showDiolog(getString(R.string.fileCopying));
            fileUtils.startCopyFileAndDel();
        }
    }

    // 获取wifi的 ssid
    private void getWifiSSID() {
        LAN_WIFI_SSID = (String) SPUtils.get(this, Const.LAN_WIFI_SSID_KEY, "");
        LAN_WIFI_PASS = (String) SPUtils.get(this, Const.LAN_WIFI_PASS_KEY, "");
        SZB_WIFI_SSID = (String) SPUtils.get(this, Const.SZB_WIFI_SSID_KEY, "");
        SZB_WIFI_PASS = (String) SPUtils.get(this, Const.SZB_WIFI_PASS_KEY, "");
    }



    //将字符串排列整齐
    private void initTextView() {
        strings = new String[]{getString(R.string.screenId), getString(R.string.name), getString(R.string.age), getString(R.string.phone),
                getString(R.string.RequiredHPV)};
        TextView[] textViews = {tv1, tv2, tv3, tv4, tv6};
        for (int i = 0; i < strings.length; i++) {
            if (textViews[i] != null) {
                textViews[i].setText(AlignedTextUtils.justifyString(strings[i], 4));
            }
        }
    }

    @OnClick({R.id.iv_screen, R.id.iv_scanhpv, R.id.bt_clear, R.id.bt_img, R.id.btn_left})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.iv_scanhpv:
                startActivityForResult(new Intent(this, ScanningActivity.class), 1);
                break;
            case R.id.bt_clear:
                clearInput();
                break;
            case R.id.bt_img:
//                //先判断是否已自动复制，复制后再进行核销，核销成功后保存信息，最后获取图像
                Log.e("TAG_1", "onViewClicked: isCopy = " + isCopy);
                isCopy = fileUtils.isCopy(new File(Const.originalPath));
                if (!isCopy) {
                    //开始链接局域网
                    WifiConnectManager.getInstance().connectWifi(LAN_WIFI_SSID, LAN_WIFI_PASS, Const.WIFI_TYPE_LAN, this);
                }
//                boolean isHave = installApk.isInstall();
//                if(!isHave){
//                    installApk(this,Const.FLY_SZB_apk);//打印服务插件本地路径);
//                }
                break;
            case R.id.btn_left:
                finish();
                break;
        }
    }

    //清空输入框
    private void clearInput() {
        etPScrennID.setText("");
        etPName.setText("");
        etPAge.setText("");
        etPPhone.setText("");
        etPHPV.setText("");
    }

    /**
     * 保存成功后更新本地存储的screenid，用做复制的目标路径
     */
    private void initSaveSP() {
        SPUtils.put(this, Const.SCREENID_KEY, screeningId);
    }

    /**
     * 获取保存的本地信息
     */
    private String initGetSP() {
        return (String) SPUtils.get(this,  Const.SCREENID_KEY, "");
    }

    /**
     * 核销成功后保存个人信息
     */
    private void initSaveMsg() {
        hpv = etPHPV.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(screeningId)) {
            SouthUtil.showToast(this, getString(R.string.register_verify_needinfo));
        } else {
            boolean isSaveSuceess = litepalUtils.userSave(etPScrennID.getText().toString(), etPName.getText().toString(), etPAge.getText().toString(), etPPhone.getText().toString(), etPHPV.getText().toString());
            if (isSaveSuceess) {
                msg = "";
                isHaveSave=1;
                initSaveSP();
                //开始连接主机wifi
                WifiConnectManager.getInstance().connectWifi(SZB_WIFI_SSID, SZB_WIFI_PASS, Const.WIFI_TYPE_SZB, this);

            } else {
                SouthUtil.showToast(this, getString(R.string.msgSaveFaild));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                etPHPV.setText(data.getStringExtra("text"));
            }else if(requestCode == 2){
                installApk.initInstallAPK();
            }
        }
    }


    /**
     * 解析二维码中的数据
     */
    private void jsonMsg(final String msg) {
        try {
            if (msg != null) {
                JSONObject jsonObject = new JSONObject(msg);
                if (jsonObject.has("screeningId")) {
                    screeningId = jsonObject.getString("screeningId");
                }
                if (jsonObject.has("name")) {
                    name = jsonObject.getString("name");
                }
                if (jsonObject.has("mobile")) {
                    mobile = jsonObject.getString("mobile");
                }
                if (jsonObject.has("age")) {
                    age = jsonObject.getString("age");
                }
                if (jsonObject.has("isReview")) {
                    isReview = jsonObject.getBoolean("isReview");
                }
                if (jsonObject.has("createAt")) {
                    createAt = jsonObject.getString("createAt");
                }
                etPScrennID.setText(screeningId);
                etPName.setText(name);
                etPPhone.setText(mobile);
                etPAge.setText(age);
                if (isReview) {
                    Toast.makeText(this, getString(R.string.screenHPV), Toast.LENGTH_SHORT).show();
                    etPHPV.setEnabled(false);
                    etPHPV.setText(getString(R.string.hpv_no));
                    ivScanhpv.setVisibility(View.GONE);
                } else {
                    etPHPV.setEnabled(true);
                    ivScanhpv.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(this, getString(R.string.screenFaild), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                        mDialog = new LoadingDialog(ShowActivity.this, true);
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


    //开始核销
    private void verifyMobile(int scrreningId, String moblie, String hpv) {
        if (isReview) {
            hpv = "";
        }

        /**
         * {"message":"该手机号不是医生用户","code":-1}
         * {"message":"该码已使用，于2018-06-07 17:49:26被核销","code":-1}
         */
        JSONObject jsonObject = new JSONObject();
        String verifyJson = "";
        try {
//                    jsonObject.put("id", 1);
            //1 正确的
            jsonObject.put("screeningId", scrreningId);
            // 正确的号码 17621140126
            jsonObject.put("mobile", moblie);
            // 101 正确的
            jsonObject.put("hpvNumber", hpv);
            verifyJson = jsonObject.toString();
//            Log.e("TAG_", "json数据:  " + verifyJson);
        } catch (JSONException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(0);
        }

        final String basic = Credentials.basic(Const.MD5_USERNAME, Const.MD5_PASSWORD
        );
        MediaType jsonType = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient client = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        return response.request().newBuilder().header("Authorization", basic).build();
                    }
                })
                .build();
        RequestBody formBody = RequestBody.create(jsonType, verifyJson);
        final Request request = new Request.Builder()
                .url(Const.URL_VERIFY)
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG_2", "onFailure:2 ");
                mHandler.sendEmptyMessage(0);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                UserBean bean = new Gson().fromJson(msg, UserBean.class);
                if (bean != null) {
                    //核销成功 就是1 ，失败就是 -1
                    Message msgobj = new Message();
                    msgobj.obj = bean.getMessage();
                    msgobj.what = bean.getCode();
                    mHandler.sendMessage(msgobj);
                } else {
                    mHandler.sendEmptyMessage(0);
                }


            }
        });
    }


    @Override//开始连接wifi
    public void startWifiConnecting(String type) {
        if (type.equals(Const.WIFI_TYPE_SZB)) {
            showDiolog(getString(R.string.wifiProcessMsgSZB));
        } else if (type.equals(Const.WIFI_TYPE_LAN)) {
            showDiolog(getString(R.string.wifiProcessMsgLAN));
        }

    }

    @Override //wifi 连接成功
    public void wifiConnectSuccess(String type) {

        if (type.equals(Const.WIFI_TYPE_LAN)) {

            if (msg.equals("")) { //文件上传
                dismissDiolog();
                if(fileUtils.isCopy(new File(Const.fromPath+Const.SPscreenId+"/"))){

                    netWorkUtils.getUrl();
                }
            } else {

               //核销
                int scrid = Integer.parseInt(etPScrennID.getText().toString().trim());
                //医生的手机号码
                String mobile = (String) SPUtils.get(this, Const.DOC_MOBILE, "");
                String hpv = etPHPV.getText().toString().trim();
                boolean result = WifiConnectManager.getInstance().getWifiConnectResult();
                showDiolog(getString(R.string.register_verify_start));
                if (!result) { //表示没有切换wifi，直接进行 核销
                    //wifi链接成功之后，开始核销，
                    verifyMobile(scrid, mobile, hpv);
                } else { //表示切换了wifi，需要停顿2秒 ，要不然会核销失败
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(1000 * 2);
                            //wifi链接成功之后，开始核销，
                            verifyMobile(scrid, mobile, hpv);

                        }
                    }).start();
                }

            }
        } else if (type.equals(Const.WIFI_TYPE_SZB)) {
            dismissDiolog();
            boolean isHave = installApk.isInstall();
            if(!isHave){
                installApk(this,Const.FLY_SZB_apk);//插件本地路径);
            }
        }
    }
    //根据android 版本选择不同的安装方式
    private void installApk(Context context, String fileApk) {

        if (fileApk != null) {
            File file = new File(fileApk);
            if (Build.VERSION.SDK_INT >= 24) {//android 7.0以上
               installApk.initInstallAPK();
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//Android 8.0以上，增加了一个未知来源安装的权限
                if(!this.getPackageManager().canRequestPackageInstalls()){
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    startActivityForResult(intent,2);
                }else {
                    installApk.initInstallAPK();
                }
            } else{//android 6.0以下直接安装
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                context.startActivity(install);
            }
        } else {
//            SouthUtil.showToast(this, this.getString(R.string.print_download_faild));
        }
    }
    @Override //wifi连接失败
    public void wifiConnectFalid(String type) {
        dismissDiolog();
        SouthUtil.showToast(this, getString(R.string.wifiFaildMSg));
    }


    @Override  //没有搜到到指定的wifi ,将会在子线程中持续 搜索指定的wifi
    public void wifiCycleSearch(String type, boolean isSSID, int count) {


        if (type.equals(Const.WIFI_TYPE_SZB)) {
            ssid = SZB_WIFI_SSID;
            pass = SZB_WIFI_PASS;
            msgStr = getString(R.string.wifiSZBFailMsg);
        } else if (type.equals(Const.WIFI_TYPE_LAN)) {
            ssid = LAN_WIFI_SSID;
            pass = LAN_WIFI_PASS;
            msgStr = getString(R.string.wifiLANFailMsg);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSSID) {
                    WifiConnectManager.getInstance().connectWithWpa(ssid, pass);
                } else {
                    Log.e("TAG_", "ShowActivity : msgStr = "+msgStr+" ,count = "+count );
                    if (count < 2) {

                        showDiolog(msgStr);
                    }
                }

            }
        });

    }

    @Override  //输入的wifi名称为空
    public void wifiInputNameEmpty(String type) {
        dismissDiolog();
        if (type.equals(Const.WIFI_TYPE_LAN)) {
            SouthUtil.showToast(ShowActivity.this, getString(R.string.wifi_LANname_empty));
        } else if (type.equals(Const.WIFI_TYPE_SZB)) {
            SouthUtil.showToast(ShowActivity.this, getString(R.string.wifi_SZBname_empty));
        }

    }



    @Override
    public void getUpLoadStart() {

    }

    @Override
    public void getUpLoadFileProcessPrecent(double percent) {
        Log.e("percent11",percent+"%");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (percent >= 100.0) {
                    showDiolog("已上传:" + (int) percent + " %");
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            dismissDiolog();
                            Intent intent = new Intent(ShowActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(timerTask, 2000);
                } else {
                    showDiolog("已上传:" + (int) percent + " %");
                }
            }
        });
    }

    @Override
    public void getUpLoadSuccess() {

    }

    @Override
    public void getUpLoadFaild() {
        showDiolog(getString(R.string.upLoadFaild));
    }

    @Override
    public void fileCopySuccess() {
        if (msg.equals("")) {
            WifiConnectManager.getInstance().connectWifi(LAN_WIFI_SSID, LAN_WIFI_PASS,Const.WIFI_TYPE_LAN,this);
        }
    }

    @Override
    public void fileCopyFailed(String msg) {
        dismissDiolog();
//        SouthUtil.showToast(this,msg);
    }

    @Override
    public void fileDelSuccess() {

    }

    @Override
    public void fileDelFailed() {

    }

    @Override
    public void fileStartCopy() {

    }


    @Override
    protected void onStop() {
        super.onStop();
        dismissDiolog();
        WifiConnectManager.getInstance().stopThreadConnectWifi();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}

