package cloudbook.gaoch.com;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import MyClass.Blur;
import MyClass.UserMessage;
import MyClass.Utility.LogToFile;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityUserInfor extends AppCompatActivity {
    CircleImageView iv_userHead;
    TextView tv_id,tv_name,tv_exp,tv_books,tv_ach,tv_readtime;
    String bg_path;
    LinearLayout layout_main,layout_1,layout_2,layout_3,layout_4;
    Drawable bgPNG;
    Button btn_back;
    private int hasBlured_top1=0,hasBlured_top2=0,hasBlured_top3=0,hasBlured_top4=0;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfor);
        initView();
        setBg();
        setBlur();
        UserMessage userMessage= (UserMessage) getIntent().getSerializableExtra("USER");
        if(userMessage!=null){
            showUserInfor(userMessage);
        }else{
            showSelfInfor();
        }
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
        iv_userHead=findViewById(R.id.userInfor_userPic);
        layout_main=findViewById(R.id.userInfor_layout_main);
        layout_1=findViewById(R.id.userInfor_layout1);
        layout_2=findViewById(R.id.userInfor_layout2);
        layout_3=findViewById(R.id.userInfor_layout3);
        layout_4=findViewById(R.id.userInfor_layout4);

        tv_ach=findViewById(R.id.Userinfor_tv_ach);
        tv_id=findViewById(R.id.Userinfor_tv_id);
        tv_name=findViewById(R.id.Userinfor_tv_name);
        tv_exp=findViewById(R.id.Userinfor_tv_exp);
        tv_books=findViewById(R.id.Userinfor_tv_books);
        tv_readtime=findViewById(R.id.Userinfor_tv_readtime);

        btn_back=findViewById(R.id.userInfor_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
            layout_2.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    layout_2.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top2){
                        Blur.blur(view_test, layout_2,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top2=location[1];
                    }

                    return true;
                }
            });
            layout_3.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    layout_3.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top3){
                        Blur.blur(view_test, layout_3,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top3=location[1];
                    }

                    return true;
                }
            });
            layout_4.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    layout_4.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top4){
                        Blur.blur(view_test, layout_4,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top4=location[1];
                    }

                    return true;
                }
            });

        }
    }

    void showUserInfor(UserMessage message){
        tv_books.setText("已购书籍："+message.getBooks()+"本");
        tv_id.setText("id:"+message.getId());
        tv_readtime.setText("阅读总时长:"+message.getReadtime());
        tv_ach.setText("成就数量:"+message.getAchieve());
        int exp=message.getExp();
        int level=0;
        while (exp>0){
            exp=exp/10;
            level++;
        }
        tv_exp.setText("经验值:"+message.getExp()+"(Lv."+level+")");
        tv_name.setText("昵称:"+message.getUsername());
        RequestOptions options = new RequestOptions().placeholder(R.drawable.user_pic).error(R.drawable.user_pic).centerCrop();
        Glide.with(this).load(ConstValue.serverUserPic+message.getId()+".png")
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(iv_userHead);

    }

    void showSelfInfor(){
        SharedPreferences preferences=getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE);

        tv_books.setText("已购书籍："+preferences.getInt(ConstValue.databaseUserBooks,99)+"本");
        tv_id.setText("id:"+preferences.getString(ConstValue.databaseUserId,"??????"));
        tv_readtime.setText("阅读总时长:"+preferences.getInt(ConstValue.databaseUserRT,9999));
        tv_ach.setText("成就数量:"+preferences.getInt(ConstValue.databaseUserachi,99));
        int exp=preferences.getInt(ConstValue.databaseUserexp,999999);
        int level=0;
        while (exp>0){
            exp=exp/10;
            level++;
        }
        tv_exp.setText("经验值:"+preferences.getInt(ConstValue.databaseUserexp,999999)+"(Lv."+level+")");
        tv_name.setText("昵称:"+preferences.getString(ConstValue.databaseUserName,"御马亲征"));
        RequestOptions options = new RequestOptions().placeholder(R.drawable.user_pic).error(R.drawable.user_pic).centerCrop();
        Glide.with(this).load(ConstValue.serverUserPic+preferences.getString(ConstValue.databaseUserId,"111111")+".png")
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(iv_userHead);

    }

}
