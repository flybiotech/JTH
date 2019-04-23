package comvoice.example.zhangbin.startimage.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.sp.SPUtils;

public class DeleteUtils {
    private Context mContext;


    public DeleteUtils(Context mContext){
        this.mContext = mContext;
    }
    /**
     * 自定义弹出框布局
     */
    public void advancedDialog(List stringList) {
        LinearLayout linearLayoutMain = new LinearLayout(mContext);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(mContext);//this为获取当前的上下文
        listView.setFadingEdgeLength(0);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, stringList);
        listView.setAdapter(arrayAdapter);
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.setting_please_change)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(mContext.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
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
                        SPUtils.put(mContext, Const.DELETE_KEY,1);//永久保留
                        break;
                    case 1:
                        SPUtils.put(mContext, Const.DELETE_KEY,-1);//两周删除
                        break;
                    default:
                        break;
                }
                SouthUtil.showToast(mContext,mContext.getString(R.string.wifiPass_save_success));
                dialog.cancel();
            }
        });
    }


}
