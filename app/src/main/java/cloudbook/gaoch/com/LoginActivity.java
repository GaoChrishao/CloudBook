package cloudbook.gaoch.com;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import MyClass.LoginPermission;
import MyClass.SignUpMessage;
import MyClass.SignUpPermission;
import MyClass.SigninMessage;
import MyClass.UserMessage;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_login;
    private Button btn_signup;
    private EditText edit_account;
    private EditText edit_password,edit_password_re;
    static Socket socket = null;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();


    }

    private void initView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        btn_login=findViewById(R.id.button_login);
        btn_signup=findViewById(R.id.button_sign_up);
        edit_account=findViewById(R.id.editText_account);
        edit_password=findViewById(R.id.editText_password);
        edit_password_re=findViewById(R.id.editText_password_re);
        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE);
        String name= preferences.getString(ConstValue.databaseUserId,"");
        String pwd=preferences.getString(ConstValue.databaseUserpassword,"");
        edit_account.setText(name);
        edit_password.setText(pwd);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_login:
                final String account=edit_account.getText().toString();
                final String password=edit_password.getText().toString();
                if(account.length()<1||password.length()<1){
                    Toast.makeText(LoginActivity.this, "账号或密码不能为空！", Toast.LENGTH_SHORT).show();
                    return ;
                }else{
                    SharedPreferences.Editor editor =  getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE).edit();
                    editor.putString(ConstValue.databaseUserId,account);
                    editor.putString(ConstValue.databaseUserpassword,password);
                    editor.apply();
                    showProgressDialog();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                socket = new Socket(ConstValue.serverIp,ConstValue.serverPortLogin);
                                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                out.writeObject(new SigninMessage(account,password));
                                out.flush();
                                ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                                LoginPermission permission= (LoginPermission) in.readObject();
                                Message msg = new Message();
                                if(permission!=null){
                                    Log.e("cloudbook","msg"+permission.permissionCode);
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
                                            if(socket.isConnected()){
                                                msg.what=0;
                                            }else{
                                                msg.what=ConstValue.SignUpReturnCode_serverWrong;
                                            }
                                            break;
                                        case -1:
                                            msg.what=-1;
                                            break;
                                    }
                                }
                                socket.close();
                                myhandle.sendMessage(msg);
                            }catch (IOException e){
                                myhandle.sendEmptyMessage(ConstValue.SignUpReturnCode_serverWrong);

                            } catch (ClassNotFoundException e) {
                                myhandle.sendEmptyMessage(ConstValue.SignUpReturnCode_serverWrong);
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
            case R.id.button_sign_up:
                final String account_1=edit_account.getText().toString();
                final String password_1=edit_password.getText().toString();
                final String password_re=edit_password_re.getText().toString();
                    if(edit_password_re.getVisibility()==View.GONE){
                        edit_password_re.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "请再次输入密码后确定", Toast.LENGTH_SHORT).show();
                    }else{

                        if(!password_re.equals(password_1)){
                            Toast.makeText(this, "两次输入密码需要相同！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(account_1.length()<6||password_1.length()<6||account_1.length()>15){
                            Toast.makeText(LoginActivity.this, "账号不能大于15位小于6位,密码不能大于20位小于6位", Toast.LENGTH_SHORT).show();
                            return ;
                        }else{
                            showProgressDialog();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        socket = new Socket(ConstValue.serverIp,ConstValue.serverPortSignUp);
                                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                        out.writeObject(new SignUpMessage(account_1,password_1,password_1));
                                        out.flush();
                                        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                                        SignUpPermission permission= (SignUpPermission) in.readObject();
                                        Message msg = new Message();
                                        if(permission!=null){
                                            msg.what=permission.permissionCode;

                                        }
                                        socket.close();
                                        myhandle.sendMessage(msg);

                                    }catch (IOException e){
                                        myhandle.sendEmptyMessage(ConstValue.SignUpReturnCode_serverWrong);
                                    } catch (ClassNotFoundException e) {
                                        myhandle.sendEmptyMessage(ConstValue.SignUpReturnCode_serverWrong);
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                break;
        }
    }

    Handler myhandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstValue.LoginReturnCode_sucess:
                    String name = msg.getData().getString(ConstValue.databaseUserId);
                    String password=msg.getData().getString(ConstValue.databaseUserpassword);
                    Toast.makeText( LoginActivity.this, "欢迎登入："+name, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor =  getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE).edit();
                    editor.putString(ConstValue.databaseUserId,name);
                    editor.putString(ConstValue.databaseUserpassword,password);
                    editor.apply();

                    UserMessage userMessage= (UserMessage) msg.getData().getSerializable("USER");
                    saveToSP(userMessage);

                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case ConstValue.LoginReturnCode_wrong:
                    Toast.makeText( LoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValue.LoginReturnCode_noAccount :
                    Toast.makeText( LoginActivity.this, "无该账号", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValue.SignUpReturnCode_hasExistAccount:
                    Toast.makeText(LoginActivity.this, "已经存在该账号", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValue.SignUpReturnCode_serverWrong:
                    Toast.makeText(LoginActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValue.SignUpReturnCode_sucess:
                    Toast.makeText(LoginActivity.this, "成功创建", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValue.SignUpReturnCode_wrong:
                    Toast.makeText(LoginActivity.this, "账号密码出错！", Toast.LENGTH_SHORT).show();
                    break;
            }
            closeProgressDialog();
        }
    };


    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("请稍后...");
            //progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }


    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

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
