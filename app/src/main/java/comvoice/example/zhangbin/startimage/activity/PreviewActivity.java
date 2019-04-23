package comvoice.example.zhangbin.startimage.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.view.ZoomImageView;

public class PreviewActivity extends BaseActivity {
    Bitmap bp = null;
//    ZoomImageView imageview;//自定义imageview，可以随着手势放大或缩小
    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preview);
//        imageview = (ZoomImageView) findViewById(R.id.iv);
        photoView =findViewById(R.id.iv1);
        Intent intent = getIntent();
        String pathName = intent.getStringExtra("msg");
//        bp = BitmapFactory.decodeFile(pathName);//通过传递过来的路径转化为bitmap
//        photoView.setEnabled(true);
//        photoView.setImageBitmap(bp);

        Glide.with(getApplicationContext())//通过第三方框架展示传递过来的图片
                .load(pathName)
                .into(photoView);


        photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                finish();
            }
        });
    }







}
