package comvoice.example.zhangbin.startimage.utils;

import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.User;

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
    public DetailsUtils(Context context,TextView tv_show,List<User>userList){
        this.context=context;
        this.tv_show=tv_show;
        this.userList=userList;
    }
    //将详细信息归结到一个字符串中
    public void initDetils(){
        if(userList!=null){
            sb1.delete(0,sb1.length());
            listAllLength1.clear();
            startList1.clear();;
            endList1.clear();
            sb1.append(context.getString(R.string.screenId)+" : "+userList.get(0).getScreenId()+"\n\n");
            startList1.add(0);
            endList1.add(context.getString(R.string.screenId).length());
            listAllLength1.add(sb1.toString().length());

            sb1.append(context.getText(R.string.pName)+" : "+userList.get(0).getPhone()+"\n\n");
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
}
