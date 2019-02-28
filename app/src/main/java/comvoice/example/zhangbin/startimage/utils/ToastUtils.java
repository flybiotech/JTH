package comvoice.example.zhangbin.startimage.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by dell on 2018/4/24.
 */

public class ToastUtils {

    private static Toast mToast;

    public static void showToast(Context mContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);

        }
        mToast.show();
    }




}
