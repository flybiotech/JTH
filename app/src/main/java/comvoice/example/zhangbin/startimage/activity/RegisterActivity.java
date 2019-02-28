package comvoice.example.zhangbin.startimage.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.UserBean;
import comvoice.example.zhangbin.startimage.utils.Const;
import comvoice.example.zhangbin.startimage.utils.LitepalUtils;
import comvoice.example.zhangbin.startimage.utils.LoadingDialog;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;
import comvoice.example.zhangbin.startimage.wifiinfo.WifiConnectManager;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class RegisterActivity extends AppCompatActivity implements WifiConnectManager.WifiConnectListener.WifiPingListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_screen)
    ImageView ivScreen;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.edit_registerName)
    EditText editRegisterName;
    @BindView(R.id.edit_registerPass)
    EditText editRegisterPass;
    @BindView(R.id.edit_registerPassCover)
    EditText editRegisterPassCover;
    @BindView(R.id.btn_registerSure)
    Button btnRegisterSure;
    @BindView(R.id.btn_registerBack)
    Button btnRegisterBack;
    //    @BindView(R.id.edit_hospital)
//    EditText editHospital;
    private LitepalUtils litepalUtils;
    private LoadingDialog mDialog;
    private String mobile = "";
    private String password = "";
    private String confirmPassword = "";
    private String hospital = "";
    private UserBean userBean;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissDiolog();
            switch (msg.what) {


                case -1: //表示该手机号码已经被核销或者没有在微信回台注册
                    /**
                     * {"message":"该手机号不是医生用户","code":-1}
                     * {"message":"该码已使用，于2018-06-07 17:49:26被核销","code":-1}
                     */
                    String str = (String) msg.obj;
                    if (str.contains(getString(R.string.register_verify_mobile_1))) {
                        SouthUtil.showToast(RegisterActivity.this, getString(R.string.register_verify_mobile_1));

                    } else if (str.contains(getString(R.string.register_verify_mobile_2))) { //该号码已使用.说明该该手机号码在微信上已经注册了，是医生
//                        startRegister();
                        postRegisterRequest(mobile, password);
                    } else {
                        SouthUtil.showToast(RegisterActivity.this, getString(R.string.register_failed_4));
                    }

                    break;

                case 0:
                    SouthUtil.showToast(RegisterActivity.this, getString(R.string.register_failed_4));
                    break;

                case 3: //注册成功
                    litepalUtils.initSave(mobile, password, hospital);
                    SouthUtil.showToast(RegisterActivity.this, getString(R.string.register_success));
                    finish();
                    break;

                case -3://该用户已注册
                    SouthUtil.showToast(RegisterActivity.this, getString(R.string.register_failed_3));
                    break;

                case -4:// 注册失败
                    SouthUtil.showToast(RegisterActivity.this, getString(R.string.register_failed_4));
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initView();
//        initHad();
    }

    private void initView() {
        tvTitle.setText(getString(R.string.register));
        litepalUtils = new LitepalUtils(this);
        editRegisterName.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @OnClick({R.id.edit_registerName, R.id.edit_registerPass, R.id.edit_registerPassCover, R.id.btn_registerSure, R.id.btn_registerBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_registerName:
                break;
            case R.id.edit_registerPass:
                break;
            case R.id.edit_registerPassCover:
                break;
            case R.id.btn_registerSure:
                mobile = editRegisterName.getText().toString().trim();
//                verifyMobile(mobile);
                startRegister();
//                showDiolog(getString(R.string.wifiTestPing));
//                WifiConnectManager.getInstance().ping(RegisterActivity.this);
                break;
            case R.id.btn_registerBack:
                finish();
                break;
        }
    }


    /**
     * 保存账户信息 ,开始注册 ，确保是手机号码是医生，然后开始注册
     */
    private void startRegister() {
        getEditTextData();

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            SouthUtil.showToast(this, getString(R.string.register_msg_empty));
        } else {
            if (mobile.length() != 11) {
                SouthUtil.showToast(this, getString(R.string.register_mobile_error));
            } else {

                if (password.length() >= 6) {
                    if (password.equals(confirmPassword)) {
                        showDiolog(getString(R.string.wifiTestPing));
                        WifiConnectManager.getInstance().ping(RegisterActivity.this);

                    } else {
                        SouthUtil.showToast(this, getString(R.string.register_msg_unLike));
                    }
                } else {
                    SouthUtil.showToast(this, getString(R.string.register_mobile_password_length));
                }
            }
        }
    }

    private void getEditTextData() {
        mobile = editRegisterName.getText().toString().trim();
        password = editRegisterPass.getText().toString().trim();
        confirmPassword = editRegisterPassCover.getText().toString().trim();
//        hospital = editHospital.getText().toString().trim();
    }


    OkHttpClient client = new OkHttpClient();

    private void verifyMobile(String moblie) {
        showDiolog(getString(R.string.register_in));
        /**
         * {"message":"该手机号不是医生用户","code":-1}
         * {"message":"该码已使用，于2018-06-07 17:49:26被核销","code":-1}
         */
        JSONObject jsonObject = new JSONObject();
        String verifyJson = "";
        try {
//                    jsonObject.put("id", 1);
            //1 正确的
            jsonObject.put("screeningId", Const.SCREENINGID);
            // 正确的号码 17621140126
            jsonObject.put("mobile", moblie);
            // 101 正确的
            jsonObject.put("hpvNumber", Const.HPVNUMBER);
            verifyJson = jsonObject.toString();
//            Log.e("TAG_", "json数据:  " + verifyJson);
        } catch (JSONException e) {
            e.printStackTrace();
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
                Log.e("TAG_2", "onFailure: ");
                mHandler.sendEmptyMessage(0);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                try {
                    UserBean bean = new Gson().fromJson(msg, UserBean.class);
                    if (bean != null) {
                        //一般情况下都是 -1
                        Message msgobj = new Message();
                        msgobj.obj = bean.getMessage();
                        msgobj.what = bean.getCode();
                        mHandler.sendMessage(msgobj);
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


    private void postRegisterRequest(String moblie, String password) {

        showDiolog(getString(R.string.register_in));

        //建立请求表单，添加上传服务器数据
        RequestBody formBody = new FormBody.Builder()
                .add("mobile", moblie)
                .add("password", password)
                .build();

        //发起请求
        Request request = new Request.Builder()
                .url(Const.URL_REGISTER)
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

                UserBean bean = new Gson().fromJson(msg, UserBean.class);
                Log.e("TAG_1", "onResponse: code  " + bean.getCode() + ", msg = " + bean.getMessage());
                if (bean != null) {
                    mHandler.sendEmptyMessage(bean.getCode());
                } else {
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
                mDialog = new LoadingDialog(RegisterActivity.this, true);
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
                inputMethodManager.hideSoftInputFromWindow(RegisterActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
            mobile = editRegisterName.getText().toString().trim();
            startRegister();
//            verifyMobile(mobile);
//            showDiolog(getString(R.string.wifiTestPing));
//            WifiConnectManager.getInstance().ping(RegisterActivity.this);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public void pingSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                verifyMobile(mobile);
            }
        });

    }

    @Override
    public void pingFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissDiolog();

                SouthUtil.showToast(RegisterActivity.this, getString(R.string.register_failed_4)+","+getString(R.string.wifiFailMsgprint));
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
