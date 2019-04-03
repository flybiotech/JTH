package comvoice.example.zhangbin.startimage.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.ListMessage;
import comvoice.example.zhangbin.startimage.model.User;
import comvoice.example.zhangbin.startimage.sp.SPUtils;

/**
 * 文件操作类，包括复制和删除
 */
public class FileUtils {
    private LoadingDialog loadingDialog;
    private Context mContext;
    private String fromPath = "";
    private String toPath = "";
    private boolean isCopy;//判断是否已复制
    private NetWorkUtils netWorkUtils;
    private FileCopyAndDelListener mListener;

    public FileUtils(Context context) {
        this.mContext = context;

    }

    public FileUtils(Context context, FileCopyAndDelListener listener) {
        this.mContext = context;
        mListener = listener;
//        netWorkUtils = new NetWorkUtils(mContext);
    }

    public FileUtils() {

    }

    /**
     * 将默认路径下的目录复制到指定的目录下 ,循环复制所有的文件
     */
    public int cicleCoyeFile(String fromFile, String toFile) {
        //要复制的文件目录
        File[] fromList;
        File file = new File(fromFile);
        //判断文件是否存在
        if (!file.exists()) {
            return -1;
        }
        //如果存在则获取当前目录下的所有文件，填充数组
        fromList = file.listFiles();
        //目标目录
        File toList = new File(toFile);
        //创建目录
        if (!toList.exists()) {
            toList.mkdirs();
        }
        //遍历要复制的全部文件
        for (int i = 0; i < fromList.length; i++) {
            if (fromList[i].isDirectory()) {//如果当前项为子目录，进行递归
                cicleCoyeFile(fromList[i].getPath() + "/", toFile + "/" + fromList[i].getName() + "/");
            } else {//如果当前项为文件则进行拷贝
                copyFileSingleImage(fromList[i].getPath(), toFile + fromList[i].getName());
            }
        }
        return 0;
    }

