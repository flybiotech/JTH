package comvoice.example.zhangbin.startimage.application;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

/**
 * Created by zhangbin on 2018/5/28.
 */

public class MyApplication extends LitePalApplication {

    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;
        LitePal.initialize(this);
        CrashHandler.getInstance().init(this);
        CrashReport.initCrashReport(getApplicationContext(), "d59652b980", true);
    }


    public static Context getInstance() {
        return myApplication.getApplicationContext();
    }




}
