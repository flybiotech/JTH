package comvoice.example.zhangbin.startimage.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import comvoice.example.zhangbin.startimage.R;

/**
 * Created by zhangbin on 2018/6/28.
 */

public class InstallApk {
    private Context context;
    public InstallApk(Context context){
        this.context=context;
    }
    /**
     * 判断是否安装图像获取软件
     */
    public boolean isInstall(){
        PackageInfo packageInfo=null;
        try {
            packageInfo=context.getPackageManager().getPackageInfo(Const.page,0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("apk..",e.getMessage().toString());
            packageInfo=null;
        }
            if(packageInfo!=null){
                Intent intent=context.getPackageManager().getLaunchIntentForPackage(Const.page);
                context.startActivity(intent);
                return true;
            }else {
//                String path = Environment.getExternalStorageDirectory() + "/FLY_SZB_V1.1.apk";//打印服务插件本地路径
                try {
                    File file = new File(Const.FLY_SZB_apk);
                    if (!file.exists()) {
                        copyAPK2SD(Const.FLY_SZB_apk);//将项目中的服务插件复制到本地路径下
                    }
//                    installApk(context,path);
                    return false;
                } catch (IOException e) {
//                    e.printStackTrace();
                    Log.e("apk11",e.getMessage().toString());
                }
            }
            return false;
    }

    /**
     * 图像获取软件安装的通用方法
     */
    public void initInstallAPK(){
        Intent install = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri =
                FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(Environment.getExternalStorageDirectory() + "/com.stub.StubApp.apk"));
//                content://com.qcam.fileprovider/external_files/Download/update.apk
        Log.e(TAG, "android 7.0 : apkUri " + apkUri);
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(install);
    }
    /**
     * 拷贝assets文件夹的APK插件到SD
     *
     * @param strOutFileName
     * @throws IOException
     */
    private void copyAPK2SD(String strOutFileName) throws IOException {
       createDipPath(strOutFileName);
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            myInput = context.getAssets().open("com.stub.StubApp.apk");
            myOutput = new FileOutputStream(strOutFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (myOutput != null) {
                myOutput.flush();
                myOutput.close();
            }
            if (myInput != null) {
                myInput.close();
            }
        }
    }

    /**
     * 根据文件路径 递归创建文件
     *
     * @param file
     */
    public static void createDipPath(String file) {
        String parentFile = file.substring(0, file.lastIndexOf("/"));
        File file1 = new File(file);
        File parent = new File(parentFile);
        if (!file1.exists()) {
            parent.mkdirs();
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String TAG = "TAG_FragPrinter";

}