    /**
     * 拷贝具体文件
     */
    public int copyFileSingleImage(String fromFile, String toFile) {
        try {
            InputStream inputStream = new FileInputStream(fromFile);
            OutputStream outputStream = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int d;
            while ((d = inputStream.read(bt)) > 0) {
                outputStream.write(bt, 0, d);
            }
            inputStream.close();
            outputStream.close();
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    //判断是否已复制原始文件, true :表示没有复制，false 表示已经复制完成
    public boolean isCopy(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (getSize(files) > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 本地文件大小
     */
    private long LocalAllSize;

    public long getSize(File[] localFile) {

        for (int i = 0; i < localFile.length; i++) {
            if (localFile[i].isFile()) {
                LocalAllSize += localFile[i].length();
            } else {
                getSize(localFile[i].listFiles());
            }
        }
        return LocalAllSize;
    }

    /**
     * 判断默认文件夹下指定目录是否有文件
     */
    boolean isCopyComplete = false;
    public void startCopyFileAndDel() {

        isCopy = isCopy(new File(Const.originalPath));
        String screeningId = initGetSP();

        if (null != screeningId) {
            if (isCopy) {//默认路径存在文件
                mListener.fileStartCopy();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //原始文件夹下，如果有没有，没有被复制，true 。那么将开始复制
                        if (isCopy(new File(Const.originalPath + "/质控图/PHOTOS"))) {
                            isCopyComplete=initCopy1(screeningId, 1);
                        }
                        if (isCopy(new File(Const.originalPath + "/醋酸白/PHOTOS"))) {
                            isCopyComplete=initCopy1(screeningId, 2);
                        }
                        if (isCopy(new File(Const.originalPath + "/碘油/PHOTOS"))) {
                            isCopyComplete=initCopy1(screeningId, 3);
                        }
                        //删除文件
                        if (Const.originalPath != null&&isCopyComplete) {
                            File file = new File(Const.originalPath);
                            deleteFile(file);
                        }
                        //文件复制成功
                        if(isCopyComplete){
                            mListener.fileCopySuccess();
                        }else {
                            mListener.fileCopyFailed(mContext.getString(R.string.copyFaild));
                        }

                    }
                }).start();

            }else {
                mListener.fileCopyFailed(mContext.getString(R.string.screeningID_empty));
            }
        }else {
            mListener.fileCopyFailed(mContext.getString(R.string.screeningID_empty));
        }
    }

    /**
     * 获取保存的本地信息
     */
    public String initGetSP() {
        return (String) SPUtils.get(mContext, Const.SCREENID_KEY, "");
    }

    /**
     * 将原始文件复制到指定的目录下
     */
    public boolean initCopy1(final String screenId, final int temp) {
//        initShowDialog(mContext.getString(R.string.fileCopying));
        List<User> userList = LitePal.where("screenId=?", screenId).find(User.class);
        int re = -1;
        if (userList.size() > 0) {

            if (temp == 1) {//原图
                fromPath = Const.originalPath + "/质控图/PHOTOS/";
                toPath = userList.get(0).getPath() + "/原图/";
            }
            if (temp == 2) {//醋酸白图
                fromPath = Const.originalPath + "/醋酸白/PHOTOS/";
                toPath = userList.get(0).getPath() + "/醋酸白/";
            }
            if (temp == 3) {//碘油图
                fromPath = Const.originalPath + "/碘油/PHOTOS/";
                toPath = userList.get(0).getPath() + "/碘/";
            }
        }
        Log.e("fromPath,toPath", fromPath + ",,," + toPath);
        re = cicleCoyeFile(fromPath, toPath);
        if (re == 0) {
            return true;
        }
        return false;

    }

    //对话框
    private void initShowDialog(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(mContext);
        }
        loadingDialog.setMessage(msg);
        loadingDialog.dialogShow();
    }

//    private void initDeleteFile() {
//        initShowDialog(mContext.getString(R.string.fileDelete));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Message message = handler.obtainMessage();
//                if (Const.originalPath != null) {
//                    File file = new File(Const.originalPath);
//                    deleteFile(file);
//                }
//                handler.sendMessage(message);
//            }
//        }).start();
//    }

    /**
     * 删除文件夹
     */
    public static boolean deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * 判断指定的文件夹下有几个子目录，匹配数据库数据，如果存在修改参数为1
     */
    private List<ListMessage> list_show;
    List<User> userList;

    public void getModify() {
        File file = new File(Const.fromPath);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                userList = LitePal.where("screenId=?", files[i].getName()).find(User.class);
                if (userList.size() > 0) {
                    userList.get(0).setIdentification("1");
                    userList.get(0).save();
                }
            }
        }
    }

    /**
     * 得到已核销未上传的患者信息
     */
    public List getList() {
        userList = LitePal.where("Identification=? and dPhone=?", "0",Const.DoctorPhone).find(User.class);
        list_show = new ArrayList<>();
        list_show.clear();
        if (userList.size() > 0) {
            for (int i = 0; i < userList.size(); i++) {
                ListMessage listMessage = new ListMessage();
                listMessage.setScreenId(userList.get(i).getScreenId());
                listMessage.setName(initHideName(userList.get(i).getName()));
                listMessage.setPhone(initHidePhone(userList.get(i).getPhone()));
                list_show.add(listMessage);
            }
        }
        return list_show;
    }

    /**
     * 隐藏手机号中间4位
     */
    private String initHidePhone(String phone){
        if(!phone.equals("")){
            return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return "";
    }

    /**
     * 隐藏姓名
     */
    private String initHideName(String name){
        if(!name.equals("")){
            String surname=name.substring(0,1);
            String hidename=name.substring(1,name.length());
            for(int i=0;i<hidename.length();i++){
                surname=surname+"*";
            }
            return surname;
        }
        return "";
    }
    public interface FileCopyAndDelListener {

        void fileCopySuccess();

        void fileCopyFailed(String msg);

        void fileDelSuccess();

        void fileDelFailed();

        void fileStartCopy();

    }


}
