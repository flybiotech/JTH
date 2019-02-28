package comvoice.example.zhangbin.startimage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.ListMessage;
import comvoice.example.zhangbin.startimage.model.User;

public class MessageAdapter extends BaseAdapter{
    private Context context;
    private List<ListMessage> userList;
    public MessageAdapter(Context context,List<ListMessage>userList){
        this.context=context;
        this.userList=userList;
    }
    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if(view==null){
            viewHolder=new ViewHolder();
            view= LayoutInflater.from(context).inflate(R.layout.list_message,null);
            viewHolder.tv_name=view.findViewById(R.id.tv_name);
            viewHolder.tv_phone=view.findViewById(R.id.tv_phone);
            viewHolder.tv_screenId=view.findViewById(R.id.tv_screenId);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.tv_name.setText(userList.get(i).getName());
        viewHolder.tv_phone.setText(userList.get(i).getPhone());
        viewHolder.tv_screenId.setText(userList.get(i).getScreenId());
        return view;
    }
    class ViewHolder{
        TextView tv_name,tv_phone,tv_screenId;
    }
}
