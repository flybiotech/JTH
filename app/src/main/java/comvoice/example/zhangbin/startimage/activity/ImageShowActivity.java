package comvoice.example.zhangbin.startimage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lcw.library.imagepicker.view.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.adapter.ImagePreViewAdapter;
import comvoice.example.zhangbin.startimage.manager.SelectionManager;
import comvoice.example.zhangbin.startimage.sp.SPUtils;
import comvoice.example.zhangbin.startimage.utils.CaseListUtils;
import comvoice.example.zhangbin.startimage.utils.Const;
import comvoice.example.zhangbin.startimage.utils.FileUtils;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;

public class ImageShowActivity extends AppCompatActivity {
    @BindView(R.id.iv_actionBar_back)
    ImageView ivActionBarBack;
    @BindView(R.id.tv_actionBar_title)
    TextView tvActionBarTitle;
    @BindView(R.id.tv_actionBar_commit)
    Button tvActionBarCommit;
    @BindView(R.id.iv_item_check)
    ImageView ivItemCheck;
    @BindView(R.id.ll_pre_select)
    LinearLayout llPreSelect;
    @BindView(R.id.rl_main_bottom)
    RelativeLayout rlMainBottom;
    @BindView(R.id.vp_main_preImage)
    HackyViewPager vpMainPreImage;
    @BindView(R.id.iv_main_play)
    ImageView ivMainPlay;
    private List<String> urls = null;//显示的图片的集合
    private ImagePreViewAdapter imagePreViewAdapter;
    private CaseListUtils caseListUtils;
    private String TAG_IMAGE = "imageshow_";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_show);
        ButterKnife.bind(this);
        initView();
        getDate();
        initPageListener();
    }

    private void initPageListener() {
        vpMainPreImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvActionBarTitle.setText(String.format("%d/%d", position + 1, urls.size()));
//                setIvPlayShow(urls.get(position));
                updateSelectButton(urls.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    /**
     * 设置是否显示视频播放按钮
     * @param mediaFile
     */
    private void setIvPlayShow(String mediaFile) {
//        if (mediaFile.getDuration() > 0) {
//            mIvPlay.setVisibility(View.VISIBLE);
//        } else {
        ivMainPlay.setVisibility(View.GONE);
//        }
    }
    private void initView() {
        caseListUtils = new CaseListUtils(this);

    }

    @OnClick({R.id.iv_actionBar_back, R.id.tv_actionBar_commit, R.id.ll_pre_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_actionBar_back:
                SPUtils.remove(this,Const.SPscreenId);
                SelectionManager.getInstance().removeAll();
                finish();
                break;
            case R.id.tv_actionBar_commit:
                Const.stringList = SelectionManager.getInstance().getSelectPaths();
                SPUtils.setPathList(this,Const.SPscreenId,Const.stringList);
                SelectionManager.getInstance().removeAll();
                finish();
                break;
            case R.id.ll_pre_select:
                if(urls == null || urls.size() == 0){
                    return;
                }
                boolean addsuccess = SelectionManager.getInstance().addImageToSelectList(urls.get(vpMainPreImage.getCurrentItem()));
                if(addsuccess){
                    updateSelectButton(urls.get(vpMainPreImage.getCurrentItem()));
                    updateCommitButton();
                }else {
                    SouthUtil.showToast(this,"还未选择图片");
                }
                break;
        }
    }
    private int mPosition = 0;
    public static final String IMAGE_POSITION = "imagePosition";
    //得到本地数据源

    private void getDate(){
        urls = caseListUtils.ImageShow();
       if(urls == null || urls.size() == 0){
           return;
       }
        SelectionManager.getInstance().setMaxCount(urls.size());
        mPosition = getIntent().getIntExtra(IMAGE_POSITION,0);
        tvActionBarTitle.setText(String.format("%d/%d",mPosition+1,urls.size()));
        imagePreViewAdapter = new ImagePreViewAdapter(this,urls);
        vpMainPreImage.setAdapter(imagePreViewAdapter);
        vpMainPreImage.setCurrentItem(mPosition);
        //更新当前页面状态
        updateSelectButton(urls.get(mPosition));
        updateCommitButton();
    }


    //更新确认按钮的状态
    List<String>stringList;
    private void updateCommitButton(){
        if(urls == null){
            return;
        }
        int maxCount = urls.size();
        int selectCount = 0;
        stringList = SPUtils.getPathList(this,Const.SPscreenId);
//        if(null == stringList){
            selectCount = SelectionManager.getInstance().getSelectPaths().size();
//        }else {
//            selectCount = stringList.size();
//        }
        if(selectCount == 0){
            tvActionBarCommit.setEnabled(false);
            tvActionBarCommit.setText(getText(R.string.button_ok));
            return;
        }
        if(selectCount < maxCount){
            tvActionBarCommit.setEnabled(true);
            tvActionBarCommit.setText(String.format(getString(R.string.confirm_msg), selectCount, maxCount));
            return;
        }
        if(selectCount == maxCount){
            tvActionBarCommit.setEnabled(true);
            tvActionBarCommit.setText(String.format(getString(R.string.confirm_msg), selectCount, maxCount));
            return;
        }
    }

    //更新选择按钮状态
    private void updateSelectButton(String imagePath){
        boolean isSelect = SelectionManager.getInstance().isImageSelect(imagePath);
        if(isSelect){
            ivItemCheck.setImageDrawable(getResources().getDrawable(R.drawable.icon_image_checked));
        }else {
            ivItemCheck.setImageDrawable(getResources().getDrawable(R.drawable.icon_image_check));
        }
    }
}
