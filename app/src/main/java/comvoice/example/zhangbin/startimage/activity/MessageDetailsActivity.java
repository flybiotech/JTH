package comvoice.example.zhangbin.startimage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;


import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.User;
import comvoice.example.zhangbin.startimage.utils.DetailsUtils;

public class MessageDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_screen)
    ImageView ivScreen;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.tv_dengji_clm)
    TextView tvDengjiClm;
    @BindView(R.id.tv_imagenameshow01)
    TextView tvImagenameshow01;
    @BindView(R.id.cb_show)
    ConvenientBanner cbShow;
    @BindView(R.id.tv_case_video)
    TextView tvCaseVideo;
    @BindView(R.id.case_recycler_vdieo)
    ListView caseRecyclerVdieo;
    DetailsUtils detailsUtils;
    String screenID = null;
    @BindView(R.id.btn_left)
    Button btnLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_message_details);
        ButterKnife.bind(this);
        screenID = getIntent().getStringExtra("message");
        initView();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//    }

    private void initView() {
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setText(R.string.case_return);
        List<User> users = null;
        if (screenID != null) {
            users = LitePal.where("screenId=?", screenID).find(User.class);
        }
        detailsUtils = new DetailsUtils(MessageDetailsActivity.this, tvDengjiClm, users);
        detailsUtils.initDetils();
    }

    @OnClick(R.id.btn_left)
    public void onViewClicked() {
        Intent intent=new Intent(MessageDetailsActivity.this,MainActivity.class);
        intent.putExtra("canshu",1);
        startActivity(intent);
    }
}
