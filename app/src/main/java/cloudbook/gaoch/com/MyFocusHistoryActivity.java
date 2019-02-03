package cloudbook.gaoch.com;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import MyClass.Blur;
import MyClass.MyAdapter.FocusItemAdapter;
import MyClass.Utility.FocusTime;
import MyClass.Utility.LocalDatabaseHelper;

public class MyFocusHistoryActivity extends AppCompatActivity {
    public LocalDatabaseHelper dbHelper;
    List<FocusTime> focusTimesList;
    RecyclerView rv;
    FocusItemAdapter adapter;
    Button btn_back;
    LinearLayout layout_main,layout_1;
    private int hasBlured_top1=0,hasBlured_top2=0,hasBlured_top3=0,hasBlured_top4=0;
    public String bg_path;
    public Drawable bgPNG;
    public int primaryColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_focus_history);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        layout_main=findViewById(R.id.myfocus_layout_main);
        layout_1=findViewById(R.id.myfocus_layout_1);
        dbHelper=new LocalDatabaseHelper(this,ConstValue.LocalDatabaseName,null,LocalDatabaseHelper.NEW_VERSION);
        rv=findViewById(R.id.myfocus_rv);
        refreshFocus();
        adapter=new FocusItemAdapter(focusTimesList,this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        btn_back=findViewById(R.id.myfocus_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        setBg();
        setBlur();

    }

    void refreshFocus(){
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        String user_id=getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE).getString(ConstValue.databaseUserId,"");
        Cursor cursor = db.query(ConstValue.LocalDB_TABLE_FOCUS,null,"userid=?", new String[] { user_id },null,null,null,null);
        focusTimesList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                String bookid=cursor.getString(cursor.getColumnIndex("bookid"));
                String userid=cursor.getString(cursor.getColumnIndex("userid"));
                String bookName=cursor.getString(cursor.getColumnIndex("bookname"));
                Long start=cursor.getLong(cursor.getColumnIndex("start_time"));
                Long end=cursor.getLong(cursor.getColumnIndex("end_time"));
                //Long time = cursor.getLong(cursor.getColumnIndex("date"));
                int allSeconds= (int) ((end-start)/1000)*30;
                int minute=allSeconds/60;
                int second=allSeconds=minute*60;
                FocusTime focusTime = new FocusTime(0,minute,second);
                focusTime.setBookName(bookName);
                focusTime.setBookId(bookid);
                focusTime.date=new Date(start);
                focusTime.start_time=start;
                focusTime.end_time=end;
                focusTimesList.add(focusTime);
            }while (cursor.moveToNext());
        }
        List<Integer> minuteList=new ArrayList<>();
        if(focusTimesList.size()>7){
            for(int i=focusTimesList.size()-7;i<focusTimesList.size()-1;i++){
                minuteList.add(focusTimesList.get(i).getMinute());
            }
        }else if(focusTimesList.size()>0){
            for(int i=0;i<7-focusTimesList.size();i++){
                minuteList.add(0);
            }
            for(int i=0;i<focusTimesList.size()-1;i++){
                minuteList.add(focusTimesList.get(i).getMinute());
            }
        }else{
            for(int i=0;i<7;i++){
                minuteList.add(0);
            }
        }
        Log.e("cloudbook","focuse_size:"+user_id+",+"+focusTimesList.size());

        //lineView.addDots(minuteList);
        cursor.close();
        db.close();
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
            layout_1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    layout_1.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top1){
                        Blur.blur(view_test, layout_1,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top1=location[1];
                    }

                    return true;
                }
            });

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
