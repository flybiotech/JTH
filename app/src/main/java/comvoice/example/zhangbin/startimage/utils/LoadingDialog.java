package comvoice.example.zhangbin.startimage.utils;


import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import comvoice.example.zhangbin.startimage.R;


public class LoadingDialog {
    private Dialog loadingDialog;
    private TextView textView;
    public static LoadingDialog instance=null;
    public LoadingDialog(Context context){
        loadingDialog=new Dialog(context, R.style.myDialogTheme);
        loadingDialog.setContentView(R.layout.view_loadingdlg);
        loadingDialog.setCanceledOnTouchOutside(false);
        //用于平板的返回键是否起作用
//        loadingDialog.setCancelable(false);
        textView= (TextView) loadingDialog.findViewById(R.id.loading_message);
    }

    public LoadingDialog(Context context, boolean b){
        loadingDialog=new Dialog(context,R.style.myDialogTheme);
        loadingDialog.setContentView(R.layout.view_loadingdlg);
        loadingDialog.setCanceledOnTouchOutside(false);
        //用于平板的返回键是否起作用
        loadingDialog.setCancelable(b);
        textView= (TextView) loadingDialog.findViewById(R.id.loading_message);
    }

    /**
     *
     * @param message
     */
    public void setMessage(String message) {
        textView.setText(message);
    }
    /**
     *
     */
    public void dismiss(){
        loadingDialog.dismiss();
    }
    /**
     *
     */
    public void dialogShow(){
        loadingDialog.show();
    }

    public boolean isShow() {
        return loadingDialog.isShowing();
    }

    public static LoadingDialog getInstance(Context context) {
        synchronized (LoadingDialog.class) {
            if (instance == null) {
                instance = new LoadingDialog(context);
            }
        }
        return instance;
    }

    public static LoadingDialog getInstance(Context context,boolean cancle) {
        synchronized (LoadingDialog.class) {
            if (instance == null) {
                instance = new LoadingDialog(context,cancle);
            }
        }
        return instance;
    }

    public static LoadingDialog getInstance() {
        return instance;
    }

}

