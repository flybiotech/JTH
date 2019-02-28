package comvoice.example.zhangbin.startimage.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.fragment.RegisterFragment;
import comvoice.example.zhangbin.startimage.fragment.SystemFragment;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.textView_home_patientInfo)
    TextView textViewHomePatientInfo;
//    @BindView(R.id.textView_home_casemanagerInfo)
//    TextView textViewHomeCasemanagerInfo;
    @BindView(R.id.textView_home_settingInfo)
    TextView textViewHomeSettingInfo;
    @BindView(R.id.linear_home_layout)
    LinearLayout linearHomeLayout;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    private long exitTiem = 0;
    private List<Fragment>fragments;
    private FragmentTransaction transaction;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        Intent intent=getIntent();
        int canshu=  intent.getIntExtra("canshu",0);//默认为登记页面
        if(canshu==0){
            switchState(0);//跳到患者信息fragment
            transaction.replace(R.id.fl_container, fragments.get(0));
//            viewPager.setCurrentItem(0);
        }
//        else if(canshu==2){//跳到系统中心界面
//            switchState(2);
//            transaction.replace(R.id.fl_container, fragments.get(2));
////            viewPager.setCurrentItem(2);
//        }
        else if (canshu == 1) {//跳到信息管理界面/
            switchState(1);
            transaction.replace(R.id.fl_container, fragments.get(1));
//            viewPager.setCurrentItem(1);
        }
    }

    private void initView() {
        fragments=new ArrayList<>();
//        MessageFragment messageFragment=new MessageFragment();
        RegisterFragment registerFragment=new RegisterFragment();
        SystemFragment systemFragment=new SystemFragment();
        fragments.add(registerFragment);
//        fragments.add(messageFragment);
        fragments.add(systemFragment);
        fragmentManager=getSupportFragmentManager();
        transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_container,fragments.get(0));
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTiem > 1000) {
                Toast.makeText(this, getString(R.string.stopApk), Toast.LENGTH_SHORT).show();
                exitTiem = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.textView_home_patientInfo, R.id.textView_home_settingInfo})
    public void onViewClicked(View view) {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        transaction = fragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.textView_home_patientInfo:
                transaction.replace(R.id.fl_container, fragments.get(0));
                switchState(0);
                break;
//            case R.id.textView_home_casemanagerInfo:
//                switchState(1);
//                transaction.replace(R.id.fl_container, fragments.get(1));
//                break;
            case R.id.textView_home_settingInfo:
                switchState(1);
                transaction.replace(R.id.fl_container, fragments.get(1));
                break;
        }
        transaction.commit();
    }
    private int mState=-1;
    //判断选中了哪个Fragment
    private void switchState(int state) {
        if (mState == state) {
            return;
        }
        mState = state;
        textViewHomePatientInfo.setTextColor(Color.BLACK);
//        textViewHomeCasemanagerInfo.setTextColor(Color.BLACK);
        textViewHomeSettingInfo.setTextColor(Color.BLACK);

        textViewHomePatientInfo.setSelected(false);
//        textViewHomeCasemanagerInfo.setSelected(false);
        textViewHomeSettingInfo.setSelected(false);


        switch (state) {
            case 0:
                textViewHomePatientInfo.setTextColor(Color.RED);
                textViewHomePatientInfo.setSelected(true);
                break;
//            case 1:
//                textViewHomeCasemanagerInfo.setTextColor(Color.RED);
//                textViewHomeCasemanagerInfo.setSelected(true);
//                break;
            case 1:
                textViewHomeSettingInfo.setTextColor(Color.RED);
                textViewHomeSettingInfo.setSelected(true);
                break;
            default:
                break;
        }
    }
}
