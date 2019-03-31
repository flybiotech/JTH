package comvoice.example.zhangbin.startimage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.adapter.CaseManagerAdapter;
import comvoice.example.zhangbin.startimage.utils.Const;

public class CaseListManagerActivity extends AppCompatActivity {

    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.btn_right)
    Button btnRight;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.textview_contrast)
    TextView textviewContrast;
    @BindView(R.id.list_caselist_01)
    ListView listCaselist01;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    private CaseManagerAdapter caseManagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_case_list_manager);
        ButterKnife.bind(this);
        initView();
        initClickItem();
        initDate();
    }

    private void initDate() {
        if(Const.msgList.size()>0){
            tvEmpty.setText("");
        }
        caseManagerAdapter=new CaseManagerAdapter(CaseListManagerActivity.this, Const.msgList);
        listCaselist01.setAdapter(caseManagerAdapter);
    }

    private void initView() {
        titleText.setText(getString(R.string.msg_list));
        btnRight.setVisibility(View.GONE);
    }

    private void initClickItem() {
        listCaselist01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(CaseListManagerActivity.this,MessageDetailsActivity.class);
                intent.putExtra("message", Const.msgList.get(position).getScreenId());
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.btn_left)
    public void onViewClicked() {
        finish();
    }
}
