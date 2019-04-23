package comvoice.example.zhangbin.startimage.utils;

import android.content.Context;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bigkoo.convenientbanner.listener.OnPageChangeListener;
import com.bumptech.glide.Glide;

import org.litepal.LitePal;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.User;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DetailsUtils {
    private Context context;
    StringBuilder sb1 = new StringBuilder();
    User user;
    private List<String>stringList;//图片集合
    List<Integer> listAllLength1 = new ArrayList<>();
    List<Integer> startList1 = new ArrayList<>();
    List<Integer> endList1 = new ArrayList<>();
    List<User>userList;
    private TextView tv_show;
    private List<String> imageAll;//所有图片的集合
    private List<String> videoPathlist;//视频地址集合
    private ConvenientBanner convenientBanner;//轮播
    private TextView tv_imagenameshow01;//图片展示
    private int imageSize = 0;
    public DetailsUtils(Context context,TextView tv_show,List<User>userList){
        this.context=context;
        this.tv_show=tv_show;
        this.userList=userList;
    }

    public DetailsUtils(Context context,ConvenientBanner convenientBanner,TextView textView){
        this.context = context;
//        this.listMessage = listMessage;

        this.convenientBanner = convenientBanner;
        this.tv_imagenameshow01 = textView;
    }

    //将详细信息归结到一个字符串中
    public void initDetils(TextView textView,List<User>userList){
        tv_show = textView;
        if(userList!=null){
            sb1.delete(0,sb1.length());
            listAllLength1.clear();
            startList1.clear();;
            endList1.clear();
            sb1.append(context.getString(R.string.screenId)+" : "+userList.get(0).getScreenId()+"\n\n");
            startList1.add(0);
            endList1.add(context.getString(R.string.screenId).length());
            listAllLength1.add(sb1.toString().length());

            sb1.append(context.getText(R.string.pName)+" : "+userList.get(0).getName()+"\n\n");
            startList1.add(listAllLength1.get(0));
            endList1.add(listAllLength1.get(0)+context.getText(R.string.pName).length());
            listAllLength1.add(sb1.toString().length());

            sb1.append(context.getText(R.string.age)+" : "+userList.get(0).getAge()+"\n\n");
            startList1.add(listAllLength1.get(1));
            endList1.add(listAllLength1.get(1)+context.getText(R.string.age).length());
            listAllLength1.add(sb1.toString().length());

            sb1.append(context.getText(R.string.RequiredPhone)+" : "+userList.get(0).getPhone()+"\n\n");
            startList1.add(listAllLength1.get(2));
            endList1.add(listAllLength1.get(2)+context.getText(R.string.RequiredPhone).length());
            listAllLength1.add(sb1.toString().length());
//
//            sb1.append(context.getText(R.string.RequiredID)+" : "+userList.get(0).getpID()+"\n\n");
//            startList1.add(listAllLength1.get(1));
//            endList1.add(listAllLength1.get(1)+context.getText(R.string.RequiredID).length());
//            listAllLength1.add(sb1.toString().length());

            sb1.append(context.getText(R.string.hpv_patient)+" : "+userList.get(0).getHPV()+"\n\n");
            startList1.add(listAllLength1.get(3));
            endList1.add(listAllLength1.get(3)+context.getText(R.string.hpv_patient).length());
            listAllLength1.add(sb1.toString().length());

//            sb1.append(context.getText(R.string.tct_patient)+" : "+userList.get(0).getTCT()+"\n\n");
//            startList1.add(listAllLength1.get(3));
//            endList1.add(listAllLength1.get(3)+context.getText(R.string.tct_patient).length());
//            listAllLength1.add(sb1.toString().length());

            tv_show.setText(AlignedTextUtils.addConbine1(sb1.toString(),startList1,endList1));
        }
    }
    public void initView(String id,OnShowContentListener listener){
        if (null == listener){
            return;
        }
        if(null == userList){
            userList = new ArrayList<>();
        }else {
            userList.clear();
        }

        rx.Observable.create(new Observable.OnSubscribe<List<User>>() {
            @Override
            public void call(Subscriber<? super List<User>> subscriber) {
                userList= LitePal.where("screenId = ?",id).find(User.class);
                subscriber.onNext(userList);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<User>>() {
                    @Override
                    public void call(List<User> s) {
                        listener.initView(userList);
                    }
                });
    }
    public void startImageShow(User msg, String ytType,String csbType,String dyType,OnShowContentListener listener) {
        if (listener==null||msg==null)return;

        if (imageAll == null) {
            imageAll = new ArrayList<String>();
        } else {
            imageAll.clear();
        }



        Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                String imageFilePath = getImageFilePath(msg);

                List<String> ytPath = getAllImageAbsolutePath(ytType,imageFilePath);
                imageAll.addAll(ytPath);

                List<String> csbPath = getAllImageAbsolutePath(csbType,imageFilePath);
                imageAll.addAll(csbPath);

                List<String> dyPath = getAllImageAbsolutePath(dyType,imageFilePath);
                imageAll.addAll(dyPath);


                subscriber.onNext(imageAll);


            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> s) {
                        listener.showImage(s);


                    }
                });




    }
    private String  getImageFilePath(User msg) {

        String path = Const.fromPath+msg.getScreenId()+"/";
        File mFile = new File(path);

//        if (!mFile.exists()) {
//            //重命名之后的文件夹名称
//            path = Environment.getExternalStorageDirectory() + "/ScreenApp/图片和视频/" + msg.getTaskNumber();
//
//        }
//        Log.i("TAG_1_path", "imageShow1: path= "+path+" , imageType = "+imageType
//                +" 存在 ： "+mFile.exists()+ " , ScreeningId() = "+msg.getScreeningId()+" ,TaskNumber() = "+msg.getTaskNumber());
//
//        path = path+"/" + imageType;
        return path;


    }
    List<String> listImagePath = null;
    private List<String> getAllImageAbsolutePath( String imageType,String path1) {
        if (listImagePath == null) {
            listImagePath = new ArrayList<String>();
        } else {
            listImagePath.clear();
        }
        String path = path1 + "/" + imageType;
        Log.i("TAG_1", "getAllImageAbsolutePath: path = "+path);
        File mFile = new File(path);

        if (mFile.exists()) {

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
            if (files != null && files.length > 0) {
                for (File file1 : files) {
                    if (file1.getName().endsWith(".jpg")) ;
                    listImagePath.add(file1.getAbsolutePath());
                }
            }

        }
        return listImagePath;
    }

    /**
     * 图片翻页监视
     */
    public void lunbo(List<String>list, OnItemClickListener onItemClickListener) {
        imageSize = list.size();
//        List<String> list = ImageShow();
        //自定义你的Holder，实现更多复杂的界面，不一定是图片翻页，其他任何控件翻页亦可。
//        convenientBanner.setPages(new CBViewHolderCreator<LocalImageHolderView>() {
//            @Override
//            public LocalImageHolderView createHolder() {
//
//                return new LocalImageHolderView();
//            }
//        }, list)
//                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
//                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
//                //设置指示器的方向
//                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);

//        convenientBanner = (ConvenientBanner)header.findViewById(R.id.convenientBanner) ;

//        loadTestDatas();
        //本地图片例子
        convenientBanner.setPages(
                new CBViewHolderCreator() {
                    @Override
                    public LocalImageHolderView createHolder(View itemView) {
                        return new LocalImageHolderView(itemView);
                    }

                    @Override
                    public int getLayoutId() {
                        return R.layout.item_localimage;
                    }
                }, list)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                .setOnItemClickListener(onItemClickListener);

    }

    /**
     * 轮播回调接口
     */
    class LocalImageHolderView extends Holder<String> {
        private ImageView imageView;

        public LocalImageHolderView(View itemView) {
            super(itemView);
        }

        @Override
        protected void initView(View itemView) {
            imageView =itemView.findViewById(R.id.ivPost);
        }

        @Override
        public void updateUI(String data) {
            convenientBanner.setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                }

                @Override
                public void onPageSelected(int index) {
                    if (imageAll != null) {

                        tv_imagenameshow01.setText("图片展示 ： " + new File(imageAll.get(index)).getName());
                    }

                }
            });

            Glide.with(context).load(data)
                    .into(imageView);
        }


//        @Override
//        public View createView(Context context) {
//            imageView = new ImageView(context);
//            imageView.setScaleType(ImageView.ScaleType.CENTER);
//            return imageView;
//        }
//
//        @Override
//        public void UpdateUI(Context context, final int position, final String data) {
////            final List<String> list = ImageShow();
//            convenientBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                }
//
//                @Override
//                public void onPageSelected(int position) {
//                    Log.i("TAG_11", "onPageSelected: position = "+position);
//                    if (imageAll!=null)
//                        tv_imagenameshow01.setText("图片展示 ： " + new File(imageAll.get(position)).getName());
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int state) {
//
//                }
//            });
//            Glide.with(context).load(data)
//                    .into(imageView);
//        }


    }






    public int  setCurrentPosition(int pos) {
        if (pos >= imageSize) {
            return 1;
        } else if (pos<0) {
            return 2;
        }
        convenientBanner.setCurrentItem(pos,true);
        return 3;
    }




    public int getCurrentPosition() {
        if (convenientBanner == null) return 0;
        return convenientBanner.getCurrentItem();
    }


    public interface OnShowContentListener{

        void initView(List<User> listMsg);

        void showImage(List<String>listImagePath);

        void showVideo(List<String> listVideoPath );




    }
}
