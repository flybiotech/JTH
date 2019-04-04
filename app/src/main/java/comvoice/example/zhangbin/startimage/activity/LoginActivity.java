package comvoice.example.zhangbin.startimage.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.UserBean;
import comvoice.example.zhangbin.startimage.service.DeleteService;
import comvoice.example.zhangbin.startimage.sp.SPUtils;
import comvoice.example.zhangbin.startimage.utils.Const;
import comvoice.example.zhangbin.startimage.utils.LitepalUtils;
import comvoice.example.zhangbin.startimage.utils.LoadingDialog;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;
import comvoice.example.zhangbin.startimage.wifiinfo.WifiConnectManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements WifiConnectManager.WifiConnectListener, WifiConnectManager.WifiConnectListener.WifiPingListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_screen)
    ImageView ivScreen;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.edit_loginName)
    EditText editLoginName;
    @BindView(R.id.tv_loginpass)
    TextView tvLoginpass;
    @BindView(R.id.edit_loginPass)
    EditText editLoginPass;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.ll_login)
    LinearLayout llLogin;
    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.down_but)
    Button downBut;
    @BindView(R.id.login_ll)
    LinearLayout loginLl;

    @BindView(R.id.forgetPassword)
    TextView forgetPassword;

    private LitepalUtils litepalUtils;
    private RxPermissions rxPermissions;
    private LoadingDialog mDialog;
    private String mobile = "";
    private String password = "";
    private String LAN_WIFI_SSID;
    private String LAN_WIFI_PASS;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissDiolog();
            switch (msg.what) {

                case 0:
                    SouthUtil.showToast(LoginActivity.this, getString(R.string.login_fail_3));

                    break;

                case 1: //操作成功
                    //记录登录成功的 医生账号和密码

                    SouthUtil.showToast(LoginActivity.this, getString(R.string.login_success));
                    Const.DoctorPhone = mobile;
                    Intent intent = null;
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                    int dele_data = (int) SPUtils.get(LoginActivity.this, Const.DELETE_KEY, -1);
                    Log.e("login_delete", dele_data + "");
                    if (dele_data == -1) {
                        intent = new Intent(LoginActivity.this, DeleteService.class);
                        startService(intent);
                    }

                    finish();
                    break;

                case -1://该用户不存在
                    SouthUtil.showToast(LoginActivity.this, getString(R.string.login_fail_1));
                    break;

                case -2:// 密码错误
                    SouthUtil.showToast(LoginActivity.this, getString(R.string.login_fail_2));
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
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        login(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEditTextData();
    }


    private void setEditTextData() {
        editLoginName.setText((String) (SPUtils.get(this, Const.DOC_MOBILE, "")));
        editLoginPass.setText((String) (SPUtils.get(this, Const.DOC_PASSWORD, "")));
    }

    private void initView() {
        tvTitle.setText(R.string.login);
        litepalUtils = new LitepalUtils(this);
        llLogin.requestFocus();
        rxPermissions = new RxPermissions(this);
        editLoginName.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @OnClick({R.id.iv_screen, R.id.edit_loginName, R.id.tv_loginpass, R.id.edit_loginPass, R.id.tv_register, R.id.bt_login, R.id.down_but, R.id.forgetPassword})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.edit_loginName:
                break;
            case R.id.edit_loginPass:
                break;
            case R.id.tv_register:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_login:
                startConnectLoginWifi();
                break;
            case R.id.down_but:
                pupopshow();
                break;

            case R.id.forgetPassword:
                intent = new Intent(LoginActivity.this, ModifyPassWordActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    //登录账户记录
    public void pupopshow() {
        View pupopview = LoginActivity.this.getLayoutInflater().inflate(R.layout.pupop_view, null);
        ListView listview = (ListView) pupopview.findViewById(R.id.lv_docshow);
        listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, litepalUtils.getDoctors()));
        final PopupWindow pupopwindow = new PopupWindow(pupopview, 300, LinearLayout.LayoutParams.WRAP_CONTENT, true);//设置对话框的大小
        pupopwindow.showAsDropDown(editLoginName, 0, 2);
        pupopwindow.setOutsideTouchable(true);//设置对话框外可点击
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editLoginName.setText(litepalUtils.getDoctors().get(i));
                editLoginPass.setText(litepalUtils.getdPassword(litepalUtils.getDoctors().get(i)));
                pupopwindow.dismiss();
            }
        });
    }

    private void startConnectLoginWifi() {
        mobile = editLoginName.getText().toString().trim();
        password = editLoginPass.getText().toString().trim();
        if (mobile.equals("") || password.equals("")) {
            SouthUtil.showToast(this, getString(R.string.login_info_empty));
            dismissDiolog();
            return;
        }
        SPUtils.put(LoginActivity.this, Const.DOC_MOBILE, mobile);
        SPUtils.put(LoginActivity.this, Const.DOC_PASSWORD, password);
        LAN_WIFI_SSID = (String) SPUtils.get(this, Const.LAN_WIFI_SSID_KEY, "");
        LAN_WIFI_PASS = (String) SPUtils.get(this, Const.LAN_WIFI_PASS_KEY, "");
        showDiolog(getString(R.string.wifiTestPing));
        WifiConnectManager.getInstance().ping(LoginActivity.this);

    }


    /**
     * 申请权限
     **/
    private void login(boolean firstCommit) {

        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        if (!firstCommit) {
                            loginInit();
                        }

                    } else {
                        // Oups permission denied
                        SouthUtil.showToast(LoginActivity.this, getString(R.string.login_permission));
                    }
                });
    }


    OkHttpClient client = new OkHttpClient();

    private void postLoginRequest(String userName, String password) {


        //建立请求表单，添加上传服务器数据
        RequestBody formBody = new FormBody.Builder()
                .add("mobile", userName)
                .add("password", password)
                .build();

        //发起请求
        Request request = new Request.Builder()
                .url(Const.URL_LOGIN)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG_1", "onFailure: ");
                mHandler.sendEmptyMessage(0);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                UserBean bean = null;
                try {
                    bean = new Gson().fromJson(msg, UserBean.class);
                    if (bean != null) {
                        mHandler.sendEmptyMessage(bean.getCode());
                    } else {
                        mHandler.sendEmptyMessage(0);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(0);
                }

            }
        });


    }

    private void showDiolog(String msg) {
        if (mDialog != null && mDialog.isShow()) {
            mDialog.setMessage(msg);
        } else {
            if (mDialog == null) {
                mDialog = new LoadingDialog(LoginActivity.this, true);
            }
            mDialog.setMessage(msg);
            mDialog.dialogShow();
        }
    }


    private void dismissDiolog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
