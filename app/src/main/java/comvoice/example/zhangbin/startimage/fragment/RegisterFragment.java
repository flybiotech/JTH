package comvoice.example.zhangbin.startimage.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.activity.ScanningActivity;
import comvoice.example.zhangbin.startimage.activity.ShowActivity;
import comvoice.example.zhangbin.startimage.adapter.MessageAdapter;
import comvoice.example.zhangbin.startimage.model.ListMessage;
import comvoice.example.zhangbin.startimage.service.UpLoadService;
import comvoice.example.zhangbin.startimage.sp.SPUtils;
import comvoice.example.zhangbin.startimage.utils.Const;
import comvoice.example.zhangbin.startimage.utils.DialogSelectUtils;
import comvoice.example.zhangbin.startimage.utils.FileUtils;
import comvoice.example.zhangbin.startimage.utils.InstallApk;
import comvoice.example.zhangbin.startimage.utils.LoadingDialog;
import comvoice.example.zhangbin.startimage.utils.NetWorkUtils;
import comvoice.example.zhangbin.startimage.utils.SearchMessage;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;
import comvoice.example.zhangbin.startimage.wifiinfo.WifiConnectManager;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * RegisterFragment.java
 */
public class RegisterFragment extends Fragment implements AdapterView.OnItemClickListener, UpLoadService.UpLoadFileProcess,
        WifiConnectManager.WifiConnectListener, FileUtils.FileCopyAndDelListener {
    @BindView(R.id.edittext)
    EditText edittext;
    @BindView(R.id.imageview)
    ImageView imageview;
    @BindView(R.id.ll)
    LinearLayout ll;
    Unbinder unbinder;
    //    @BindView(R.id.lv_show)
    ListView lvShow;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.iv_screen)
    ImageView ivScreen;
    private LoadingDialog loadingDialog;
    private List<ListMessage> userList;//展示的数据源
    private SearchMessage searchMessage;
    private MessageAdapter messageAdapter;
    private FileUtils fileUtils;
    private NetWorkUtils netWorkUtils;
    private List<String> show_list;
    private DialogSelectUtils dialogSelectUtils;
    private Thread thread = null;
    private LoadingDialog mDialog;
    private InstallApk installApk;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissDiolog();
            if (msg.what == 1) {
                initData();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView(view);
        initTextClick();
        initItemClick();

        return view;
    }

    private void initItemClick() {
        lvShow.setOnItemClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        initGetData();
        fileUtils.startCopyFileAndDel();
        UpLoadService.setUpLoadFileProcessListener(this);
    }

    private void initGetData() {
        showDiolog(getString(R.string.loadingMEssage));
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                userList = fileUtils.getList();
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void initView(View view) {
        lvShow = view.findViewById(R.id.lv_show);
        loadingDialog = new LoadingDialog(getContext());
        dialogSelectUtils = new DialogSelectUtils(getContext(), this);
        searchMessage = new SearchMessage();
        fileUtils = new FileUtils(getContext(), this);
        mDialog = new LoadingDialog(getContext());
        show_list = new ArrayList<>();
        show_list.add(getString(R.string.fragment_getImage));
        show_list.add(getString(R.string.fragment_updataImage));
//        UpLoadService.setUpLoadFileProcessListener(this);
        installApk = new InstallApk(getActivity());
        netWorkUtils = new NetWorkUtils(getActivity());
    }

    private void initData() {
        if (userList != null) {
            if (tvEmpty != null) {
                tvEmpty.setText("");
            }
            messageAdapter = new MessageAdapter(getContext(), userList);
            lvShow.setAdapter(messageAdapter);
            messageAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initTextClick() {
        //EditText添加监听
        edittext.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }//文本改变之前执行

            @Override
            //文本改变的时候执行
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //如果长度为0
                if (s.length() == 0) {
                    //隐藏“删除”图片
                    imageview.setVisibility(View.GONE);
                } else {//长度不为0
                    //显示“删除图片”
                    imageview.setVisibility(View.VISIBLE);
                }
            }

            public void afterTextChanged(Editable s) {

                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String msg = null;
                        if (edittext != null) {
                            msg = edittext.getText().toString().trim();
                        } else {
                            msg = "";
                        }
                        if (msg != null && !msg.equals("")) {//当输入查询条件时，

                            userList = searchMessage.vagueSelect(msg);
                            Log.e("userList", "111111111111111111111" + msg + ",," + userList.size());
                        }
                        handler.sendMessage(message);
                    }
                });
                thread.start();
            }//文本改变之后执行
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                String msg = data.getStringExtra("text");
                Intent intent = new Intent(getContext(), ShowActivity.class);
                intent.putExtra("msg", msg);
                startActivity(intent);
            }else if(requestCode == 2){
                installApk.initInstallAPK();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        if(!fileUtils.isCopy(new File(Const.originalPath))){
        dialogSelectUtils.showDialog(show_list, userList.get(i).getScreenId());
//        }
    }

    @OnClick({R.id.imageview, R.id.iv_screen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageview:
                edittext.setText("");
                initGetData();
                break;
            case R.id.iv_screen:
                if (!fileUtils.isCopy(new File(Const.originalPath))) {
                    startActivityForResult(new Intent(getContext(), ScanningActivity.class), 1);
                } else {
                    SouthUtil.showToast(getActivity(), getString(R.string.copyFile));
                }
                break;
            default:
                break;
        }
    }


    private void showDiolog(String msg) {
        if (mDialog != null && mDialog.isShow()) {
            mDialog.setMessage(msg);
        } else {
            if (mDialog == null) {
                mDialog = new LoadingDialog(getContext(), true);
            }
            mDialog.setMessage(msg);
            mDialog.dialogShow();
        }
    }

    private void dismissDiolog() {
        if (mDialog != null && mDialog.isShow()) {
            mDialog.dismiss();
        }

    }
    //根据android 版本选择不同的安装方式
    private void installApk(Context context, String fileApk) {

        if (fileApk != null) {
            File file = new File(fileApk);
            if (Build.VERSION.SDK_INT >= 24) {//android 7.0以上
                installApk.initInstallAPK();
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//Android 8.0以上，增加了一个未知来源安装的权限
                if(!getActivity().getPackageManager().canRequestPackageInstalls()){
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
    //开始上传
    @Override
    public void getUpLoadStart() {
//        showDiolog("已上传:" +  "0%");
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                showDiolog(getString(R.string.fragment_startUpdate));
//            }
//        });

    }

    @Override
    public void getUpLoadFileProcessPrecent(double percent) {
        Log.e("percent", percent + "%");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (percent >= 100.0) {
                    showDiolog("已上传:" + (int) percent + " %");
                    dismissDiolog();
                    initGetData();
                } else {
                    showDiolog("已上传:" + (int) percent + " %");

                }
            }
        });
    }

    //上传成功
    @Override
    public void getUpLoadSuccess() {

    }

    //上传失败
    @Override
    public void getUpLoadFaild() {
        dismissDiolog();
        SouthUtil.showToast(getActivity(), getString(R.string.upLoadFaild));
    }


    @Override
    public void startWifiConnecting(String type) {
        showDiolog(getString(R.string.wifiProcessMsg));
    }

    @Override
    public void wifiConnectSuccess(String type) {

        if (type.equals(Const.WIFI_TYPE_SZB)) {
            dismissDiolog();
            boolean isHave = installApk.isInstall();
            if(!isHave){
                installApk(getActivity(),Const.FLY_SZB_apk);//打印服务插件本地路径);
            }
        } else if (type.equals(Const.WIFI_TYPE_LAN)) {
            //上传

            if (fileUtils.isCopy(new File(Const.fromPath + Const.SPscreenId + "/"))) {
//                SouthUtil.showToast(getActivity(), "dddddddddddddddddddd");
                netWorkUtils.getUrl();
            } else {
                dismissDiolog();
                SouthUtil.showToast(getActivity(), getActivity().getString(R.string.file_no_exist));
            }
        }
    }

    @Override
    public void wifiConnectFalid(String type) {
        dismissDiolog();
        SouthUtil.showToast(getActivity(), getString(R.string.wifiFaildMSg));
    }

    String ssid = "";
    String pass = "";
    String msgStr = "";

    @Override
    public void wifiCycleSearch(String type, boolean isSSID, int count) {
       if (getActivity()==null)
           return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (type.equals(Const.WIFI_TYPE_SZB)) {
                    ssid = (String) SPUtils.get(getActivity(), Const.SZB_WIFI_SSID_KEY, "");
                    ;
                    pass = (String) SPUtils.get(getActivity(), Const.SZB_WIFI_PASS_KEY, "");
                    msgStr = getString(R.string.wifiSZBFailMsg);
                } else if (type.equals(Const.WIFI_TYPE_LAN)) {
                    ssid = (String) SPUtils.get(getActivity(), Const.LAN_WIFI_SSID_KEY, "");
                    pass = (String) SPUtils.get(getActivity(), Const.LAN_WIFI_PASS_KEY, "");
                    msgStr = getString(R.string.wifiLANFailMsg);
                }

                if (isSSID) {
                    WifiConnectManager.getInstance().connectWithWpa(ssid, pass);
                } else {
                    if (count < 2) {
                        showDiolog(msgStr);
                    }
                }

            }
        });


    }

    @Override
    public void wifiInputNameEmpty(String type) {
        dismissDiolog();
        if (type.equals(Const.WIFI_TYPE_LAN)) {
            SouthUtil.showToast(getActivity(), getString(R.string.wifi_LANname_empty));
        } else if (type.equals(Const.WIFI_TYPE_SZB)) {
            SouthUtil.showToast(getActivity(), getString(R.string.wifi_SZBname_empty));
        }
    }


    @Override
    public void fileCopySuccess() {

    }

    @Override
    public void fileCopyFailed(String msg) {
        dismissDiolog();
//        SouthUtil.showToast(getActivity(), msg);

    }

    @Override
    public void fileDelSuccess() {

    }

    @Override
    public void fileDelFailed() {

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("TAG_1", "onStop: FRAGMENT " );
        WifiConnectManager.getInstance().stopThreadConnectWifi();
    }


}
