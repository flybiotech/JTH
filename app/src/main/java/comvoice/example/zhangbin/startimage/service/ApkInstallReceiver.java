package comvoice.example.zhangbin.startimage.service;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import comvoice.example.zhangbin.startimage.R;

/**
 * Created by Administrator on 2017/8/13.
 */

public class ApkInstallReceiver extends BroadcastReceiver {

    private static String TAG = "TAG_ApkInstallReceiver";

    //        下载完成后，下载管理会发出DownloadManager.ACTION_DOWNLOAD_COMPLETE这个广播，并传递downloadId作为参数。通过接受广播我们可以打开对下载完成的内容进行操作

    //        点击下载中通知栏提示，系统会对下载的应用单独发送Action为DownloadManager.ACTION_NOTIFICATION_CLICKED广播。intent.getData为content://downloads/all_downloads/29669，最后一位为downloadId。
//        如果同时下载多个应用，intent会包含DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS这个key，表示下载的的downloadId数组。这里设计到下载管理通知栏的显示机制
    @Override
    public void onReceive(Context context, Intent intent) {
//        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
//            //通过这个返回也可以获取 系统下载器分配的id
//            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//            long localDownloadId = UpdaterUtils.getLocalDownloadId(context);
//            if (downloadApkId == localDownloadId) {
//                installApk(context, downloadApkId);
//            }
//        }else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)){
//            //处理 如果还未完成下载，用户点击Notification ,则调到下载的界面
//            Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
//            // 由于没有在Activity环境下启动Activity,设置下面的标签
//            viewDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(viewDownloadIntent);

//            long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
//            //点击通知栏取消下载
//            manager.remove(ids);
//            Toast.makeText(context, "已经取消下载", Toast.LENGTH_SHORT).show();

//        }


    }


    //安装apk
    private static void installApk(Context context, long downloadApkId) {
        //    通过隐式意图调用系统安装程序安装APK
        Intent install = new Intent(Intent.ACTION_VIEW);
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //获取下载地址的uri   downloadFileUri: content://downloads/all_downloads/38
        Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.e(TAG, "downloadFileUri: "+downloadFileUri);
        if (downloadFileUri != null) {
            File file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    , "update.apk");
            if (Build.VERSION.SDK_INT > 24) {
                Uri apkUri =
                        FileProvider.getUriForFile(context,context.getPackageName()+ ".fileprovider", file);
//                content://com.qcam.fileprovider/external_files/Download/update.apk
                Log.e(TAG, "android 7.0 : apkUri "+apkUri );
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                context.startActivity(install);
            } else {
//                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                //Uri.fromFile(file) : file:///storage/emulated/0/Download/update.apk
                Log.e(TAG, "Uri.fromFile(file) : "+ Uri.fromFile(file) );

                install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                context.startActivity(install);
            }

        } else {
//            Toast.makeText(context, context.getString(R.string.notification_fail), Toast.LENGTH_SHORT).show();
        }
    }


//    /**
//     * 通过隐式意图调用系统安装程序安装APK
//     */
//    public static void install(Context context) {
//        File file = new File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                , "myApp.apk");
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        // 由于没有在Activity环境下启动Activity,设置下面的标签
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
//            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
//            Uri apkUri =
//                    FileProvider.getUriForFile(context, "com.a520wcf.chapter11.fileprovider", file);
//            //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//        }else{
//            intent.setDataAndType(Uri.fromFile(file),
//                    "application/vnd.android.package-archive");
//        }
//        context.startActivity(intent);
//    }


//    public void openFile(File file, Context context) {
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(Intent.ACTION_VIEW);
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){//android 7.0
//            Uri uriForFile = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", file);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setDataAndType(uriForFile, context.getContentResolver().getType(uriForFile));
//        }else{
//            intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));
//        }
//        try {
//            context.startActivity(intent);
//        } catch (Exception var5) {
//            var5.printStackTrace();
//            Toast.makeText(context, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
//        }
//    }
//    public String getMIMEType(File file) {
//        String var1 = "";
//        String var2 = file.getName();
//        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
////        MimeTypeMap类是专门处理mimeType的类。
//        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
//        return var1;
//    }

//    public static boolean deleteFileWithPath(String filePath) {
//        SecurityManager checker = new SecurityManager();
//        File f = new File(filePath);
//        checker.checkDelete(filePath);
//        if (f.isFile()) {
//            f.delete();
//            return true;
//        }
//        return false;
//    }

}
