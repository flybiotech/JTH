package comvoice.example.zhangbin.startimage.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import comvoice.example.zhangbin.startimage.R;
import comvoice.example.zhangbin.startimage.model.User;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Created by zhangbin on 2018/6/26.
 */

public class WriteExcelUtils {
    private Context context;
    private LoadingDialog loadingDialog;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case -1:
                    Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(context, "Excel表生成成功", Toast.LENGTH_SHORT).show();
                    break;
            }
            if(loadingDialog!=null){
                loadingDialog.dismiss();
            }
        }
    };
    public WriteExcelUtils(Context context){
        this.context=context;
    }
    /**
     * 将医生的操作记录写入到Excel表中
     */
    public void initExcel(){
        if(loadingDialog==null){
            loadingDialog=new LoadingDialog(context);
        }
        loadingDialog.setMessage("正在生成Excel文件");
        loadingDialog.dialogShow();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message=handler.obtainMessage();
                List<User>userList= LitePal.findAll(User.class);
                if(userList.size()==0){
                    message.what=-1;
                }else {
                    //设置每列的标题
                    String []title={context.getString(R.string.hospital),context.getString(R.string.dName),
                            context.getString(R.string.dDate),context.getString(R.string.name),context.getString(R.string.RequiredHPV),
                            context.getString(R.string.RequiredCytology)};
                    String fileDicPath= Environment.getExternalStorageDirectory()+"/FLY_Image/操作记录.xls";

                    try {
                        File file=new File(fileDicPath);
                        if(!file.exists()){
                            file.createNewFile();
                        }
                        WritableWorkbook workbook;//创建excel工作簿
                        OutputStream os=new FileOutputStream(fileDicPath);
                        workbook= Workbook.createWorkbook(os);
                        WritableSheet sheet=workbook.createSheet("操作记录",0);//添加第一个工作表并设置第一个sheet的名字
                        Label label;
                        for(int i=0;i<title.length;i++){
                            //Label(x,y,x)代表单元格的第x+1列，第y+1行，内容z
                            //在Label对象的子对象中指明单元格的位置和内容
                            label =new Label(i,0,title[i]);
                            label=new Label(i,0,title[i],getHeader());
                            //将定义好的单元格添加到工作表中
                            sheet.addCell(label);
                        }
                        /**
                         * 下面是填充数据
                         * 保存数字到单元格，需要使用jxl.write.Number
                         * 必须使用其完整路径，否则会出现错误
                         */
                        for(int i=0;i<userList.size();i++){
                            //填充医院
                            label=new Label(0,i+1,userList.get(i).getHospital());
                            sheet.addCell(label);
                            //填充医生
                            label=new Label(1,i+1,userList.get(i).getdPhone());
                            sheet.addCell(label);
                            //填充日期
                            label=new Label(2,i+1,userList.get(i).getData());
                            sheet.addCell(label);
                            //填充患者
                            label=new Label(3,i+1,userList.get(i).getName());
                            sheet.addCell(label);
                            //填充hpv
                            label=new Label(4,i+1,userList.get(i).getHPV());
                            sheet.addCell(label);
                            //填充tct
                            label=new Label(5,i+1,userList.get(i).getTCT());
                            sheet.addCell(label);
                        }
                        workbook.write();
                        workbook.close();
                        message.what=1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        handler.sendMessage(message);
                    }

                }

            }
        }).start();

    }
    //Excel样式
    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD);//定义字体
        WritableCellFormat format = null;
        try {
            font.setColour(Colour.BLACK);
            format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);//左右居中
            format.setVerticalAlignment(VerticalAlignment.CENTRE);//上下居中
            format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//黑色边框
            format.setBackground(Colour.YELLOW);//黄色背景
        } catch (Exception e) {
            Log.e("样式异常", e.getMessage().toString());
        }
        return format;
    }

}
