package comvoice.example.zhangbin.startimage.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.UserBean;
import comvoice.example.zhangbin.startimage.sp.SPUtils;
import comvoice.example.zhangbin.startimage.utils.Const;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;
import comvoice.example.zhangbin.startimage.wifiinfo.WifiConnectManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyPassWordActivity extends AppCompatActivity {

    @BindView(R.id.edit_modifyName)
    EditText editName;

    @BindView(R.id.edit_modifyPass)
    EditText editPass;

    @BindView(R.id.edit_modifyPassCover)
    EditText editPassCover;

    @BindView(R.id.edit_auth)
    EditText mEditAuthCode;


    @BindView(R.id.btn_auth)
    Button auth; //验证码


    @BindView(R.id.btn_modifySave)
    Button btnSave;

    @BindView(R.id.btn_modifyCancel)
    Button btnCancel;

    @BindView(R.id.tv_title)
    TextView title;

    @BindView(R.id.btn_left)
    Button btnLeft;

    private String mobile = "";
    private String password = "";
    private String passwordCover = "";
    private String mAuthCodeRandom = "";//随机生成的验证码
    private String mAuthCodeUser = ""; // 用户输入的验证码
    private int times = 60;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 0:
                    SouthUtil.showToast(ModifyPassWordActivity.this, getString(R.string.authModifyPassFaild));
                    break;
                case 5://密码修改成功
                    SouthUtil.showToast(ModifyPassWordActivity.this, getString(R.string.authModifyPass));
                    finish();
                    break;

                case -5: //密码修改失败
                    SouthUtil.showToast(ModifyPassWordActivity.this, getString(R.string.authModifyPassFaild));
                    break;

                case 6:
                    stopThread();
                    setBtnAuthText(getString(R.string.authCodebtn));
                    auth.setEnabled(true);
                    SouthUtil.showToast(ModifyPassWordActivity.this, getString(R.string.authCodeTelnoExist));
                    break;
                case 7: //验证码发送成功
                    setBtnAuthText(countms+"");
                    break;

                case -7: //验证码发送失败
                    SouthUtil.showToast(ModifyPassWordActivity.this, getString(R.string.authCodesendFailed));
                    break;

                case 9:// 显示验证码正在等待的时间
                    setBtnAuthText(countms+"");
                    break;

                case 10: //验证码等待 的时间结束
                    stopThread();
                    setBtnAuthText(getString(R.string.authCodebtn));
                    auth.setEnabled(true);
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
        setContentView(R.layout.activity_modify_pass_word);
        ButterKnife.bind(this);
        initView();

    }


    private void initView() {
        title.setText(R.string.authModifyTitle);
    }

    private void setBtnAuthText(String msg) {
        auth.setText(msg);
    }


    @OnClick({R.id.btn_auth,R.id.btn_modifySave,R.id.btn_modifyCancel})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.btn_auth:
                if (startModifyPassword()) {

                    getRandomAuthCode();
                    setBtnAuthText(times + "");
                    auth.setEnabled(false);
                    postAuthCodeRequest(mobile, mAuthCodeRandom);
                    setTextThread();
                }
                break;

            case R.id.btn_modifySave:

                if (startModifyPassword()) {
                    mAuthCodeUser = mEditAuthCode.getText().toString().trim();
                    mAuthCodeRandom =(String) SPUtils.get(this, Const.AUTHCODE_KEY, "");
                    if (mAuthCodeUser.equals(mAuthCodeRandom) && !mAuthCodeUser.equals("")) {
                        postModifyRequest(mobile, password);
                    } else {
                        SouthUtil.showToast(this, getString(R.string.authModifyError));
                    }
                }


                break;

            case R.id.btn_modifyCancel:
                stopThread();
                finish();
                break;

            default:
                break;
        }

    }


    private boolean startModifyPassword() {
        mobile = editName.getText().toString().trim();
        password = editPass.getText().toString().trim();
        passwordCover = editPassCover.getText().toString().trim();

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordCover)) {
            SouthUtil.showToast(this, getString(R.string.authCodeTelnoEmpty));
        } else {
            if (mobile.length() != 11) {
                SouthUtil.showToast(this, getString(R.string.register_mobile_error));
            } else {

                if (password.length() >= 6) {
                    if (password.equals(passwordCover)) {
                        //表示所有的条件都已经满足了。可以进行下一步了
                        return true;


                    } else {
                        SouthUtil.showToast(this, getString(R.string.register_msg_unLike));
                    }
                } else {
                    SouthUtil.showToast(this, getString(R.string.register_mobile_password_length));
                }
            }
        }

        return false;
    }



    OkHttpClient client = new OkHttpClient();

    //验证码
    private void postAuthCodeRequest(String mobile, String authCode) {
        Log.e("TAG_1", "postAuthCodeRequest: moblie ="+mobile+" , authCode = "+authCode );

        //建立请求表单，添加上传服务器数据
        RequestBody formBody = new FormBody.Builder()
                .add("mobile", mobile)
                .add("authCode", authCode)
                .build();

        //发起请求
        Request request = new Request.Builder()
                .url(Const.URL_AUTH)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG_1", "onFailure:1 ");
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String msg = response.body().string();

                    UserBean bean = new Gson().fromJson(msg, UserBean.class);
                    Log.e("TAG_1", "onResponse:  1 code = " + bean.getCode() + ", msg = " + bean.getMessage());
                    if (bean != null) {
                        mHandler.sendEmptyMessage(bean.getCode());
                    } else {
                        mHandler.sendEmptyMessage(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(0);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(0);
                }
            }
        });


    }


    //修改密码
    private void postModifyRequest(String moblie, String password) {


        //建立请求表单，添加上传服务器数据
        RequestBody formBody = new FormBody.Builder()
                .add("mobile", moblie)
                .add("password", password)
                .build();

        //发起请求
        Request request = new Request.Builder()
                .url(Const.URL_MODIFY)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG_1", "onFailure:2 ");
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String msg = response.body().string();

                    UserBean bean = new Gson().fromJson(msg, UserBean.class);
                    Log.e("TAG_1", "onResponse: 2 code  = " + bean.getCode() + ", msg = " + bean.getMessage());
                    if (bean != null) {
                        mHandler.sendEmptyMessage(bean.getCode());
                    } else {
                        mHandler.sendEmptyMessage(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(0);

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(0);
                }
            }
        });


    }


    private Thread mThread1 = null;
    private int countms = times;
    private void setTextThread() {
        mThread1=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        countms--;
                        if (countms >0) {
                            mHandler.sendEmptyMessage(9);
                        } else {
                            countms = times;
                            mHandler.sendEmptyMessage(10);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e("TAG_1", "run:子线程已经断开了 " );
                        return;
                    }
                }
            }
        });

        mThread1.start();



    }


    private void stopThread() {
        if (mThread1 != null) {
            mThread1.interrupt();
        }
    }











    //获取四位数的验证码
    private void getRandomAuthCode() {
        mAuthCodeRandom = (int) (Math.random() * 9000 + 1000) + "";
        SPUtils.put(this, Const.AUTHCODE_KEY, mAuthCodeRandom);
        Log.e("TAG_1", "验证码 getRandomAuthCode: mAuthCodeRandom = "+ mAuthCodeRandom);

    }


    @Override
    protected void onStop() {
        super.onStop();
        stopThread();
    }
}
