package comvoice.example.zhangbin.startimage.fragment;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import comvoice.example.zhangbin.startimage.activity.FTPSettingActivity;
import comvoice.example.zhangbin.startimage.activity.UploadActivity;
import comvoice.example.zhangbin.startimage.activity.WifiSettingActivity;
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
//            case 2:
//                startActivity(new Intent(getContext(), FTPSettingActivity.class));
//                break;
//            case 1://数据上传
//               startActivity(new Intent(getContext(), UploadActivity.class));
//                break;
        }
    }
}
