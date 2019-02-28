package comvoice.example.zhangbin.startimage.model;


import org.litepal.crud.LitePalSupport;

/**
 * Created by zhangbin on 2018/6/25.
 */

public class Doctor extends LitePalSupport {
    private Integer dID;//医生ID
    private String dName;//姓名
    private String dPassword;//密码
    private String hospital;//医院

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public Integer getdID() {
        return dID;
    }

    public void setdID(Integer dID) {
        this.dID = dID;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public String getdPassword() {
        return dPassword;
    }

    public void setdPassword(String dPassword) {
        this.dPassword = dPassword;
    }
}
