package cloudbook.gaoch.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import MyClass.LoginPermission;
import MyClass.SigninMessage;
import MyClass.UserMessage;

public class StartActivity extends AppCompatActivity {
    static Socket socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        SharedPreferences preferences = getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE);
        final String account= preferences.getString(ConstValue.databaseUserId,"");
        final String password=preferences.getString(ConstValue.databaseUserpassword,"");

        if(!account.equals("")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        socket = new Socket(ConstValue.serverIp,ConstValue.serverPortLogin);
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(new SigninMessage(account,password));
                        out.flush();
                        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                        LoginPermission permission= (LoginPermission) in.readObject();
                        Message msg = new Message();
                        if(permission!=null){
                            switch (permission.permissionCode){
                                case 1:
                                    msg.what=1;
                                    Bundle bundle = new Bundle();
                                    bundle.putString(ConstValue.databaseUserId,account);  //往Bundle中存放数据
                                    bundle.putString(ConstValue.databaseUserpassword,password);  //往Bundle中存放数据
                                    bundle.putSerializable("USER",permission.userInfor);
                                    msg.setData(bundle);//mes利用Bundle传递数据
                                    break;
                                case 0:
                                    msg.what=0;
                                    break;
                                case -1:
                                    msg.what=-1;
                                    break;
                            }
                        }
                        socket.close();
                        myhandle.sendMessage(msg);
                    }catch (IOException e){
                        e.printStackTrace();
                        Message msg = new Message();
                        msg.what=3;
                        myhandle.sendMessage(msg);
                    } catch (ClassNotFoundException e) {
                        Message msg = new Message();
                        msg.what=3;
                        myhandle.sendMessage(msg);
                        e.printStackTrace();
                    }
                }
            }).start();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what=3;
                    myhandle.sendMessage(msg);
                }
            }).start();
        }



    }

    Handler myhandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstValue.LoginReturnCode_sucess:
                    String name = msg.getData().getString(ConstValue.databaseUserId);
                    String password=msg.getData().getString(ConstValue.databaseUserpassword);
                    Toast.makeText( StartActivity.this, "欢迎登入："+name, Toast.LENGTH_SHORT).show();
                    UserMessage userMessage= (UserMessage) msg.getData().getSerializable("USER");
                    saveToSP(userMessage);
                    Intent intent = new Intent(StartActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case ConstValue.LoginReturnCode_wrong:
                    Intent intent1 = new Intent(StartActivity.this,LoginActivity.class);
                    startActivity(intent1);
                    Toast.makeText( StartActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case ConstValue.LoginReturnCode_noAccount :
                    Intent intent2 = new Intent(StartActivity.this,LoginActivity.class);
                    startActivity(intent2);
                    Toast.makeText( StartActivity.this, "无该账号", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 3:
                    Intent intent3 = new Intent(StartActivity.this,LoginActivity.class);
                    startActivity(intent3);
                    finish();
                    break;
            }

        }
    };

    void saveToSP(UserMessage msg){
        SharedPreferences.Editor editor = getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE).edit();
        editor.putString(ConstValue.databaseUserId,msg.getId());
        editor.putString(ConstValue.databaseUserName,msg.getUsername());
        editor.putInt(ConstValue.databaseUserexp,msg.getExp());
        editor.putInt(ConstValue.databaseUserRT,msg.getReadtime());
        editor.putInt(ConstValue.databaseUserBooks,msg.getBooks());
        editor.putInt(ConstValue.databaseUserachi,msg.getAchieve());
        editor.apply();
        Log.e("cloudbook",msg.getUsername()+"-------"+msg.getExp());
    }


}
