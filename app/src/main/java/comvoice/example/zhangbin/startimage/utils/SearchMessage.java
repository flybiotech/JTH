package comvoice.example.zhangbin.startimage.utils;



import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

import comvoice.example.zhangbin.startimage.model.ListMessage;
import comvoice.example.zhangbin.startimage.model.User;

public class SearchMessage {
    private List<User>listMessages;//数据库集合
    private List<ListMessage> addmessageList;//listview数据源

    /**
     * 查询数据库数据，并将指定字段查询、添加的List_message对象中，作为listview 的数据源
     */
    public List addData(){
        addmessageList=new ArrayList<>();
        listMessages= LitePal.findAll(User.class);
        for(int i=0;i<listMessages.size();i++){
            ListMessage addmessage=new ListMessage();
            addmessage.setName(listMessages.get(i).getName());
            addmessage.setPhone(listMessages.get(i).getPhone());
            addmessage.setScreenId(listMessages.get(i).getScreenId());
            addmessageList.add(addmessage);
        }
        return addmessageList;
    }

    /**
     * 根据输入的查询条件进行模糊查询
     */
    public List vagueSelect(final String condition){
        addmessageList=new ArrayList<>();

        listMessages= LitePal.findAll(User.class);

        for(int i=0;i<listMessages.size();i++){
            if(listMessages.get(i).getPhone().contains(condition)){
                ListMessage addmessage=new ListMessage();
                addmessage.setName(initHideName(listMessages.get(i).getName()));
                addmessage.setPhone(initHidePhone(listMessages.get(i).getPhone()));
                addmessage.setScreenId(listMessages.get(i).getScreenId());
                addmessageList.add(addmessage);
            }
        }
        return addmessageList;
    }

    /**
     * 隐藏手机号中间4位
     */
    private String initHidePhone(String phone){
        if(!phone.equals("")){
            return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return "";
    }

    /**
     * 隐藏姓名
     */
    private String initHideName(String name){
        if(!name.equals("")){
            String surname=name.substring(0,1);
            String hidename=name.substring(1,name.length());
            for(int i=0;i<hidename.length();i++){
                surname=surname+"*";
            }
            return surname;
        }
        return "";
    }

}
