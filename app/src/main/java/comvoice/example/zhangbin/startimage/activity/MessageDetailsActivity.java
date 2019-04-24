package comvoice.example.zhangbin.startimage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;


import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.User;
import comvoice.example.zhangbin.startimage.utils.CaseListUtils;
import comvoice.example.zhangbin.startimage.utils.DetailsUtils;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;

public class MessageDetailsActivity extends AppCompatActivity implements DetailsUtils.OnShowContentListener,
        OnItemClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

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
    @BindView(R.id.imageleft)
    ImageView imageLeft;
    @BindView(R.id.imageright)
    ImageView imageRight;

    DetailsUtils detailsUtils;
    String screenID = "";
    @BindView(R.id.btn_left)
    Button btnLeft;
    private List<String> imageAll = null;
    private User user = null;
    private String id;//接受传过来的id值
    private Adapter adapter;//视频展示的适配器

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

    private void initView() {
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setText(R.string.case_return);
        btnLeft.setOnClickListener(this);
        imageLeft.setOnClickListener(this);
        imageRight.setOnClickListener(this);


        detailsUtils = new DetailsUtils(MessageDetailsActivity.this, cbShow, tvImagenameshow01);
        detailsUtils.initView(screenID, MessageDetailsActivity.this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                Intent intent = new Intent(MessageDetailsActivity.this, MainActivity.class);
                intent.putExtra("canshu", 1);
                startActivity(intent);
                break;

            case R.id.imageleft:
                setCurPos(2);


                break;

            case R.id.imageright:
                setCurPos(1);


                break;
        }

    }

    private void setCurPos(int type) {
        int pos = detailsUtils.getCurrentPosition();
        int p = 0;
        if (pos < -1) {
            SouthUtil.showToast(this, getString(R.string.case_imageNo));
            return;
        }

        if (type == 2) { //左边按钮的点击结果
            p = detailsUtils.setCurrentPosition(pos - 1);

        } else if(type==1){//右边按钮的点击结果
            p = detailsUtils.setCurrentPosition(pos + 1);

        }


        if (p == 2) {
            SouthUtil.showToast(this, getString(R.string.case_imageFirst));
        } else if (p == 1) {
            SouthUtil.showToast(this, getString(R.string.case_imageLast));
        }

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onItemClick(int position) {
        if (imageAll != null) {
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra("msg", imageAll.get(position));
            startActivity(intent);
        }
    }

    @Override
    public void initView(List<User> listMsg) {
        if (listMsg != null && listMsg.size() > 0) {
            user = listMsg.get(0);
            detailsUtils.initDetils(tvDengjiClm, listMsg);
            detailsUtils.startImageShow(user, getString(R.string.image_artword), getString(R.string.image_acetic_acid_white),
                    getString(R.string.image_Lipiodol), MessageDetailsActivity.this);
        }
    }

    @Override
    public void showImage(List<String> listImagePath) {
        imageAll = listImagePath;
        if (imageAll != null && imageAll.size() > 0) {
            tvImagenameshow01.setText("图片展示 ： " + new File(imageAll.get(0)).getName());
        }

        detailsUtils.lunbo(imageAll, this);
//        detailsUtils.videoShow(msg, this);
    }

    @Override
    public void showVideo(List<String> listVideoPath) {

    }


}
