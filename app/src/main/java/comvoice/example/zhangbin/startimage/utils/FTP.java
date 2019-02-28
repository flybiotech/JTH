package comvoice.example.zhangbin.startimage.utils;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TimeZone;

/**
 * Created by zhangbin on 2018/4/25.
 */

public class FTP {
    private FTPClient ftpClient;//FTP连接
    private String strIp;//ip地址
    private int intPort;//端口号
    private String user;//用户名
    private String password;//用户密码

    /* *
 * Ftp构造函数
 */
    public FTP(String strIp, int intPort, String user, String Password) {
        this.strIp = strIp;
        this.intPort = intPort;
        this.user = user;
        this.password = Password;
        this.ftpClient = new FTPClient();
    }
    /**
     * @return 判断是否登入成功
     * */
    public boolean ftpLogin() {
        boolean isLogin = false;
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
        this.ftpClient.setControlEncoding("utf-8");
//        this.ftpClient.setControlEncoding("GBK");
        this.ftpClient.configure(ftpClientConfig);
        try {
            if (this.intPort > 0) {
                this.ftpClient.connect(this.strIp, this.intPort);
            }else {
                this.ftpClient.connect(this.strIp);
            }
            // FTP服务器连接回答
            int reply = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
                Log.e("faild","登录FTP服务失败！");
                return isLogin;
            }
            this.ftpClient.login(this.user, this.password);
            // 设置传输协议
//            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            Log.e("faild","恭喜" + this.user + "成功登陆FTP服务器");
            isLogin = true;
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("faild",this.user + "登录FTP服务失败！" + e.getMessage());
        }
        this.ftpClient.setBufferSize(1024 * 2);
        this.ftpClient.setDataTimeout(30 * 1000);
        return isLogin;
    }
    /**
     * 创建hpv,tct服务器目录路径
     */
    public void uploadHPV(String localDirectory,String remoteDirectoryPath){
        File file=new File(localDirectory);
        String remoteDirectoryPathHPV=remoteDirectoryPath+file.getName()+"-HPV/";
        String remoteDirectoryPathTCT=remoteDirectoryPath+file.getName()+"-TCT/";
        try {
            this.ftpClient.makeDirectory(remoteDirectoryPathHPV);
            this.ftpClient.makeDirectory(remoteDirectoryPathTCT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /***
     * @上传文件夹
     * @param localDirectory
     *   当地文件夹
     * @param remoteDirectoryPath
     *   Ftp 服务器路径 以目录"/"结束
     * */
    public boolean uploadDirectory(String localDirectory,
                                   String remoteDirectoryPath) {
        File src = new File(localDirectory);
        boolean makeDirFlag=false;
        try {
            remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";
            Log.e("remotePath",remoteDirectoryPath);
            FTPFile[]ftpFiles=ftpClient.listFiles(remoteDirectoryPath);
            if(ftpFiles.length==0){
            makeDirFlag= this.ftpClient.makeDirectory(remoteDirectoryPath);
                Log.e("服务器，","不存在");
            }else {
                makeDirFlag=true;
                Log.e("服务器，","存在");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        if(makeDirFlag){
            File[] allFile = src.listFiles();
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
                }
            }
            return makeDirFlag;
        }else {
            ftpLogOut();
            return makeDirFlag;
        }
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
    /***
     * 上传Ftp文件
     * @param localFile 当地文件
     * @param
     * */
    public boolean uploadFile(File localFile, String romotUpLoadePath) {
        BufferedInputStream inStream = null;
        boolean success = false;
        try {
            this.ftpClient.changeWorkingDirectory(romotUpLoadePath);// 改变工作路径
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            Log.e("logging",localFile.getName() + "开始上传.....");
            success = this.ftpClient.storeFile(localFile.getName(), inStream);
            if (success == true) {
                Log.e("loadSuccess",localFile.getName() + "上传成功");
                return success;
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("haveMO",localFile + "未找到");
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

}
