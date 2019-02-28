package comvoice.example.zhangbin.startimage.model;

import org.litepal.crud.LitePalSupport;

/**
 * Created by zhangbin on 2018/5/29.
 */

public class User extends LitePalSupport {
    private String name;
    private String phone;
    private String pID;
    private String path;
    private String HPV;
    private String TCT;
    private String data;
    private String hospital;
    private String dPhone;//医生账户
    private String screenId;//筛查id
    private String age;
    private String Identification;//是否已上传

    public String getIdentification() {
        return Identification;
    }

    public void setIdentification(String identification) {
        Identification = identification;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getdPhone() {
        return dPhone;
    }

    public void setdPhone(String dPhone) {
        this.dPhone = dPhone;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHPV() {
        return HPV;
    }

    public void setHPV(String HPV) {
        this.HPV = HPV;
    }

    public String getTCT() {
        return TCT;
    }

    public void setTCT(String TCT) {
        this.TCT = TCT;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }
}
