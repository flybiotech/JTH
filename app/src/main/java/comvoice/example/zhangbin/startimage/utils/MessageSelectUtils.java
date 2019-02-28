package comvoice.example.zhangbin.startimage.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;
import comvoice.example.zhangbin.startimage.activity.CaseListManagerActivity;
import comvoice.example.zhangbin.startimage.model.User;

/**
 * Created by zhangbin on 2018/4/27.
 * 这个类为信息管理界面查询的方法的工具类
 */

public class MessageSelectUtils {
    private Context context;
    private List<String> list2;//查询条件集合
    private EditText etScreenID,editName,editTel,edit_casesearch_01,edit_ID;
    private List<User> userCaseList = new ArrayList<>();//查询出符合条件的人员信息集合
    private List<User> haveCaseList;//已筛查人员
    private List<User> noCaseList;//未筛查人员
    boolean f1, f2, f3, f4;
    public MessageSelectUtils(Context context,EditText etScreenID, EditText etitName, EditText etitTel){
        this.context=context;
        this.etScreenID=etScreenID;
        this.editName=etitName;
        this.editTel=etitTel;
        list2=new ArrayList<>();
    }
    /**
     *将输入的查询条件归集到一个集合里，然后传递到下一个界面
     */
    public void getUserInfo() {
        list2.clear();
        //筛查id
        if (etScreenID.getText().toString().trim().equals("") || etScreenID.getText().toString().trim() == null) {
            f1 = false;
        } else {
            list2.add(etScreenID.getText().toString().trim());
            f1 = true;
        }
        //姓名
        if (editName.getText().toString().trim().equals("") || editName.getText().toString().trim() == null) {
            f1 = false;
        } else {
            list2.add(editName.getText().toString().trim());
            f1 = true;
        }
        //手机电话号码
        if (editTel.getText().toString().trim().equals("") || editTel.getText().toString().trim() == null) {
            f2 = false;
        } else {
            list2.add(editTel.getText().toString().trim());
            f2 = true;
        }

//        //身份证号
//        if (edit_ID.getText().toString().trim().equals("") || edit_ID.getText().toString().trim() == null) {
//            f4 = false;
//        } else {
//            list2.add(edit_ID.getText().toString().trim());
//            f4 = true;
//        }
        userCaseList =getUserCaseBySearch(f1, f2, f3,list2);
        haveCaseList=new ArrayList<>();
        noCaseList=new ArrayList<>();
        haveCaseList.clear();
        noCaseList.clear();
        if(userCaseList.size()>0){
            for(int i=0;i<userCaseList.size();i++){
                if(userCaseList.get(i).getIdentification().equals("0")){
                    noCaseList.add(userCaseList.get(i));
                }else if(userCaseList.get(i).getIdentification().equals("1")){
                    haveCaseList.add(userCaseList.get(i));
                }
            }
        }
        userCaseList.clear();
        userCaseList.addAll(haveCaseList);
        userCaseList.addAll(noCaseList);
        Log.e("筛查1",haveCaseList.size()+"");
        Log.e("筛查2",noCaseList.size()+"");
        Const.msgList.clear();
        Const.msgList.addAll(userCaseList);
        Intent intent = new Intent(context, CaseListManagerActivity.class);
        context.startActivity(intent);
    }

    /**
     *分会符合条件的人员集合
     */
    public  List<User> getUserCaseBySearch(boolean f1,boolean f2,boolean f3,List<String >list) {
        List<User> list1 = new ArrayList<User>();
        List<String> list3 = new ArrayList<String>();
        int i=0;
        if (f1) {
            i=i+1;
//            sBuilder.append("idNumber =?   and   ");
            list3.add("screenId like  ?");//筛查id
        }
        if (f2) {
            i=i+1;
            list3.add("name like  ?");//姓名
        }
        if (f3) {
            i=i+1;
            list3.add("phone like  ?");//电话
        }

//        if (f4) {
//            i=i+1;
//            list3.add("IDCard like  ?");//身份证号
//        }

        switch (i) {

            case 0://查询所有的患者的信息
                list1 = LitePal.where("name!=?","").find(User.class);
                break;
            case 1:
                list1 = LitePal.where(list3.get(0),"%"+list.get(0)+"%").find(User.class);

                break;
            case 2:

                list1 = LitePal.where(list3.get(0)+"  and  "+list3.get(1),"%"+list.get(0)+"%","%"+list.get(1)+"%").find(User.class);

                break;
            case 3:

                list1 = LitePal.where(list3.get(0)+"  and  "+list3.get(1)+"   and   "+list3.get(2), "%"+list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%").find(User.class);

                break;
            case 4:

                list1 = LitePal.where(list3.get(0)+"  and  "+list3.get(1)+"   and   "+list3.get(2)+"   and   "+list3.get(3), "%"+list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%").find(User.class);

                break;
        }
        return list1;

    }
}
