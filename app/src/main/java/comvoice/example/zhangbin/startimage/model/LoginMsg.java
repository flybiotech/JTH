package comvoice.example.zhangbin.startimage.model;

import org.litepal.crud.LitePalSupport;

/**
 * Created by zhangbin on 2018/6/26.
 * 保存登录的账户信息
 */

public class LoginMsg extends LitePalSupport {
    private String doctor;
    private String hospital;

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
}
