package comvoice.example.zhangbin.startimage.fragment;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.activity.DeleteSettingActivity;
import comvoice.example.zhangbin.startimage.activity.FTPSettingActivity;
import comvoice.example.zhangbin.startimage.activity.UploadActivity;
import comvoice.example.zhangbin.startimage.activity.WifiSettingActivity;
import comvoice.example.zhangbin.startimage.sp.SPUtils;
import comvoice.example.zhangbin.startimage.utils.Const;
import comvoice.example.zhangbin.startimage.utils.SouthUtil;
import comvoice.example.zhangbin.startimage.utils.WriteExcelUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SystemFragment extends Fragment implements AdapterView.OnItemClickListener{

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_screen)
    ImageView ivScreen;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.list_setting)
    ListView listSetting;
    Unbinder unbinder;
    private List<String>stringList;
    private ListAdapter adapter;
    private WriteExcelUtils writeExcelUtils;
    private List<String>dialogList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        initItemClick();
        return view;
    }

    private void initItemClick() {
        listSetting.setOnItemClickListener(this);
    }

    private void initView() {
        tvTitle.setText(getText(R.string.setting_title));
        writeExcelUtils=new WriteExcelUtils(getContext());
        stringList=new ArrayList<>();
//        stringList.add(getString(R.string.importExcel));
        stringList.add(getString(R.string.wifiSet));
        stringList.add(getString(R.string.setting_delete));
//        stringList.add(getString(R.string.ftpsetting));
//        stringList.add(getString(R.string.upload));
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,stringList);
        listSetting.setAdapter( adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){
//            case 0: //导出Excel
//                writeExcelUtils.initExcel();
//                break;
            case 0://WiFi设置
                startActivity(new Intent(getActivity(), WifiSettingActivity.class));
                break;

            case 1:
//                startActivity(new Intent(getActivity(), DeleteSettingActivity.class));
                dialogList = new ArrayList<>();
                dialogList.add(getString(R.string.forover_save));
                dialogList.add(getString(R.string.twoweek_delete));
                advancedDialog(dialogList);
                break;
//            case 2:
//                startActivity(new Intent(getContext(), FTPSettingActivity.class));
//                break;
//            case 1://数据上传
//               startActivity(new Intent(getContext(), UploadActivity.class));
//                break;
        }
    }
    /**
     * 自定义弹出框布局
     */
    public void advancedDialog(List stringList) {
        LinearLayout linearLayoutMain = new LinearLayout(getContext());//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(getContext());//this为获取当前的上下文
        listView.setFadingEdgeLength(0);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, stringList);
        listView.setAdapter(arrayAdapter);
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.setting_please_change)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(getContext().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int arg2, long l) {
                switch (arg2) {
                    case 0:
                        SPUtils.put(getContext(), Const.DELETE_KEY,1);//永久保留
                        break;
                    case 1:
                        SPUtils.put(getContext(), Const.DELETE_KEY,-1);//两周删除
                        break;
                    default:
                        break;
                }
                SouthUtil.showToast(getContext(),getString(R.string.wifiPass_save_success));
                dialog.cancel();
            }
        });
    }
}
