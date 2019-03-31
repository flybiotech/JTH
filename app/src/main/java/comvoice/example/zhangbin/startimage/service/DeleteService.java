package comvoice.example.zhangbin.startimage.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.litepal.LitePal;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import comvoice.example.zhangbin.startimage.model.User;
import comvoice.example.zhangbin.startimage.utils.Const;

public class DeleteService extends Service {
    private List<User>userList;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new uploadTAsk().execute("");
        return super.onStartCommand(intent, flags, startId);
    }
    private class uploadTAsk extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            selectLitepal();
            stopSelf();

            return null;
        }
    }

    //查询本地数据库，是否有患者的登记日期到达两周

    private void selectLitepal(){
        userList = LitePal.findAll(User.class);
        long nowdata = getDate();
        if(userList.size() > 0){
            for(int i = 0;i<userList.size();i++){
                if(null != userList.get(i).getData()){
                    long redata = Long.parseLong(userList.get(i).getData());
//                    long redata = 1;
                    Log.e("DELETESERVICE",nowdata+",,,"+redata);
                    if(nowdata - redata > 100){
                        userList.get(i).delete();
                    }
                }

            }
        }
    }

    /**
     * 得到保存患者时的时间，毫秒
     */
    private long getDate(){
//        SimpleDateFormat formatter = null; //转化时间
//        formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss", Locale.getDefault());//系统当前时间
//        Date curDate =  new Date();
//        String date=formatter.format(curDate);
//        return date;
        long time = System.currentTimeMillis();
        return time;
    }
}
