package comvoice.example.zhangbin.startimage.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lcw.library.imagepicker.manager.ConfigManager;
import com.lcw.library.imagepicker.view.PinchImageView;

import java.util.List;

import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.utils.GlideLoader;

public class ImagePreViewAdapter extends PagerAdapter {

    private Context mContext;
    private List<String>strings;
    GlideLoader glideLoader;

    public ImagePreViewAdapter (Context mContext,List<String>strings){
        this.mContext = mContext;
        this.strings = strings;
        glideLoader = new GlideLoader();
    }
    @Override
    public int getCount() {
        return strings == null ? 0 : strings.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_viewpager_image,null);
        PinchImageView imageView = view.findViewById(R.id.iv_item_image);
        try {
            glideLoader.loadPreImage(imageView, strings.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.addView(view);

        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
