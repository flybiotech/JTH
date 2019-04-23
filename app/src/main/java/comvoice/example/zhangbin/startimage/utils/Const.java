package comvoice.example.zhangbin.startimage.utils;

import android.Manifest;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

import comvoice.example.zhangbin.startimage.model.User;

/**
 * Created by dell on 2018/7/16.
 */

public class Const {
//    http://flybiotech.com/fly_user/user.php/login
    private static String mURL = "http://www.flybiotech.com/fly_user/user.php/";
    private static String mURLVerify = "https://wechat.flybiotech.cn/";

    public static boolean wifiRepeat = false;//wifi当前的wifi是否是重复连接
    public static int typeWifi = 3;
    public static String SZB_WIFI_SSID_KEY = "szbName";//视珍宝wiif
    public static String SZB_WIFI_PASS_KEY = "szbPass";//视珍宝密码
    public static String SZB_WIFI_MODIFY_KEY = "szbModify";//视珍宝账号和密码是否有修改
    public static String SZB_WIFI_FILTER_KEY = "szbFilter";//视珍宝WiFi过滤的关键字
    public static String LAN_WIFI_SSID_KEY = "LANName";//局域网wifi名称
    public static String LAN_WIFI_PASS_KEY = "LANPass";//局域网WiFi密码
    public static String LAN_WIFI_MODIFY_KEY = "LANModify";//局域网WiFi账号和密码是否有修改
    public static int wifiConnectTime = 1000 * 3; //10秒
    public static String DOC_MOBILE = "MOBILEdoc"; //记录登录的医生的手机号码
    public static String DOC_PASSWORD = "PASSWORDdoc";// 记录登录的医生的密码
    public static String WIFI_TYPE_LAN = "LAN";//设置连接wifi的类型
    public static String WIFI_TYPE_SZB = "SZB";//设置连接wifi的类型


    public static final String ip="112.65.248.237";//ftp服务器ip地址
    public static final int port=21;//端口
    public static final String name="LUFAYUAN";
    public static final String password="FLYXJ@1571076885";
    public static final String fromPath= Environment.getExternalStorageDirectory()+"/FLY_Image/JTH图片/";//第一次复制的目标路径，也是上传后自动复制的原始路径（需要复制两次）
    public static final String toPath=Environment.getExternalStorageDirectory()+"/FLY_Copy/";//第二次复制的目标路径
    public static final String originalPath=Environment.getExternalStorageDirectory()+"/AFLY_Save";//第一次复制的原始文件路径
    public static String SPscreenId="";
    public static String SCREENID_KEY="SCREENID";  // 获取保存的本地信息 用做复制的目标路径
    public static String AUTHCODE_KEY="authcode";  // 保存验证码
    public static String DoctorPhone="";//保存登陆的医生电话
    public static String DELETE_KEY = "DELETE_DATA";


    public static final String page="com.molink.brean.fly";
    public static String apk="base.apk";
    public static final List<User> msgList=new ArrayList<>();

    public static String URL_LOGIN = mURL + "login";
    public static String URL_REGISTER = mURL + "join";
    public static String URL_MODIFY = mURL + "editPwd";
    public static String URL_AUTH = mURL + "verify";
    public static String URL_VERIFY = mURLVerify + "jth/api/screenings/consume.json"; //核销


    public static int SCREENINGID = 1; //screeningId
    public static String HPVNUMBER = "xx1000"; //hpvNumber
    public static String MD5_USERNAME = "jthapi"; //
    public static String MD5_PASSWORD = "PvL3PVDtHsyAyUo"; //

    public static String FLY_SZB_apk = Environment.getExternalStorageDirectory() + "/com.stub.StubApp.apk";


    public static List<String>stringList;//选择的图片的集合

















    /**
     * 权限常量相关
     */
    public static final int WRITE_READ_EXTERNAL_CODE = 0x01;
    public static final String[] WRITE_READ_EXTERNAL_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public static final int HARDWEAR_CAMERA_CODE = 0x02;
    public static final String[] HARDWEAR_CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};

    public static final int ASK_CALL_BLUE_CODE= 0x03;//权限申请的返回值
    public static final String[] ASK_CALL_BLUE_PERMISSION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};












}
