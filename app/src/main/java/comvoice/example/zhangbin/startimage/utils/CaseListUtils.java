package comvoice.example.zhangbin.startimage.utils;

import android.content.Context;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import comvoice.example.zhangbin.startimage.activity.ImageShowActivity;

/**
 * Created by zhangbin on 2018/4/27.
 */

public class CaseListUtils {
    private Context context;

    private List<String> YTlist;//原图集合
    private List<String> CSBlist;//醋酸白
    private List<String> DYlist;//碘油
    private List<String> imagList;//此集合不分类型，可以存储任何一种类型的图片
    private List<String> imageAll;//所有图片的集合
    public CaseListUtils(Context context){
        this.context=context.getApplicationContext();
    }

    /**
     * 查询出需要展示的图片的集合，顺序为原图、醋酸白、碘油
     */
    public List ImageShow(){
        String screenid = new FileUtils(context).initGetSP();
        String path = Const.fromPath+screenid+"/";
        if(!new File(path).exists()){
            return null;
        }
        YTlist=new ArrayList<>();
        CSBlist=new ArrayList<>();
        DYlist=new ArrayList<>();
        imageAll=new ArrayList<>();
        imageAll.clear();
        YTlist.clear();
        CSBlist.clear();
        DYlist.clear();

        String YTpath=path + "原图/" ;
        String CSBpath=path + "醋酸白/" ;
        String DYpath=path + "碘/" ;

        YTlist.addAll(ImageSelect(YTpath));
        CSBlist.addAll(ImageSelect(CSBpath));
        DYlist.addAll(ImageSelect(DYpath));

        imageAll.addAll(YTlist);
        imageAll.addAll(CSBlist);
        imageAll.addAll(DYlist);
        return imageAll;
    }
    /**
     * 根据传递的路径进行图片查询
     */
    private List ImageSelect(String path){
        File file=new File(path);
        imagList=new ArrayList<>();
        if(file.exists()){
            File[] files = new File(path).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    boolean a = false;
                    if (name.endsWith(".jpg")) {//所有.jpg格式的文件添加到数组中
                        a = true;
                    }
                    return a;
                }
            });
            if(files!=null&&files.length>0){
                for(File file1:files){
                    if(file1.getName().endsWith(".jpg"));
                    imagList.add(file1.getAbsolutePath());
                }
            }

        }
       return imagList;
    }

}
