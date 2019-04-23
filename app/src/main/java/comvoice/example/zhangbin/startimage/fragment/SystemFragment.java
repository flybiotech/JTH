package comvoice.example.zhangbin.startimage.fragment;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.activity.DeleteSettingActivity;
import comvoice.example.zhangbin.startimage.activity.FTPSettingActivity;
import comvoice.example.zhangbin.startimage.activity.UploadActivity;
import comvoice.example.zhangbin.startimage.activity.WifiSettingActivity;
import comvoice.example.zhangbin.startimage.sp.SPUtils;
import comvoice.example.zhangbin.startimage.utils.Const;
import comvoice.example.zhangbin.startimage.utils.DeleteUtils;
import comvoice.example.zhangbin.startimage.utils.LoadingDialog;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;
import comvoice.example.zhangbin.startimage.utils.UpdateManager;
import comvoice.example.zhangbin.startimage.utils.WriteExcelUtils;
import comvoice.example.zhangbin.startimage.wifiinfo.WifiConnectManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SystemFragment extends Fragment implements AdapterView.OnItemClickListener,WifiConnectManager.WifiConnectListener{

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_screen)
    ImageView ivScreen;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.list_setting)
    ListView listSetting;
    Unbinder unbinder;
    private List<String>stringList;
    private ListAdapter adapter;
    private WriteExcelUtils writeExcelUtils;
    private List<String>dialogList;
    private DeleteUtils deleteUtils;
    private LoadingDialog loadingDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        initItemClick();
        return view;
    }

    private void initItemClick() {
        listSetting.setOnItemClickListener(this);
    }

    private void initView() {
        tvTitle.setText(getText(R.string.setting_title));
        writeExcelUtils=new WriteExcelUtils(getContext());
        stringList=new ArrayList<>();
//        stringList.add(getString(R.string.importExcel));
        stringList.add(getString(R.string.wifiSet));
        stringList.add(getString(R.string.setting_delete));
        stringList.add(getString(R.string.upload_app));;
//        stringList.add(getString(R.string.ftpsetting));
//        stringList.add(getString(R.string.upload));
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,stringList);
        listSetting.setAdapter( adapter);
        wifiConnectManager = WifiConnectManager.getInstance();
        updateManager = new UpdateManager(getContext());
        deleteUtils = new DeleteUtils(getContext());
        loadingDialog = new LoadingDialog(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){
//            case 0: //导出Excel
//                writeExcelUtils.initExcel();
//                break;
            case 0://WiFi设置
                startActivity(new Intent(getActivity(), WifiSettingActivity.class));
                break;

            case 1:
//                startActivity(new Intent(getActivity(), DeleteSettingActivity.class));
                dialogList = new ArrayList<>();
                dialogList.add(getString(R.string.forover_save));
                dialogList.add(getString(R.string.twoweek_delete));
                deleteUtils.advancedDialog(dialogList);
                break;
            case 2:
                connectLan();
                break;
//            case 2:
//                startActivity(new Intent(getContext(), FTPSettingActivity.class));
//                break;
//            case 1://数据上传
//               startActivity(new Intent(getContext(), UploadActivity.class));
//                break;
        }
    }
    private String LAN_WIFI_SSID = "";
    private String LAN_WIFI_PASS = "";
    private WifiConnectManager wifiConnectManager;
    private UpdateManager updateManager;
    //开始连接局域网WIFI
    private void connectLan(){
        LAN_WIFI_SSID = (String) SPUtils.get(getContext(),Const.LAN_WIFI_SSID_KEY,"");
        LAN_WIFI_PASS = (String) SPUtils.get(getContext(),Const.LAN_WIFI_PASS_KEY,"");
        wifiConnectManager.connectWifi(LAN_WIFI_SSID,LAN_WIFI_PASS,Const.WIFI_TYPE_LAN,this);
    }

    private OkHttpClient okHttpClient;
    private void initDownLoad(){
        okHttpClient=new OkHttpClient();
        Request request=new Request.Builder()
                .url("http://flybiotech.w231.mc-test.com/jth_user/version.txt")
                .build();
        okHttpClient.newCall(request).enqueue(new MyCient());
    }
    private class MyCient implements Callback {

        @Override
        public void onFailure(Call call, IOException e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "访问失败", Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            final String res=response.body().string();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateManager.initJson(res);
                }
            });
        }
    }
    @Override
    public void startWifiConnecting(String type) {
        showDialog(getString(R.string.wifiProcessMsgLAN));
    }

    @Override
    public void wifiConnectSuccess(String type) {
        dismissDialog();
        if (type.equals(Const.WIFI_TYPE_LAN)) {
            Log.e("system",11+"");
            initDownLoad();
        }
    }

    @Override
    public void wifiConnectFalid(String type) {
        dismissDialog();
        if (type.equals(Const.WIFI_TYPE_LAN))
            SouthUtil.showToast(getContext(), getString(R.string.wifiFaildMSg));
    }

    @Override
    public void wifiCycleSearch(String type, boolean isSSID, int count) {
        dismissDialog();
        if (type.equals(Const.WIFI_TYPE_LAN) ) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isSSID)
                        wifiConnectManager.connectWithWpa(LAN_WIFI_SSID,LAN_WIFI_PASS);
                }
            });
        }
    }

    @Override
    public void wifiInputNameEmpty(String type) {
        dismissDialog();
        if (type.equals(Const.WIFI_TYPE_LAN)) {
            SouthUtil.showToast(getContext(), getString(R.string.wifiFailLANMsg));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    private void showDialog(String msg){
        if(loadingDialog != null){
            loadingDialog.setMessage(msg);
            loadingDialog.dialogShow();
        }
    }
    private void dismissDialog(){
        if(loadingDialog != null && loadingDialog.isShow()){
            loadingDialog.dismiss();
        }
    }
}
