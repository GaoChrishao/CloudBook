package cloudbook.gaoch.com;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;

import MyClass.Blur;
import MyClass.Book;
import MyClass.CommentList;
import MyClass.CommentPermission;
import MyClass.Utility.LocalDatabaseHelper;
import MyClass.Write;

public class WriteActivity extends AppCompatActivity {
    private Button btn;
    private EditText ed_id,ed_name,ed_content;
    private LocalDatabaseHelper dbHlper;
    public String bg_path;
    public Drawable bgPNG;
    public int primaryColor;
    Write write;
    private boolean isUpdate;
    int type;
    RelativeLayout layout_main;
    private int hasBlured_top1=0,hasBlured_top2=0,hasBlured_top3=0,hasBlured_top4=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        initView();

        Intent intent = getIntent();
        if(intent!=null){
            type=intent.getIntExtra("type",1);
            Bundle bundle = intent.getBundleExtra("write");
            if(bundle!=null){write = (Write)bundle.getSerializable("write"); }
        }
        if(type==1){
            //更新操作
            isUpdate=true;
            ed_id.setVisibility(View.GONE);
            //已经存在的笔记
            if(write!=null){
                ed_id.setText(write.getBook_id());
                ed_content.setText(write.getContent());
                ed_name.setText(write.getBook_name());
                ed_name.setFocusable(false);
            }
        }else if(type==2){
            //自由笔记
            ed_id.setText("-1");
            ed_id.setVisibility(View.GONE);
        }else if(type==3){
            //扫码笔记
            ed_id.setText(intent.getStringExtra("bookid"));
            ed_name.setText(intent.getStringExtra("bookname"));
            ed_id.setFocusable(false);
            ed_name.setFocusable(false);
        }


        setBg();
        //setBlur();



    }


    void initView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        btn=findViewById(R.id.write_btn_yes);
        ed_id=findViewById(R.id.write_et_bookid);
        ed_name=findViewById(R.id.write_et_bookname);
        ed_content=findViewById(R.id.write_et_content);
        layout_main=findViewById(R.id.write_layout_main);


        dbHlper=new LocalDatabaseHelper(this,ConstValue.LocalDatabaseName,null,LocalDatabaseHelper.NEW_VERSION);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookid=ed_id.getText().toString()+"";
                String bookName=ed_name.getText().toString()+"";
                String content=ed_content.getText().toString()+"";
                if(content.equals("")){
                    Toast.makeText(WriteActivity.this, "内容为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(bookName.equals("")){
                    Toast.makeText(WriteActivity.this, "书籍名称为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isUpdate){
                    write.content=content;
                }else{
                    write=new Write(bookid,bookName,new Date(System.currentTimeMillis()),content);
                    write.user_id=getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE).getString(ConstValue.databaseUserId,"0");
                }
                Toast.makeText(WriteActivity.this, "上传中。。。", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Socket socket = new Socket(ConstValue.serverIp,ConstValue.serverPortWriter);
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            out.writeObject(write);
                            out.flush();
                            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                            Write write_get= (Write) in.readObject();
                            Message msg = new Message();
                            if(write_get!=null){
                                write.id=write_get.id;
                                msg.what=1;
                            }else{
                                msg.what=2;
                            }
                            socket.close();
                            myhandle.sendMessage(msg);
                        }catch (IOException e){
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        });
    }



    /**
     *初始背景图片
     */
    public void setBg(){
        SharedPreferences configPref = getSharedPreferences(ConstValue.getConfigDataName(), MODE_PRIVATE);
        final String path = configPref.getString(ConstValue.getIsBackGroundPNG(),"");
        if(path!=""){
            bg_path = path;
            Log.d("WriteActivity","读取到图片地址");
            try{
                bgPNG = Drawable.createFromPath(path);
                layout_main.setBackground(bgPNG);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "读取背景图片错误！请重启软件后重新选择一张背景图片！", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences(ConstValue.getConfigDataName(),MODE_PRIVATE).edit();
                editor.putString(ConstValue.getIsBackGroundPNG(),"");
                editor.apply();
                return;
            }

        }
    }

    Handler myhandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    SQLiteDatabase db =dbHlper.getWritableDatabase();
                    if(type==2||type==3){
                        //插入笔记
                        ContentValues value = new ContentValues();
                        value.put("id",write.id);
                        value.put("bookid",write.book_id);
                        value.put("bookname",write.book_name);
                        value.put("content",write.content);
                        value.put("userid",write.user_id);
                        value.put("time",write.date.toString());
                        db.insert(ConstValue.LocalDB_TABLE_WRITE,null,value);
                        Toast.makeText(WriteActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                        db.close();
                    }else{
                        //type==1 更新笔记
                        String sql = "UPDATE "+ConstValue.LocalDB_TABLE_WRITE+" set bookname='"+write.book_name+"',content='"+write.content+"' where id="+write.getId()+";";
                        db.execSQL(sql);
                        db.close();
                        Toast.makeText(WriteActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    Write reWrite=new  Write(write.book_id,write.book_name,write.date,write.content);
                    reWrite.id=write.id;
                    bundle.putSerializable("WRITE",reWrite);
                    intent.putExtras(bundle);
                    setResult(2,intent);  //返回的结果码
                    finish();
                    //Toast.makeText( WriteActivity.this, "云备份成功！", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText( WriteActivity.this, "云备份失败！", Toast.LENGTH_SHORT).show();
                    break;

            }

        }
    };

    /**
     * 是否开启毛玻璃
     * @return
     */
    public boolean getIsBlur(){
        SharedPreferences configPref = getSharedPreferences(ConstValue.getConfigDataName(), MODE_PRIVATE);
        boolean is = configPref.getBoolean(ConstValue.isBlur, true);
        return is;

    }

    public void setBlur(){
        final View view_test=layout_main;
        if(getIsBlur()){
            ed_name.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    ed_name.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top1){
                        Blur.blur(view_test, ed_name,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top1=location[1];
                    }

                    return true;
                }
            });
            ed_content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    ed_content.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top2){
                        Blur.blur(view_test, ed_content,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top2=location[1];
                    }

                    return true;
                }
            });

            ed_id.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    ed_id.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top3){
                        Blur.blur(view_test, ed_id,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top3=location[1];
                    }

                    return true;
                }
            });



        }
    }






}
