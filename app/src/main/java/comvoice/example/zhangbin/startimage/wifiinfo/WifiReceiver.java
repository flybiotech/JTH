package comvoice.example.zhangbin.startimage.wifiinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by dell on 2017/8/18.
 */

public class WifiReceiver extends BroadcastReceiver {
    private static String TAG = "TAG_WifiReceiver";
//    int mark = 0;
    private static WifiConnectinfo mWifiConnectinfo;
    @Override
    public void onReceive(Context context, Intent intent) {
//        i++;
        String action = intent.getAction();
        Bundle extras = intent.getExtras();

        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {//这个监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            Log.e("WIFI状态", "wifiState:" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    Log.e("WIFI状态", "wifiState:WIFI_STATE_DISABLED");
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    Log.e("WIFI状态", "wifiState:WIFI_STATE_DISABLING");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Log.e("WIFI状态", "wifiState:WIFI_STATE_ENABLED");
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    Log.e("WIFI状态", "wifiState:WIFI_STATE_ENABLING");
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    Log.e("WIFI状态", "wifiState:WIFI_STATE_UNKNOWN");
                    break;
                //
            }
        }
        // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
        // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，当然刚打开wifi肯定还没有连接到有效的无线
//        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
////            Logger.e("ffff");
//            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
//            String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);
//            if (null != parcelableExtra) {
//                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
//                NetworkInfo.State state = networkInfo.getState();
//                if (state == NetworkInfo.State.DISCONNECTED) {//连接失败
////                    Log.e(TAG, "onReceive: state  2   " + state + "  2");
//
//
//                } else if (state == NetworkInfo.State.CONNECTED) {
//
//                    String ssid = wifiInfo.getSSID();
//                    if (Const.wifiMark) {
//                        if (ssid.equals("\"" + ((String)SPUtils.get(context,WifiSettingActivity.SZB_WIFI_SSID_KEY,""))+ "\"")) {
//                            if (mWifiConnectinfo != null) {
//                                Const.wifiMark = false;
//                                mWifiConnectinfo.wifiConnectSuccess(1);
//
//                            }
//                        }
//
//                    }
//                }
//            }
//        }
    };




    public  static void setWifiConnectionListener(WifiConnectinfo wifiConnectionListener) {
        mWifiConnectinfo = wifiConnectionListener;

    }

        public interface WifiConnectinfo {

            void wifiConnectSuccess(int state);

            void wifiConnectFaile(int state);
        }

}
