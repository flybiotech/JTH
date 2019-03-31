package comvoice.example.zhangbin.startimage.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.utils.AlignedTextUtils;
import comvoice.example.zhangbin.startimage.utils.MessageSelectUtils;

public class MessageFragment extends Fragment {

    //
//    @BindView(R.id.title_text)
//    TextView titleText;
//    @BindView(R.id.btn_left)
//    Button btnLeft;
//    @BindView(R.id.btn_right)
//    Button btnRight;
//    @BindView(R.id.rl)
//    RelativeLayout rl;
    @BindView(R.id.tv_casesearch_01)
    TextView tvCasesearch01;
    @BindView(R.id.edit_casesearch_screenid)
    EditText editCasesearchScreenid;
    @BindView(R.id.tv_casesearch_02)
    TextView tvCasesearch02;
    @BindView(R.id.edit_casesearch_Name)
    EditText editCasesearchName;
    @BindView(R.id.tv_casesearch_03)
    TextView tvCasesearch03;
    @BindView(R.id.edit_casesearch_tel)
    EditText editCasesearchTel;
    @BindView(R.id.btn_casesearch_search)
    Button btnCasesearchSearch;
    Unbinder unbinder;
    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_screen)
    ImageView ivScreen;
    @BindView(R.id.rl)
    RelativeLayout rl;
    private MessageSelectUtils messageSelectUtils;
    private String[] nameTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        nameTv = new String[]{getString(R.string.screenId), getString(R.string.pName), getString(R.string.phone)};
        TextView[] tvID = {tvCasesearch01, tvCasesearch02, tvCasesearch03};
        for (int i = 0; i < nameTv.length; i++) {
            tvID[i].setText(AlignedTextUtils.justifyString(nameTv[i], 4));
        }
        btnLeft.setVisibility(View.GONE);
//        btnRight.setVisibility(View.GONE);
        messageSelectUtils = new MessageSelectUtils(getContext(), editCasesearchScreenid, editCasesearchName, editCasesearchTel);
        tvTitle.setText(R.string.case_title);
        ivScreen.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_left, R.id.btn_casesearch_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_left:

                break;
            case R.id.btn_casesearch_search:
//                Toast.makeText(getActivity(), getString(R.string.patient_select_patients_message), Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        messageSelectUtils.getUserInfo();
                    }
                }).start();
                break;
            default:
                break;
        }
    }
}
