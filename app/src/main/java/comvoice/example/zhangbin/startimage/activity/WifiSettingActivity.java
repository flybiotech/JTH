package comvoice.example.zhangbin.startimage.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.thanosfisherman.wifiutils.WifiUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.sp.SPUtils;
import comvoice.example.zhangbin.startimage.utils.Const;
import comvoice.example.zhangbin.startimage.utils.LoadingDialog;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;
import comvoice.example.zhangbin.startimage.wifiinfo.WifiConnectManager;
import comvoice.example.zhangbin.startimage.wifiinfo.WifiHotAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WifiSettingActivity extends BaseActivity implements View.OnClickListener, WifiConnectManager.WifiConnectListener {

    private Button clear, save, btn_right, btn_left, selectBtnLAN, selectBtnGetImageSoft;
    private EditText editName, editPass, editFilter;
    private TextView title_text;
    private WifiHotAdapter adapter;
    private String wifiName = "";
    private int wifiType = 0;// 判断是视珍宝的wifi type=0  还是局域网的wifi type=1
    ConnectivityManager connectManager;
    private LoadingDialog mDialog;

    URL url;//请求的url地址
    int state = -1;//网络请求返回值
    HttpURLConnection urlConnection;
    private String LAN_WIFI_SSID = "";
    private String LAN_WIFI_PASS = "";
    private String SZB_WIFI_SSID = "";
    private String SZB_WIFI_PASS = "";
    private List<ScanResult> results = new ArrayList<ScanResult>();
    String ssid = "";
    String pass = "";
    String msgStr = "";

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: //能上网
                    SouthUtil.showToast(WifiSettingActivity.this, getString(R.string.save_wifi_test_success));
                    dismissDiolog();
                    break;

                case 1://不能上网
                    SouthUtil.showToast(WifiSettingActivity.this, getString(R.string.save_wifi_test_fail));
                    dismissDiolog();
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
        setContentView(R.layout.activity_wifi_setting_layout);

        init();

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermission(Const.ASK_CALL_BLUE_PERMISSION)) {
            requestPermission(Const.ASK_CALL_BLUE_CODE, Const.ASK_CALL_BLUE_PERMISSION);
        } else {
            selectBtn(0);
        }
    }

    private void init() {
        clear = (Button) findViewById(R.id.btn_wifiSetting_clear01);
        save = (Button) findViewById(R.id.btn_wifiSetting_save01);
        selectBtnLAN = (Button) findViewById(R.id.btn_wifisetting_lan);
        selectBtnGetImageSoft = (Button) findViewById(R.id.btn_wifisetting_getImageSoft);
        selectBtnLAN.setOnClickListener(this);
        selectBtnGetImageSoft.setOnClickListener(this);
        btn_right = (Button) findViewById(R.id.btn_right);
        btn_left = (Button) findViewById(R.id.btn_left);//菜单项左边的按钮
        btn_left.setVisibility(View.VISIBLE);
        btn_left.setOnClickListener(this);
        editName = (EditText) findViewById(R.id.edit_wifiSetting_name01);
        editPass = (EditText) findViewById(R.id.edit_wifiSetting_pass01);
        editFilter = (EditText) findViewById(R.id.edit_filters);//过滤的WiFi
        title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText(R.string.wifiSet);
        btn_right.setVisibility(View.INVISIBLE);
        clear.setOnClickListener(this);
        save.setOnClickListener(this);
        WifiUtils.enableLog(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wifiSetting_clear01:// 清除相关信息
                editName.setText("");
                editPass.setText("");
                break;

            case R.id.btn_wifiSetting_save01://保存相关信息

                if (wifiType == 0) { //保存视珍宝WiFi的密码
                    getWifiNameAndPass(Const.SZB_WIFI_SSID_KEY, Const.SZB_WIFI_PASS_KEY, Const.SZB_WIFI_MODIFY_KEY, false);
                    SouthUtil.showToast(this, getString(R.string.wifiPass_save_success));
//                    LogUtils.e("TAG_W", "保存成功，视珍宝:wifi 名称 :" + editName.getText().toString().trim());
                } else {//保存局域网wifi的密码
                    getWifiNameAndPass(Const.LAN_WIFI_SSID_KEY, Const.LAN_WIFI_PASS_KEY, Const.LAN_WIFI_MODIFY_KEY, true);
                    //开始连接局域网WiFi
                    WifiConnectManager.getInstance().connectWifi(LAN_WIFI_SSID, LAN_WIFI_PASS, Const.WIFI_TYPE_LAN, this);

                }


                break;
            case R.id.btn_wifisetting_getImageSoft: //图像获取软件wifi设置

                selectBtn(0);
                refreshWifiList(this);

                break;
            case R.id.btn_wifisetting_lan://局域网wifi设置
                selectBtn(1);
                refreshWifiList(this);

                break;
            case R.id.btn_left:
                finish();
                break;
            default:
                break;


        }
    }


    private void initConnectivityManager() {
        if (connectManager == null) {
            connectManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        }
    }



    //如果输入的账号和密码都没有变，则不需要修改保存的内容，如果有变化，就需要修改保存的内容了
    private void getWifiNameAndPass(String key_Name, String key_Pass, String key_modify, boolean isLAN) {
        String oldName = (String) SPUtils.get(this, key_Name, "");
        String oldPass = (String) SPUtils.get(this, key_Pass, "");
        LAN_WIFI_SSID = editName.getText().toString().trim();
        LAN_WIFI_PASS = editPass.getText().toString().trim();
        if (!oldName.equals(LAN_WIFI_SSID) || !oldPass.equals(LAN_WIFI_PASS)) {
            SPUtils.put(this, key_Name, editName.getText().toString().trim());
            SPUtils.put(this, key_Pass, editPass.getText().toString().trim());
            SPUtils.put(this, key_modify, true);
        } else {
            SPUtils.put(this, key_modify, false);
        }


    }


    private void showDiolog(String msg) {
        if (mDialog != null && mDialog.isShow()) {
            mDialog.setMessage(msg);
        } else {
            if (mDialog == null) {
                mDialog = new LoadingDialog(WifiSettingActivity.this, true);
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


    //测试 是否能上网
    public synchronized void ping() {
        showDiolog(getString(R.string.wifi_test_net));
        initConnectivityManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(600);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        try {
                            NetworkCapabilities networkCapabilities = connectManager.getNetworkCapabilities(connectManager.getActiveNetwork());
                            /**
                             * NOT_METERED&INTERNET&NOT_RESTRICTED&TRUSTED&NOT_VPN
                             *
                             */
                            Log.i("TAG_Avalible", "NetworkCapalbilities:" + networkCapabilities.toString());

                            boolean valudated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                            if (valudated) {
                                mHandler.sendEmptyMessage(0); //链接成功
                            } else {
                                mHandler.sendEmptyMessage(1);
                            }

                        } catch (Exception e) {
                            mHandler.sendEmptyMessage(1);
                        }


                    } else { //6.0以下

                        url = new URL("https://www.baidu.com/");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setConnectTimeout(5000);//设置连接超时时间
                        state = urlConnection.getResponseCode();
                        Log.e("TAG_PING", " ping 值 run:  state = " + state);
                        if (state == 302) {
                            mHandler.sendEmptyMessage(0); //链接成功

                        }

                    }

                } catch (Exception e) {
                    Log.e("TAG_EROOR", "run: 错误I " + e.getMessage());
                    mHandler.sendEmptyMessage(1);
                }
            }
        }).start();


    }


    public void selectBtn(int mState) {
        String wifiName = "";
        String wifiPass = "";
        selectBtnLAN.setSelected(false);
        selectBtnGetImageSoft.setSelected(false);
        switch (mState) {
            case 0: //主机
                selectBtnGetImageSoft.setSelected(true);
                wifiType = 0;
//                editFilter.setText((String) SPUtils.get(this, SZB_WIFI_FILTER_KEY, ""));
                wifiName = (String) SPUtils.get(this, Const.SZB_WIFI_SSID_KEY, "");
                wifiPass = (String) SPUtils.get(this, Const.SZB_WIFI_PASS_KEY, "");


                break;
            case 1://局域网
                selectBtnLAN.setSelected(true);
                wifiType = 1;
//                editFilter.setText((String) SPUtils.get(this, LAN_WIFI_FILTER_KEY, ""));
                wifiName = (String) SPUtils.get(this, Const.LAN_WIFI_SSID_KEY, "");
                wifiPass = (String) SPUtils.get(this, Const.LAN_WIFI_PASS_KEY, "");
                break;
            default:
                break;
        }

        editName.setText(wifiName);
        editPass.setText(wifiPass);

    }

    @Override
    public void openGpsPermission() {
        super.openGpsPermission();
        refreshWifiList(this);

    }


    //给listview 列表填充数据 ,将搜索到的wifi显示在listview上
    private void refreshWifiList(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
        List<ScanResult> scan = wifiManager.getScanResults();
        //如果没有搜索到wifi，就开启GPS
        if (scan.size() == 0) {
            openGPSSettings();
        } else {
            if (results == null) {
                results = new ArrayList<ScanResult>();
            }
            results.clear();

            Observable.from(scan)
                    .filter(new Func1<ScanResult, Boolean>() {
                        @Override
                        public Boolean call(ScanResult scanResult) {

                            return scanResult.SSID.contains(editFilter.getText().toString().trim());
                        }
                    }).map(new Func1<ScanResult, ScanResult>() {
                @Override
                public ScanResult call(ScanResult scanResult) {
                    return scanResult;
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ScanResult>() {
                        @Override
                        public void onCompleted() {
                            if (null == adapter) {
                                adapter = new WifiHotAdapter(results, WifiSettingActivity.this);
                            } else {
                                adapter.refreshData(results);
                            }
                            lv_add(results);

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(ScanResult scanResult) {
                            results.add(scanResult);
                        }
                    });


        }
    }


    private void lv_add(final List<ScanResult> results) {
        LinearLayout linearLayoutMain = new LinearLayout(this);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(this);//this为获取当前的上下文
        listView.setFadingEdgeLength(0);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        listView.setAdapter(adapter);
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.setting_wifi_name)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                wifiName = results.get(arg2).SSID;
                editName.setText(wifiName);
                dialog.cancel();
            }
        });
    }


    //跳到GPS设置界面
    private int GPS_REQUEST_CODE = 10;

    private boolean checkGPSIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    /**
     * 跳转GPS设置
     */
    private void openGPSSettings() {
        if (checkGPSIsOpen()) {
//            initLocation(); //自己写的定位方法
        } else {
            //没有打开则弹出对话框
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.setting_gps))
                    .setMessage(getString(R.string.setting_gps_list))
                    // 拒绝, 退出应用
                    .setNegativeButton(R.string.button_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })

                    .setPositiveButton(R.string.setting_wifi_title,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //跳转GPS设置界面
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, GPS_REQUEST_CODE);
                                }
                            })

                    .setCancelable(false)
                    .show();
        }
    }


    @Override
    public void startWifiConnecting(String type) {
        showDiolog(getString(R.string.wifiProcessMsg));
    }

    @Override
    public void wifiConnectSuccess(String type) {
        if (type.equals(Const.WIFI_TYPE_LAN)) {
            ping();
        } else {
            dismissDiolog();
        }

    }

    @Override
    public void wifiConnectFalid(String type) {
        dismissDiolog();
        SouthUtil.showToast(this, getString(R.string.wifiFaildMSg));
    }

    @Override
    public void wifiCycleSearch(String type, boolean isSSID, int count) {
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


        dismissDiolog();
        if (type.equals(Const.WIFI_TYPE_LAN)) {
            SouthUtil.showToast(WifiSettingActivity.this, getString(R.string.wifi_LANname_empty));
        } else if (type.equals(Const.WIFI_TYPE_SZB)) {
            SouthUtil.showToast(WifiSettingActivity.this, getString(R.string.wifi_SZBname_empty));
        }
    }

    @Override
    protected void onStop () {
        super.onStop();
        dismissDiolog();
        WifiConnectManager.getInstance().stopThreadConnectWifi();
    }


}
