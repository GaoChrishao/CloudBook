package cloudbook.gaoch.com;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
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
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import MyClass.ActivationMessage;
import MyClass.Blur;
import MyClass.Book;
import MyClass.Utility.FTP;
import MyClass.Utility.LocalDatabaseHelper;
import MyClass.Utility.LogToFile;
import MyClass.Utility.Utility;
import MyClass.Write;
import MyClass.WriteList;
import cloudbook.gaoch.com.fragment.FragmentMain;
import cloudbook.gaoch.com.fragment.FragmentMyBook;
import cloudbook.gaoch.com.fragment.FragmentRank;
import cloudbook.gaoch.com.fragment.FragmentSetting;
import cloudbook.gaoch.com.fragment.FragmentUs;
import cloudbook.gaoch.com.fragment.FragmentWrite;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity  {
    public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
    public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
    public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";
    public static String userAccount;
    Write write;

    private static final int REQUEST_CODE_SCAN = 1;
    private static final int REQUEST_CODE_SPIC = 0;
    private static final int REQUEST_CODE_USER_PIC = 2;
    private final int mRequestCode = 100;//权限请求码
    private ProgressDialog progressDialog;
    public DrawerLayout drawer;
    private Button btn_drawer;
    private NavigationView navigationView;
    public String bg_path;
    public Drawable bgPNG;
    public int primaryColor;
    public LocalDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Blur.bkg=null;
        primaryColor=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        LogToFile.init(MainActivity.this);
        setBg();
        requestPermission();
        dbHelper=new LocalDatabaseHelper(this,ConstValue.LocalDatabaseName,null,LocalDatabaseHelper.NEW_VERSION);
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        db.close();
        navigationView.setCheckedItem(R.id.nav_home);
        synMyWrite();
        changeVarHead();

    }

    //初始化布局以及加载主Fragment
    void initView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }



        drawer = findViewById(R.id.drawer_layout);
        btn_drawer = findViewById(R.id.nav_button);
        btn_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDrawer();
            }
        });


        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_write) {
                    FragmentManager manager=getSupportFragmentManager();
                    FragmentTransaction transaction=manager.beginTransaction();
                    transaction.replace(R.id.main_frame,new FragmentWrite());
                    transaction.commit();
                    // Handle the camera action
                } else if (id == R.id.nav_home) {
                    FragmentManager manager=getSupportFragmentManager();
                    FragmentTransaction transaction=manager.beginTransaction();
                    transaction.replace(R.id.main_frame,new FragmentMain());
                    transaction.commit();

                } else if (id == R.id.nav_mybook) {
                    FragmentManager manager=getSupportFragmentManager();
                    FragmentTransaction transaction=manager.beginTransaction();
                    transaction.replace(R.id.main_frame,new FragmentMyBook());
                    transaction.commit();

                } else if (id == R.id.nav_setting) {
                    FragmentManager manager=getSupportFragmentManager();
                    FragmentTransaction transaction=manager.beginTransaction();
                    transaction.replace(R.id.main_frame,new FragmentSetting());
                    transaction.commit();

                } else if (id == R.id.nav_exit){
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(id==R.id.nav_rank) {
                    FragmentManager manager=getSupportFragmentManager();
                    FragmentTransaction transaction=manager.beginTransaction();
                    transaction.replace(R.id.main_frame,new FragmentRank());
                    transaction.commit();
                } else if (id == R.id.nav_about) {
                    FragmentManager manager=getSupportFragmentManager();
                    FragmentTransaction transaction=manager.beginTransaction();
                    transaction.replace(R.id.main_frame,new FragmentUs());
                    transaction.commit();

                }
                drawer.closeDrawers();
                return true;
            }
        });



        //将R.id.main_frame替换为FragmentMain
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.main_frame,new FragmentMain());
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        //按下返回键，关闭抽屉
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }









    /**
     * 动态权限申请
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //需要检查的三个权限
            String[] permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            List<String> mPermissionList = new ArrayList<>();

            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);//添加还未授予的权限
                }
            }

            //申请权限
            if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
                ActivityCompat.requestPermissions(this, permissions, mRequestCode);
            }
        }

    }

    /**
     * 显示进度对话框
     */
    public void showProgressDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("请稍后...");
        }
        progressDialog.show();
    }


    /**
     * 关闭进度对话框
     */
    public void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }


    //扫码，更具服务器返回的Book来决定执行什么操作
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(MainActivity.this, "无该图书信息", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    closeProgressDialog();
                    Intent intent = new Intent(MainActivity.this,BookActivity.class);
                    intent.putExtra("BOOK",(Book)(msg.getData().getSerializable("BOOK")));
                    startActivity(intent);
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, "上传用户头像失败！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    closeProgressDialog();
                    Toast.makeText(MainActivity.this, "上传用户头像成功！", Toast.LENGTH_SHORT).show();
                    break;
                case 11:
                    Toast.makeText(MainActivity.this, "同步笔记成功", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Bundle bundle =msg.getData();
                    ActivationMessage activationMessage = (ActivationMessage) bundle.getSerializable("acivationMsg");
                    if(activationMessage!=null){
                        switch (activationMessage.getActivationCondition()){
                            case -1:
                                closeProgressDialog();
                                Toast.makeText(MainActivity.this, "请先登入", Toast.LENGTH_SHORT).show();
                                break;
                            case 0:
                                closeProgressDialog();
                                Toast.makeText(MainActivity.this, "该图书已经被激活", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, "激活成功", Toast.LENGTH_SHORT).show();
                                //再次请求书籍信息
                                getActivationBookInfor(activationMessage.getActivationBookID());
                                break;



                        }
                    }
                    break;
                case 5:
                    closeProgressDialog();
                    addToHas((Book)(msg.getData().getSerializable("BOOK")));
                    break;
                case 6:
                    Toast.makeText(MainActivity.this, "无效二维码！", Toast.LENGTH_SHORT).show();
                    closeProgressDialog();
                    break;

            }
        }
    };

    public void showDrawer(){
        System.out.println("----------------------------");
        drawer.openDrawer(GravityCompat.START);
    }
    public void closeDrawer(){
        drawer.closeDrawers();
    }

    //Activity之间的信息传递
    //详细可以百度OnActivityResult相关的信息
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        switch (requestCode){
            case REQUEST_CODE_SCAN:
                if(resultCode==RESULT_OK){
                    if (data != null) {
                        String content = data.getStringExtra(Constant.CODED_CONTENT);
                        if(content==null||content.length()<1)return;
                        String all[]=content.split(" ");
                        String type=all[0];
                        if(all.length<2||!Utility.isInteger(all[1])){
                            Toast.makeText(MainActivity.this, "无效二维码！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final String bookId=all[1];
                        if(type.equals(ConstValue.scanCode_book)){
                            showProgressDialog();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Socket socket = null;
                                    try {
                                        //连接服务器，发送书籍id，获取返回的Book信息
                                        socket = new Socket(ConstValue.serverIp,ConstValue.serverPortBook);
                                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                        out.writeObject(new Book(bookId));
                                        out.flush();
                                        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                                        Book completeBook= (Book) in.readObject();

                                        Message msg = new Message();
                                        if(completeBook.getId().equals("0")){
                                            //Book的id为0，代表数据中不存在该书
                                            msg.what=0;
                                        }else{
                                            msg.what=1;
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("BOOK",completeBook);
                                            msg.setData(bundle);
                                        }

                                        //线程消息传递（在其它线程中，无法执行跳转界面以及界面更新等操作，需要告知主线程，让主线程来完成这些操作）
                                        //参考《第一行代码》10.2
                                        handler.sendMessage(msg);
                                        socket.close();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        LogToFile.e("MainActivity","获取图书错误");
                                        LogToFile.e("MainActivity",e.getMessage());
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                        LogToFile.e("MainActivity","获取图书错误");
                                        LogToFile.e("MainActivity",e.getMessage());
                                    }

                                }
                            }).start();
                        }else if(type.equals(ConstValue.scanCode_focus)){
                            //扫码到的二维码为专注二维码，跳转专注界面
                            if(all.length<3){
                                Toast.makeText(this, "无效二维码！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String bookname=all[2];
                            Intent intent = new Intent(this,FocusActivity.class);
                            intent.putExtra("bookid",bookId);
                            intent.putExtra("bookname",bookname);
                            startActivity(intent);
                        }else if(type.equals(ConstValue.scanCode_write)){
                            if(all.length<3){
                                Toast.makeText(this, "无效二维码！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String bookname=all[2];
                            Intent intent = new Intent(this,WriteActivity.class);
                            intent.putExtra("bookid",bookId);
                            intent.putExtra("bookname",bookname);
                            intent.putExtra("type",3);
                            startActivity(intent);
                        }else if(type.equals(ConstValue.scanCode_jihuo)){
                            showProgressDialog();
                            String code=all[1];
                            final ActivationMessage amsg=new ActivationMessage(userAccount,getUserpwd(),code);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Socket socket = null;
                                    try {
                                        socket = new Socket(ConstValue.serverIp,ConstValue.serverPortactivation);
                                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                        out.writeObject(amsg);
                                        out.flush();
                                        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                                        ActivationMessage msg1= (ActivationMessage) in.readObject();

                                        Message msg = new Message();
                                        Bundle bundle  = new Bundle();
                                        bundle.putSerializable("acivationMsg",msg1);
                                        msg.setData(bundle);
                                        msg.what=4;
                                        handler.sendMessage(msg);
                                        socket.close();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        LogToFile.e("MainActivity","激活图书失败");
                                        LogToFile.e("MainActivity",e.getMessage());
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                        LogToFile.e("MainActivity","激活图书失败");
                                        LogToFile.e("MainActivity",e.getMessage());
                                    }

                                }
                            }).start();


                        }else{
                            Toast.makeText(this, "我不知道你扫描了啥哎~", Toast.LENGTH_SHORT).show();
                        }



                    }

                }
                break;
            case REQUEST_CODE_SPIC:
                if (resultCode == RESULT_OK) {
                    //Activity之间的信息传递
                    //选择自定义背景后返回的处理
                    //详细可以百度OnActivityResult相关的信息

                    Uri imageUri = data.getData();//图片的相对路径
                    Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);//用ContentProvider查找选中的图片
                    cursor.moveToFirst();
                    final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//获取图片的绝对路径
                    Log.d("MainActivity", path);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences.Editor editor = getSharedPreferences(ConstValue.getConfigDataName(),MODE_PRIVATE).edit();
                            editor.putString(ConstValue.getIsBackGroundPNG(),path);
                            editor.apply();
                            Log.d("MainActivity","保存图片地址");
                        }
                    }).start();
                    DrawerLayout layout = findViewById(R.id.drawer_layout);
                    layout.setBackground(Drawable.createFromPath(path));
                    cursor.close();
                    Toast.makeText(this, "重启生效！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_USER_PIC:
                if (resultCode == RESULT_OK) {
                    //Activity之间的信息传递
                    //选择自定义背景后返回的处理
                    //详细可以百度OnActivityResult相关的信息
                    showProgressDialog();
                    Uri imageUri = data.getData();//图片的相对路径
                    Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);//用ContentProvider查找选中的图片
                    cursor.moveToFirst();
                    final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//获取图片的绝对路径
                    Log.d("MainActivity", path);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences.Editor editor = getSharedPreferences(ConstValue.getConfigDataName(),MODE_PRIVATE).edit();
                            editor.putString(ConstValue.UserPic,path);
                            editor.apply();
                            Log.d("MainActivity","保存用户头像图片地址");
                            try {
                                FTP.getInstance().uploadSingleFile(new File(path), "", getUserID(),new FTP.UploadProgressListener() {
                                    @Override
                                    public void onUploadProgress(String currentStep, long uploadSize, File file) {
                                        if (currentStep.equals(FTP_UPLOAD_SUCCESS)) {
                                            handler.sendEmptyMessage(3);
                                        } else if (currentStep.equals(FTP_UPLOAD_FAIL)) {
                                            handler.sendEmptyMessage(2);
                                        }else{
                                            //handler.sendEmptyMessage(2);
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    //DrawerLayout layout = findViewById(R.id.drawer_layout);
                    //layout.setBackground(Drawable.createFromPath(path));
                    cursor.close();
                    //Toast.makeText(this, "重启生效！", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

   public void startScanActivity(){
        Intent intent = new Intent(this,CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
   }

    /**
     * 保存是否开启毛玻璃效果
     */
    public void saveIsBlur(boolean isBlur){
        SharedPreferences.Editor editor = getSharedPreferences(ConstValue.getConfigDataName(),MODE_PRIVATE).edit();
        editor.putBoolean(ConstValue.isBlur,isBlur);
        editor.apply();
    }

    /**
     * 修改var_head的内容
     */
    public void changeVarHead() {
        SharedPreferences preferences = getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE);
        final String account= preferences.getString(ConstValue.databaseUserId,"账号");
        userAccount=account;
        final String name= preferences.getString(ConstValue.databaseUserName,"用户名");
        View headerView = navigationView.getHeaderView(0);
        TextView textview = (TextView) headerView.findViewById(R.id.nav_username);
        textview.setText(name);
        TextView textView1 = headerView.findViewById(R.id.nav_account);
        textView1.setText(account);
        CircleImageView userPic=headerView.findViewById(R.id.nav_userpic);
        RequestOptions options = new RequestOptions().placeholder(R.drawable.user_pic).error(R.drawable.user_pic).centerCrop();
        Glide.with(this).load(ConstValue.serverUserPic+ getUserID()+".png")
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("MainActivity","加载用户头像失败");
                        LogToFile.e("MainActivity","加载用户头像失败");
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(userPic);

        Log.e("MainActivity",primaryColor+"");
        if(primaryColor!=0){
            LinearLayout layout = headerView.findViewById(R.id.nav_header);
            layout.setBackgroundColor(primaryColor);
        }
        Log.e("cloudbook","颜色:"+primaryColor);
    }

    public String getUserID(){
        SharedPreferences preferences = getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE);
        return preferences.getString(ConstValue.databaseUserId,"");
    }
    public String getUserName(){
        SharedPreferences preferences = getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE);
        return preferences.getString(ConstValue.databaseUserName,"");
    }
    public String getUserpwd(){
        SharedPreferences preferences = getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE);
        return preferences.getString(ConstValue.databaseUserpassword,"");
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

    /**
     * 自定义背景图片
     */
    public void chooseUserPic() {
        //requestWritePermission();
        Intent intent_choose = new Intent(Intent.ACTION_PICK);//Intent.ACTION_GET_CONTENT和是获得最近使用过的图片。
        intent_choose.setType("image/*");//应该是指定数据类型是图片。
        startActivityForResult(intent_choose, REQUEST_CODE_USER_PIC);
    }

    /**
     * 自定义背景图片
     */
    public void chooseBg() {
        //requestWritePermission();
        Intent intent_choose = new Intent(Intent.ACTION_PICK);//Intent.ACTION_GET_CONTENT和是获得最近使用过的图片。
        intent_choose.setType("image/*");//应该是指定数据类型是图片。
        startActivityForResult(intent_choose, REQUEST_CODE_SPIC);
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
                drawer.setBackground(bgPNG);
                primaryColor = colorFromBitmap( BitmapFactory.decodeFile(path));
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
     *得到背景图品地址
     */
    public String getBg_path(){
        SharedPreferences preferences = getSharedPreferences(ConstValue.getConfigDataName(),MODE_PRIVATE);
        return preferences.getString(ConstValue.getIsBackGroundPNG(),"");
    }

    public void cancelBg(){
       SharedPreferences.Editor editor  = getSharedPreferences(ConstValue.getConfigDataName(),MODE_PRIVATE).edit();
       editor.putString(ConstValue.getIsBackGroundPNG(),"");
       editor.apply();
    }


    /**
     *得到主色调
     */
    private int colorFromBitmap(Bitmap bitmap) {
        final int NUMBER_OF_PALETTE_COLORS = 128;
        final Palette palette = Palette.generate(bitmap, NUMBER_OF_PALETTE_COLORS);
        if (palette != null && palette.getVibrantSwatch() != null) {
            primaryColor = palette.getVibrantSwatch().getRgb();
            return palette.getVibrantSwatch().getRgb();
        }
        return 0;
    }


    private void synMyWrite(){
        write= new Write("0","0",null,"");
        write.user_id=getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE).getString(ConstValue.databaseUserId,"0");
        Log.e("MainActivity","11111111111111111");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket socket = new Socket(ConstValue.serverIp,ConstValue.serverPortgetWriters);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(write);
                    out.flush();
                    ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    WriteList write_get= (WriteList) in.readObject();
                    Message msg = new Message();
                    msg.what=11;
                    socket.close();
                    SQLiteDatabase db =dbHelper.getWritableDatabase();
                    for(int i=0;i<write_get.writeList.size();i++){
                        Write write_temp=write_get.writeList.get(i);
                        ContentValues value = new ContentValues();
                        value.put("id",write_temp.id);
                        value.put("bookid",write_temp.book_id);
                        value.put("bookname",write_temp.book_name);
                        value.put("content",write_temp.content);
                        value.put("userid",write_temp.user_id);
                        value.put("time",write_temp.date.toString());
                        Log.e("cloudbook",write_temp.id+" "+write_temp.book_id);
                        if(hasInLocalDb(db,write_temp.id)){
                            //update
                            String sql = "UPDATE "+ConstValue.LocalDB_TABLE_WRITE+" set bookname='"+write_temp.book_name+"',content='"+write_temp.content+"' where id="+write_temp.getId()+";";
                            Log.e("MainActivity",sql);
                            db.execSQL(sql);
                        }else{
                            //insert
                            db.insert(ConstValue.LocalDB_TABLE_WRITE,null,value);
                        }
                    }
                    db.close();
                    Log.e("MainActivity","22222222222");
                    handler.sendMessage(msg);

                }catch (IOException e){
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean hasInLocalDb(SQLiteDatabase db,String id){
        Cursor cursor = db.query("write", null, "id=?", new String[] { id }, null, null, null);
        while (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    void addToHas(Book book){
        String bookid=book.getId();
        String bookName=book.getName();
        float price=book.getPrice();
        SharedPreferences preferences = getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE);
        final String account= preferences.getString(ConstValue.databaseUserId,"");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("bookname",bookName);
        value.put("bookid",bookid);
        value.put("userid",account);
        value.put("cover",book.getCover());
        value.put("writer",book.getWriter());
        value.put("allpages",100);
        value.put("readpages",0);
        value.put("price",price);
        value.put("lasttime",System.currentTimeMillis());
        db.insert("has",null,value);
        db.close();
        Toast.makeText(MainActivity.this, "成功加入书架！", Toast.LENGTH_SHORT).show();
    }

    void getActivationBookInfor(final String bookId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                try {
                    //连接服务器，发送书籍id，获取返回的Book信息
                    socket = new Socket(ConstValue.serverIp,ConstValue.serverPortBook);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(new Book(bookId));
                    out.flush();
                    ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    Book completeBook= (Book) in.readObject();

                    Message msg = new Message();
                    if(completeBook.getId().equals("0")){
                        //Book的id为0，代表数据中不存在该书
                        msg.what=0;
                    }else{
                        msg.what=5;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("BOOK",completeBook);
                        msg.setData(bundle);
                    }

                    //线程消息传递（在其它线程中，无法执行跳转界面以及界面更新等操作，需要告知主线程，让主线程来完成这些操作）
                    //参考《第一行代码》10.2
                    handler.sendMessage(msg);
                   socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    LogToFile.e("MainActivity","获取图书错误");
                    LogToFile.e("MainActivity",e.getMessage());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    LogToFile.e("MainActivity", "获取图书错误");
                    LogToFile.e("MainActivity", e.getMessage());
                }
            }
        }).start();
    }



}
