package comvoice.example.zhangbin.startimage.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import comvoice.example.zhangbin.startimage.model.Doctor;
import comvoice.example.zhangbin.startimage.model.LoginMsg;
import comvoice.example.zhangbin.startimage.model.User;
import comvoice.example.zhangbin.startimage.sp.SPUtils;

/**
 * Created by zhangbin on 2018/6/26.
 * 用来操纵数据库
 */

public class LitepalUtils {

    private Context mContext;

    public LitepalUtils(Context context){
        this.mContext=context;
    }

    /**
     * 保存注册医生信息
     */
    public boolean initSave(String name,String dPassword,String hospital){
        boolean save=false;
        int dID=0;

        List<Doctor>doctorList=LitePal.findAll(Doctor.class);
        //第一次注册
        if(doctorList.size()==0){
            dID=1;
        }else {//多次注册（非同一账户）
            dID=doctorList.get(doctorList.size()-1).getdID()+1;
        }
        Doctor doctor=new Doctor();
        doctor.setdID(dID);
        doctor.setdName(name);
        doctor.setdPassword(dPassword);
        doctor.setHospital(hospital);
        doctor.save();
        save=true;
        return save;
    }
    /**
     * 判断医生用户名知否已被注册
     */
    public boolean isHad(String name){
        List<Doctor>doctorList= LitePal.where("dName=?",name).find(Doctor.class);
        if(doctorList.size()==0){
            return false;
        }else {
            return true;
        }
    }
    /**
     * 查询所有医生的姓名
     */
    public List<String>getDoctors(){
        List<Doctor>doctorList=LitePal.findAll(Doctor.class);
        List<String>doctorName=new ArrayList<>();
        for(Doctor doctor:doctorList){
            doctorName.add(doctor.getdName());
        }
        return doctorName;
    }
    /**
     * 查询医生的密码
     */
    public String getdPassword(String dName){
        List<Doctor>doctorList=LitePal.where("dName=?",dName).find(Doctor.class);
        if(doctorList.size()>0){
            return doctorList.get(0).getdPassword();
        }
        return "";
    }
    /**
     * 判断输入的账户密码是否正确，正确时将登录的账户信息保存到另一个数据库中，该数据永远只保存一条数据
     */
    public boolean isTrue(String name,String password){
        List<Doctor>doctorList=LitePal.where("dName=?",name).find(Doctor.class);
        if(doctorList.size()>0){
            String pass=doctorList.get(0).getdPassword();
            if(pass.equals(password)){
                List<LoginMsg>loginMsgs=LitePal.findAll(LoginMsg.class);
                if(loginMsgs.size()==0){
                    LoginMsg loginMsg=new LoginMsg();
                    loginMsg.setDoctor(doctorList.get(0).getdName());
                    loginMsg.setHospital(doctorList.get(0).getHospital());
                    loginMsg.save();
                }else {
                    loginMsgs.get(0).setDoctor(doctorList.get(0).getdName());
                    loginMsgs.get(0).setHospital(doctorList.get(0).getHospital());
                    loginMsgs.get(0).save();
                }
                return true;
            }
        }
        return false;
    }
    /*
    检验HPV或TCT或身份证是否已存在,1表示HPV,2表示TCT,3表示身份证
     */
    public boolean iseEisted(String code,int temp){
        List<User>userList=null;
        if(temp==1){
            userList=LitePal.where("HPV=?",code).find(User.class);
        }else if(temp==2){
            userList=LitePal.where("TCT=?",code).find(User.class);
        }else if(temp==3){
            userList=LitePal.where("pID=?",code).find(User.class);
        }
        if(userList.size()>0){
            return true;
        }
        return false;
    }

    /**
     * 保存患者信息
     */
    public boolean userSave(String screenId,String name,String age,String phone,String hpv){
        List<LoginMsg>loginMsgs=LitePal.findAll(LoginMsg.class);
        User user=new User();
        user.setScreenId(screenId);
        user.setName(name);
        user.setAge(age);
        user.setPhone(phone);
//        user.setpID(ID);
        user.setPath(Const.fromPath+screenId);
        user.setHPV(hpv);
        user.setIdentification("0");
        user.setData(getDate());
//        user.setTCT(tct);
        try {
//            user.setHospital(loginMsgs.get(0).getHospital());
            if(Const.DoctorPhone==""){
                Const.DoctorPhone=getDPhone();
            }
            user.setdPhone(Const.DoctorPhone);
//            user.setData(getDate());
        }catch (Exception e){
            Log.e("异常，",e.getMessage().toString());
//            Log.e("异常1，",loginMsgs.get(0)+"//////"+loginMsgs.get(0).getDoctor()+"/////"+loginMsgs.get(0).getDoctor().getHospital());
        }

        boolean suc=user.save();
        return suc;
    }
    /**
     * 得到患者的账户
     */
    private String getDPhone(){
        return (String) SPUtils.get(mContext,Const.DOC_MOBILE,null);

    }
    /**
     * 得到保存患者时的时间，毫秒
     */
    private String getDate(){
//        SimpleDateFormat formatter = null; //转化时间
//        formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss", Locale.getDefault());//系统当前时间
//        Date curDate =  new Date();
//        String date=formatter.format(curDate);
        long time = System.currentTimeMillis();
        String data = String.valueOf(time);
        return data;
    }
}
