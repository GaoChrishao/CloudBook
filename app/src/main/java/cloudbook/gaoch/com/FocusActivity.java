package cloudbook.gaoch.com;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.sql.Date;

import MyClass.Blur;
import MyClass.Utility.FocusTime;
import MyClass.Utility.LocalDatabaseHelper;
import MyClass.Utility.LogToFile;

public class FocusActivity extends AppCompatActivity {

    private TextView tv_h,tv_m,tv_s,tv_name;
    private Button btn_start,btn_stop;
    private long startTime=0,endTime=0;
    private String bookid;
    private  String bookname;
    private  boolean hasStart=true;
    private FocusTime focusTime=null;
    private ImageView imageView;
    public LocalDatabaseHelper dbHelper;
    private String userid;
    LinearLayout layout_main,layout_1;
    private int hasBlured_top1=0,hasBlured_top2=0,hasBlured_top3=0,hasBlured_top4=0;
    public String bg_path;
    public Drawable bgPNG;
    public int primaryColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        initView();
        SharedPreferences preferences = getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE);
        userid= preferences.getString(ConstValue.databaseUserId,"");
        dbHelper=new LocalDatabaseHelper(this,ConstValue.LocalDatabaseName,null,LocalDatabaseHelper.NEW_VERSION);
        Intent intent =getIntent();
        bookid=intent.getStringExtra("bookid");
        bookname=intent.getStringExtra("bookname");
        if(bookname!=null&&bookname.length()>0){
            tv_name.setText("正在阅读:"+bookname);
        }else{
            tv_name.setVisibility(View.GONE);
        }

        FocusTime time = new FocusTime(preferences.getInt(ConstValue.focusHour,0),preferences.getInt(ConstValue.focusMinute,0),preferences.getInt(ConstValue.focusSecond,0));
        //tv_h.setText(time.getHour()+"h");
        if(time.getMinute()<10){
            tv_m.setText("0"+time.getMinute());
        }else{
            tv_m.setText(time.getMinute()+"");
        }
        if(time.getSecond()<10){
            tv_s.setText("0"+time.getSecond());
        }else{
            tv_s.setText(time.getSecond()+"");
        }
        setBg();
        setBlur();
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

        //tv_h=findViewById(R.id.focous_tv_h);
        tv_m=findViewById(R.id.focous_tv_m);
        tv_s=findViewById(R.id.focous_tv_s);
        btn_start=findViewById(R.id.focous_start);
        btn_stop=findViewById(R.id.focous_stop);
        imageView=findViewById(R.id.focus_pic);
        tv_name=findViewById(R.id.focus_bookname);
        layout_main=findViewById(R.id.focus_layout_main);
        layout_1=findViewById(R.id.focus_layout_1);



        RequestOptions options = new RequestOptions().placeholder(R.drawable.reading).error(R.drawable.reading).centerCrop();
        Glide.with(this).load(R.drawable.reading)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("FocusActivity","加载失败");
                        LogToFile.e("FocusActivity","加载图片失败");
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                }).into(imageView);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasStart)hasStart=true;
                startTime=System.currentTimeMillis();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (hasStart){
                            endTime=System.currentTimeMillis();
                            int tmp= (int) ((endTime-startTime)/1000);   //秒数
                            //int hour=tmp/(60*60);
                            int minute=tmp/60;
                            int second=tmp-minute*60;
                            Message msg = new Message();
                            msg.what=1;
                            Bundle bundle = new Bundle();
                            FocusTime time = new FocusTime(0,minute,second);
                            focusTime=time;
                            bundle.putSerializable("time",time);
                            msg.setData(bundle);
                            myhandler.sendMessage(msg);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasStart==true){
                   saveFocusInDB();
                }
                hasStart=false;

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(hasStart){
            saveFocusInDB();
        }
        hasStart=false;
        if(focusTime!=null){
            SharedPreferences.Editor editor = getSharedPreferences(ConstValue.spTime,MODE_PRIVATE).edit();
            //editor.putInt(ConstValue.focusHour,focusTime.getHour());
            editor.putInt(ConstValue.focusMinute,focusTime.getMinute());
            editor.putInt(ConstValue.focusSecond,focusTime.getSecond());
            editor.apply();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        hasStart=false;
        if(focusTime!=null){
            SharedPreferences.Editor editor = getSharedPreferences(ConstValue.spTime,MODE_PRIVATE).edit();
            //editor.putInt(ConstValue.focusHour,focusTime.getHour());
            editor.putInt(ConstValue.focusMinute,focusTime.getMinute());
            editor.putInt(ConstValue.focusSecond,focusTime.getSecond());
            editor.apply();
        }
        super.onDestroy();
    }

    Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    FocusTime time = (FocusTime) msg.getData().getSerializable("time");
                    //tv_h.setText(time.getHour()+"h");
                    if(time.getMinute()<10){
                        tv_m.setText("0"+time.getMinute());
                    }else{
                        tv_m.setText(time.getMinute()+"");
                    }
                    if(time.getSecond()<10){
                        tv_s.setText("0"+time.getSecond());
                    }else{
                        tv_s.setText(time.getSecond()+"");
                    }
                    break;
            }
        }
    };

    void saveFocusInDB(){
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        //插入笔记
        ContentValues value = new ContentValues();
        value.put("userid",userid);
        if(bookid!=null){
            value.put("bookid",bookid);
        }
        if(bookname!=null){
            value.put("bookname",bookname);
        }
        value.put("start_time",startTime);
        value.put("end_time",endTime);
        //value.put("date",endTime);
        db.insert(ConstValue.LocalDB_TABLE_FOCUS,null,value);
        db.close();
        Log.e("cloudbook","focus save:"+userid+" "+bookid+" "+bookname+" "+startTime+" "+endTime);
    }

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
//            layout_1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    int []location=new int[2];
//                    layout_1.getLocationInWindow(location);
//                    if(location[1]!=hasBlured_top1){
//                        Blur.blur(view_test, layout_1,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
//                        hasBlured_top1=location[1];
//                    }
//
//                    return true;
//                }
//            });

        }
    }

    /**
     *初始背景图片
     */
    public void setBg(){
        SharedPreferences configPref = getSharedPreferences(ConstValue.getConfigDataName(), MODE_PRIVATE);
        final String path = configPref.getString(ConstValue.getIsBackGroundPNG(),"");
        if(path!=""){
            bg_path = path;
            Log.d("MainActivity","读取到图片地址");
            try{
                bgPNG = Drawable.createFromPath(path);
                layout_main.setBackground(bgPNG);
                //primaryColor = colorFromBitmap( BitmapFactory.decodeFile(path));
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



}
