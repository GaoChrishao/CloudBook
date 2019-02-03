package cloudbook.gaoch.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import MyClass.Blur;
import MyClass.LoginPermission;
import MyClass.SigninMessage;
import MyClass.UserMessage;
import cloudbook.gaoch.com.ConstValue;
import cloudbook.gaoch.com.LoginActivity;
import cloudbook.gaoch.com.MainActivity;
import cloudbook.gaoch.com.R;

public class FragmentSetting extends Fragment {
    private Switch switch_blur;
    private Button BgButton,Btn_UserPic;
    private Button BgButton1;
    private LinearLayout layout1, layout2,layout3,layout_4;      //高斯模糊的布局
    private int hasBlured_top1=0,hasBlured_top2=0,hasBlured_top3=0,hasBlured_top4=0; //记录需要高斯模糊的布局的位置，如果没变，则不需要模糊
    private Button btn_change;
    private EditText ed_name,ed_pwd;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting,container,false);
        initView(view);
        return view;
    }

    void initView(View view){
        switch_blur=view.findViewById(R.id.config_theme_switch_blur);
        BgButton = view.findViewById(R.id.theme_chooseBG);
        Btn_UserPic=view.findViewById(R.id.theme_chooseUserPc);
        BgButton1 = view.findViewById(R.id.theme_cancelBG);
        layout1 = view.findViewById(R.id.fragment_theme_layout1);
        layout2 = view.findViewById(R.id.fragment_theme_layout2);
        layout3=view.findViewById(R.id.fragment_theme_layout3);
        layout_4=view.findViewById(R.id.fragment_theme_layout4);
        btn_change=view.findViewById(R.id.setting_btn);
        ed_name=view.findViewById(R.id.setting_username);
        ed_pwd=view.findViewById(R.id.setting_pwd);
        ed_name.setText(((MainActivity)getActivity()).getUserName());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //毛玻璃开启按钮
        switch_blur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked&& ((MainActivity) getActivity()).getIsBlur()==false){
                    MainActivity activity = (MainActivity) getActivity();
                    activity.saveIsBlur(true);
                    Toast.makeText(getContext(), "开启毛玻璃效果,重启生效", Toast.LENGTH_SHORT).show();
                }else if(!isChecked){
                    MainActivity activity = (MainActivity) getActivity();
                    activity.saveIsBlur(false);
                    Toast.makeText(getContext(), "已关闭毛玻璃效果，重启生效", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //选择背景图片
        BgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.chooseBg();
            }
        });

        //选择用户头像
        Btn_UserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.chooseUserPic();
            }
        });
        BgButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.cancelBg();
                Toast.makeText(activity, "重启生效！", Toast.LENGTH_SHORT).show();

            }
        });
        btn_change.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String newPwd=ed_pwd.getText().toString()+"";
                String userName=ed_name.getText().toString()+"";
                //type=1

                if(newPwd.length()<1){
                    newPwd=((MainActivity)getActivity()).getUserpwd();
                    //值更新用户名
                    if(userName.length()<1){
                        Toast.makeText(getContext(), "名称长度太短！", Toast.LENGTH_SHORT).show();
                        return ;
                    }
                }else if(newPwd.length()<8){
                    Toast.makeText(getContext(), "密码长度需要>8！", Toast.LENGTH_SHORT).show();
                    return;
                }
                ((MainActivity)getActivity()).showProgressDialog();
                final UserMessage userMessage = new UserMessage(((MainActivity)getActivity()).getUserID(),userName,newPwd);
                userMessage.type=ConstValue.userInfor_type_sendChnage;
                userMessage.oldpassword=((MainActivity)getActivity()).getUserpwd();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Socket socket = new Socket(ConstValue.serverIp,ConstValue.serverPortUserInfor);
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            out.writeObject(userMessage);
                            out.flush();
                            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                            UserMessage permission= (UserMessage) in.readObject();
                            Message msg = new Message();
                            if(permission!=null){
                              msg.what=permission.type;

                            }else{
                                msg.what=0;
                            }
                            socket.close();
                            myhandler.sendMessage(msg);
                        }catch (IOException e){
                            e.printStackTrace();
                            Message msg = new Message();
                            msg.what=0;
                            myhandler.sendMessage(msg);
                        } catch (ClassNotFoundException e) {
                            Message msg = new Message();
                            msg.what=0;
                            myhandler.sendMessage(msg);
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        });


        MainActivity activity = (MainActivity) getActivity();
        if(activity.getIsBlur()){
            switch_blur.setChecked(true);
        }
        setBlur();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setBlur(){
        if(((MainActivity)getActivity()).getIsBlur()){
            final View view_test=((MainActivity)getActivity()).drawer;
            layout1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if(hasBlured_top1!=layout1.getTop()){
                        //view_test为背景布局，由此布局提取处需要模糊的背景
                        Blur.blur_static(view_test,layout1,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top1=layout1.getTop();
                    }

                    return true;
                }
            });
            layout2.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if(hasBlured_top2!=layout2.getTop()){
                        Blur.blur_static(view_test,layout2,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top2=layout2.getTop();
                    }

                    return true;
                }
            });
            layout3.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if(hasBlured_top3!=layout3.getTop()){
                        Blur.blur_static(view_test,layout3,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top3=layout3.getTop();
                    }
                    return true;
                }
            });
            layout_4.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if(hasBlured_top4!=layout_4.getTop()){
                        Blur.blur_static(view_test,layout_4,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top4=layout_4.getTop();
                    }
                    return true;
                }
            });
        }
    }

    Handler myhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(getContext(), "服务器或者网络出错！", Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).closeProgressDialog();
                    break;
                case ConstValue.userInfor_type_changeSucess:
                    Toast.makeText(getContext(), "修改成功，重新登入！", Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).closeProgressDialog();
                    Intent intent = new Intent(getContext(),LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;
                case ConstValue.userInfor_type_changeFail:
                    Toast.makeText(getContext(), "修改失败！", Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).closeProgressDialog();
                    break;
                case ConstValue.userInfor_type_changeWrongpwd:
                    Toast.makeText(getContext(), "密码错误！", Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).closeProgressDialog();
                    break;

            }
        }
    };
}
