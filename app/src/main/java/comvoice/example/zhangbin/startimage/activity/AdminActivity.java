package comvoice.example.zhangbin.startimage.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.litepal.LitePal;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.Doctor;
import comvoice.example.zhangbin.startimage.model.FTPAccountLitepal;
import comvoice.example.zhangbin.startimage.model.LoginMsg;
import comvoice.example.zhangbin.startimage.model.User;
import comvoice.example.zhangbin.startimage.sp.SPUtils;
import comvoice.example.zhangbin.startimage.utils.Const;
import comvoice.example.zhangbin.startimage.utils.LoadingDialog;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;

public class AdminActivity extends AppCompatActivity{
    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_screen)
    ImageView ivScreen;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.bt_delete)
    Button btDelete;
    private LoadingDialog loadingDialog;
    private String pathAFLY = Environment.getExternalStorageDirectory() + "/AFLY_Save/";
    private String pathImage = Environment.getExternalStorageDirectory() + "/FLY_Image/";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            SouthUtil.showToast(AdminActivity.this,getString(R.string.delete_success));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        loadingDialog = new LoadingDialog(this);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setText(R.string.case_return);
        tvTitle.setText(R.string.delete);
    }
    @OnClick({R.id.btn_left, R.id.bt_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.bt_delete:
                deleteAll();
                break;
        }
    }
    //一键删除数据库和本地文件
    private void deleteAll() {
        loadingDialog.setMessage(getString(R.string.delete_data));
        loadingDialog.dialogShow();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                LitePal.deleteAll(Doctor.class);
                LitePal.deleteAll(FTPAccountLitepal.class);
                LitePal.deleteAll(LoginMsg.class);
                LitePal.deleteAll(User.class);
                SPUtils.remove(AdminActivity.this, Const.DOC_MOBILE);
                SPUtils.remove(AdminActivity.this, Const.DOC_PASSWORD);
                SPUtils.remove(AdminActivity.this,Const.SCREENID_KEY);
                SPUtils.remove(AdminActivity.this,Const.DELETE_KEY);
                File fileAFLY = new File(pathAFLY);
                if (fileAFLY.exists()) {
                    deleteFile(fileAFLY);
                }
                File fileImage = new File(pathImage);
                if (fileImage.exists()) {
                    deleteFile(fileImage);
                }
                handler.sendEmptyMessage(-1);
            }
        }).start();
    }

    /**
     * 删除文件夹所有内容
     */
    public void deleteFile(File file) {

        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            //
        }
    }


}
