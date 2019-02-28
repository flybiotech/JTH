package comvoice.example.zhangbin.startimage.model;

import org.litepal.crud.LitePalSupport;

public class FTPAccountLitepal extends LitePalSupport{
    private String FTPName;//ftp服务器账号
    private String FTPPassword;//ftp服务器密码

    public String getFTPName() {
        return FTPName;
    }

    public void setFTPName(String FTPName) {
        this.FTPName = FTPName;
    }

    public String getFTPPassword() {
        return FTPPassword;
    }

    public void setFTPPassword(String FTPPassword) {
        this.FTPPassword = FTPPassword;
    }
}