//            LogUtils.e("TAG","软键盘执行 ");
            /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
            startConnectLoginWifi();

            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    //登录验证
    private void loginInit() {

        showDiolog(getString(R.string.login_in));
        boolean result = WifiConnectManager.getInstance().getWifiConnectResult();
        String ssid = WifiConnectManager.getInstance().getSysConnectedSSID().replace("\"","");
        SPUtils.put(this, Const.LAN_WIFI_SSID_KEY, ssid);
        SPUtils.put(this, Const.LAN_WIFI_PASS_KEY, "********");

        // false  表示没有切换wifi ,就不需要等待wifi稳定
        if (!result) {
            postLoginRequest(mobile, password);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    SystemClock.sleep(1000 * 2);
                    postLoginRequest(mobile, password);

                }
            }).start();

        }


    }


    @Override
    public void startWifiConnecting(String type) {
        showDiolog(getString(R.string.wifiProcessMsg));
    }

    @Override
    public void wifiConnectSuccess(String type) {
        if (type.equals(Const.WIFI_TYPE_LAN)) {
            login(false);
        }

    }

    @Override
    public void wifiConnectFalid(String type) {
        dismissDiolog();
        SouthUtil.showToast(this, getString(R.string.wifiFaildMSg));
    }


    @Override //指定的wifi没有打开
    public void wifiCycleSearch(String type, boolean isSSID, int count) {

        if (type.equals(Const.WIFI_TYPE_LAN)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isSSID) {
                        WifiConnectManager.getInstance().connectWithWpa(LAN_WIFI_SSID, LAN_WIFI_PASS);
                    } else {
                        if (count < 4) {
                            showDiolog(getString(R.string.wifiFailMsgprint));
                        } else {
                            dismissDiolog();
                        }
                    }
                }
            });
        }
    }

    @Override //wifi 的账号和密码为空
    public void wifiInputNameEmpty(String type) {

        dismissDiolog();
        SouthUtil.showToast(this, getString(R.string.login_fail_3) + "," + getString(R.string.wifi_LANname_empty));


    }


    //登录时，先验证网络能否上网，能上网就直接登录，如果不能上网，就开始连接指定的wifi，再次尝试登录
    @Override
    public void pingSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                login(false);
            }
        });

    }

    @Override
    public void pingFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WifiConnectManager.getInstance().connectWifiPing(LAN_WIFI_SSID, Const.WIFI_TYPE_LAN, LoginActivity.this);
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        dismissDiolog();
        WifiConnectManager.getInstance().stopThreadConnectWifi();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
