package comvoice.example.zhangbin.startimage.service;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.litepal.LitePal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import comvoice.example.zhangbin.startimage.model.User;
import comvoice.example.zhangbin.startimage.utils.Const;
import comvoice.example.zhangbin.startimage.utils.FileUtils;

public class UpLoadService extends Service {
    private FTPClient ftpClient;//FTP连接
    private String strIp="118.25.70.83";//ip地址
    private int intPort=21;//端口号
    private String user="lfy";//用户名
    private String password="BhexLPNpGN";//用户密码
    private long LocalAllSize,FTPAllSize;
    FileUtils fileUtils;
    public UpLoadService(){
        ftpClient=new FTPClient();
        fileUtils=new FileUtils();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new  uploadTAsk().execute("");
        return super.onStartCommand(intent, flags, startId);
    }
    private class uploadTAsk extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

                String path = "";
                path= Const.fromPath+Const.SPscreenId;
                File file=new File(path);//某次筛查的全部文件，即根目录，如100
                if (file.exists()) {
                        boolean isLoginSuccess = ftpLogin();
                        if (isLoginSuccess) {
                            if (upLoadFileProcess != null) {
                                upLoadFileProcess.getUpLoadStart();
                            }
                            upLoadDir(path,"/");
                            boolean isUpSuccess=false;
                            Log.e("ceshi1",Const.SPscreenId+",,,"+path);
                            if(Const.SPscreenId!=""){
                                isUpSuccess=uploadDirectory(path, "/JTH图片/");
                                if(isUpSuccess){
                                    userList=LitePal.where("screenId=?",new File(path).getName()).find(User.class);
                                    if(userList.size()>0){
                                        userList.get(0).setIdentification("1");
                                        userList.get(0).save();
                                    }
                                }else {
                                    if (upLoadFileProcess != null) {
                                        upLoadFileProcess.getUpLoadFaild();
                                    }
                                }

                            }else {
                                isUpSuccess=initFiles(path);
                            }
                            if (isUpSuccess) {
                                ftpLogOut();
                                FTPAllSize=0;
                                LocalAllSize=0;
                                stopSelf();
                                if (upLoadFileProcess != null) {
                                    upLoadFileProcess.getUpLoadSuccess();
                                }
                            } else {
                                if (upLoadFileProcess != null) {
                                    upLoadFileProcess.getUpLoadFaild();
                                }
                            }
                        }else {
                            if (upLoadFileProcess != null) {
                                upLoadFileProcess.getUpLoadFaild();
                            }
                            stopSelf();
                        }
                }
            return null;
        }

    }
    /**
     * 便历指定的文件夹下有几个子文件
     */
    List<User>userList;
    private boolean initFiles(String path){
        File file=new File(path);
        File[]files=file.listFiles();
        boolean Upsuccess=false;
        if(files.length>0){
            for(int i=0;i<files.length;i++){
                Upsuccess=uploadDirectory(path, "/");
                if(!Upsuccess){
                    Upsuccess=false;
                    break;
                }else {
                    userList=LitePal.where("screenId=?",files[i].getName()).find(User.class);
                    if(userList.size()>0){
                        userList.get(0).setIdentification("1");
                        userList.get(0).save();
                    }
                    Upsuccess=true;
                }
            }
        }
        return Upsuccess;
    }


    /***
     * @上传文件夹
     * @param localDirectory
     *   当地文件夹
     * @param remoteDirectoryPath
     *   Ftp 服务器路径 以目录"/"结束
     * */
    boolean makeDirFlag=false;
    public boolean uploadDirectory(String localDirectory,
                                   String remoteDirectoryPath) {
        Log.e("localDirectory",localDirectory);
        File src = new File(localDirectory);

        try {
            remoteDirectoryPath=remoteDirectoryPath+src.getName()+"/";
            initCreateFtpFile(remoteDirectoryPath);
            initCreate(localDirectory);
        }catch (Exception e) {
//            e.printStackTrace();
            Log.e("is",e.getMessage().toString());
        }
        if(makeDirFlag){
            File[] allFile = src.listFiles();
            if(allFile==null){
                return makeDirFlag;
            }
            if(allFile.length>0)
            for (int currentFile = 0;currentFile < allFile.length;currentFile++) {
                if (!allFile[currentFile].isDirectory()) {
                    String srcName = allFile[currentFile].getPath().toString();
                    uploadFile(new File(srcName), remoteDirectoryPath);
                }
            }
            for (int currentFile = 0;currentFile < allFile.length;currentFile++) {
                if (allFile[currentFile].isDirectory()) {
                    // 递归
                    uploadDirectory(allFile[currentFile].getPath().toString(),
                            remoteDirectoryPath);
                    initCopy(allFile[currentFile].getPath().toString());
                }
            }
            return makeDirFlag;
        }else {
            ftpLogOut();
            return makeDirFlag;
        }
    }
    /***
     * 上传Ftp文件
     * @param localFile 当地文件
     * @param
     * */
    int num=0;
    public boolean uploadFile(File localFile, String romotUpLoadePath) {
        BufferedInputStream inStream = null;
        boolean success = false;
        try {
            this.ftpClient.changeWorkingDirectory(romotUpLoadePath);// 改变工作路径
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            Log.e("logging",localFile.getName() + "开始上传.....");
            success = this.ftpClient.storeFile(localFile.getName(), inStream);
            //文件上传成功
            if (success == true) {
                num=0;
               initSize(romotUpLoadePath,localFile);
                //将已上传数据的百分比传递给广播，实现ui界面的更新
                double percent=((double)FTPAllSize/LocalAllSize)*100;
                if (upLoadFileProcess != null) {
                    upLoadFileProcess.getUpLoadFileProcessPrecent(percent);
                }
                initDelete(localFile.getAbsolutePath());

                if(percent>=100){
                    fileUtils.deleteFile(new File(Const.fromPath+Const.SPscreenId));
                }
                return success;
            }else {//文件上传失败后，再次进行上传，最多上传4次
                if(num<=3){
                    uploadFile(localFile,romotUpLoadePath);
                }else {
//                    LogWriteUtils.e("上传失败图片",localFile.getAbsolutePath());
                }
                num++;
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inStream != null) {
                try {
                    inStream.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }
    //创建服务器文件
    private void initCreateFtpFile(String remoteDirectoryPath){
        //遍历该服务器某级目录下所有文件的集合
        FTPFile[]ftpFiles= new FTPFile[0];
        try {
            ftpClient.enterLocalPassiveMode();
            ftpFiles = ftpClient.listFiles(remoteDirectoryPath);
            if(ftpFiles.length==0){
                makeDirFlag= this.ftpClient.makeDirectory(remoteDirectoryPath);//在ftp服务器上创建文件
            }else {
                makeDirFlag=true;
            }
            if(!makeDirFlag){
                if(num<=3){
                    initCreateFtpFile(remoteDirectoryPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //得到服务器文件大小，刚刚上传成功的,如果上传成功，但是该文件的大小没有计入总大小，则重复进行计算
    private void initSize(String path,File localFile){
        FTPFile[]ftpFiles= new FTPFile[0];
        try {
            ftpClient.enterLocalPassiveMode();
            ftpFiles = ftpClient.listFiles(path+localFile.getName());
            if(ftpFiles.length>0){
                FTPAllSize+=ftpFiles[0].getSize();
                Log.e("loadSuccess",localFile.getName() + "上传成功"+FTPAllSize);
            }else {
                if(num<=3){
                    initSize(path,localFile);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //文件复制成功后删除原始文件
    private void initDelete(String path){
        new File(path).delete();
    }
    //创建复制的目标目录
    private void initCreate(String filePath){
        String []path=filePath.split("/");
        String toMir=Const.toPath;
        if(path.length>=4){
            for(int i=5;i<path.length;i++){
                toMir+=path[i]+"/";
            }
        }
        File file=new File(toMir);
        if(!file.exists()){
            file.mkdirs();
        }
    }
    //每次上传成功后开始复制文件
    private int initCopy(String filePath){
        String []path=filePath.split("/");
        String toDir=Const.toPath;
        if(path.length>=4){
            for(int i=5;i<path.length-1;i++){
                toDir+=path[i]+"/";
            }
            toDir+=path[path.length-1];
        }
       int temp= fileUtils.copyFileSingleImage(filePath,toDir);
        return temp;
    }
    /**
     * @退出关闭服务器链接
     * */
    public void ftpLogOut() {
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                boolean reuslt = this.ftpClient.logout();// 退出FTP服务器
                if (reuslt) {
                    Log.e("success","成功退出服务器");
                }
            }catch (IOException e) {
                e.printStackTrace();
                Log.e("success","退出FTP服务器异常！" + e.getMessage());
            }finally {
                try {
                    this.ftpClient.disconnect();// 关闭FTP服务器的连接
                }catch (IOException e) {
                    e.printStackTrace();
                    Log.e("success","关闭FTP服务器的连接异常！");
                }
            }
        }
    }
    /**
     * @return 判断是否登入成功
     * */
    public boolean ftpLogin() {
        boolean isLogin = false;
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
        ftpClient.setControlEncoding("utf-8");
        ftpClient.configure(ftpClientConfig);
        try {
            if (this.intPort > 0) {
                ftpClient.connect(this.strIp, this.intPort);
            }else {
                ftpClient.connect(this.strIp);
            }
            // FTP服务器连接回答
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                Log.e("faild","登录FTP服务失败！");
                return isLogin;
            }
            isLogin=ftpClient.login(this.user, this.password);
            // 设置传输协议
//            this.ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            Log.e("faild","恭喜" + this.user + "成功登陆FTP服务器"+isLogin);
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("faild",this.user + "登录FTP服务失败！" + e.getMessage()+isLogin);
        }
        ftpClient.setBufferSize(1024 * 2);
        ftpClient.setDataTimeout(30 * 1000);
        return isLogin;
    }
    public void upLoadDir(String localDir,String remoteDir){
        File file=new File(localDir);
        if(file.exists()){
            LocalAllSize=getSize(file.listFiles());
        }

//        Log.e("localsize",LocalAllSize+"");
//        try {
//            this.ftpClient.makeDirectory(remoteDir +file.getName()+"-HPV/");
//            this.ftpClient.makeDirectory(remoteDir +file.getName()+"-TCT/");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    /**
     * 本地文件大小
     */
    public long getSize(File [] localFile){

        for(int i=0;i<localFile.length;i++){
            if(localFile[i].isFile()){
                LocalAllSize+=localFile[i].length();
            }else {
                getSize(localFile[i].listFiles());
            }
        }
        return LocalAllSize;
    }

    public interface UpLoadFileProcess{
        void getUpLoadStart();
        void getUpLoadFileProcessPrecent(double percent);
        void getUpLoadSuccess();
        void getUpLoadFaild();
    }

    static UpLoadFileProcess upLoadFileProcess;

    public static void setUpLoadFileProcessListener(UpLoadFileProcess upLoadFileProcessListener) {
        upLoadFileProcess = upLoadFileProcessListener;
    }
}
