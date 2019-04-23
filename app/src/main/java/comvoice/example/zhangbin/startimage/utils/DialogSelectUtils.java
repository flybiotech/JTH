package comvoice.example.zhangbin.startimage.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.activity.ImageShowActivity;
import comvoice.example.zhangbin.startimage.manager.SelectionManager;
import comvoice.example.zhangbin.startimage.sp.SPUtils;
import comvoice.example.zhangbin.startimage.wifiinfo.WifiConnectManager;

public class DialogSelectUtils {
    private ListView listView=null;
    private AlertDialog select_dialog = null;
    private Context mContext;
    private ArrayAdapter arrayAdapter;
    List<String>show_list;
    private WifiConnectManager.WifiConnectListener mListenner;
    public DialogSelectUtils(Context context,WifiConnectManager.WifiConnectListener listener){
        this.mContext=context;
        mListenner = listener;
    }
    public void showDialog(){

        show_list = new ArrayList<>();
        show_list.add(mContext.getString(R.string.fragment_getImage));
        show_list.add(mContext.getString(R.string.see_image));
        show_list.add(mContext.getString(R.string.fragment_updataImage));

        listView=new ListView(mContext);
        LinearLayout linearLayoutMain = new LinearLayout(mContext);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        listView.setFadingEdgeLength(0);
        listView.setFocusableInTouchMode(true);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        arrayAdapter=new ArrayAdapter(mContext,android.R.layout.simple_list_item_1,show_list);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        select_dialog=new AlertDialog.Builder(mContext).setTitle("请选择(筛查ID："+getSP()+")").setView(linearLayoutMain).setPositiveButton("取消",null).create();
        select_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button bt_cancel=select_dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (select_dialog != null) {
                            select_dialog.dismiss();
                        }
                    }
                });
            }
        });
        select_dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        select_dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: // 连接 主机wifi， 跳转到主机
                        if(dialogSelect != null){
                            dialogSelect.getIndex(0);
                        }
                        break;

                    case 1:
                        if(dialogSelect != null){
                            dialogSelect.getIndex(1);
                        }
                        break;
                    case 2:
                        if(dialogSelect != null){
                            dialogSelect.getIndex(2);
                        }
                        break;
                        default:break;
                }

                if (select_dialog != null) {
                    select_dialog.dismiss();
                }
            }
        });
    }
    //取消选择框
    public void dismissDialog(){
        if(null != select_dialog && select_dialog.isShowing()){
            select_dialog.dismiss();
        }
    }


    /**
     * 保存成功后更新本地存储的screenid，用做复制的目标路径
     */
    private void initSaveSP () {
        SPUtils.put(mContext, Const.SCREENID_KEY, Const.SPscreenId);
    }

    /**
     * 得到本地保存的screenid
     */
    private String getSP(){
        return (String) SPUtils.get(mContext,Const.SCREENID_KEY,"");
    }

    public interface DialogSelect{
        void getIndex(int type);
    }

    static DialogSelect dialogSelect;

    public static void setDialogSelectListener(DialogSelect dialogSelectListener){
        dialogSelect = dialogSelectListener;
    }











}
