package cloudbook.gaoch.com.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import MyClass.Blur;
import MyClass.Book;
import MyClass.BookHas;
import MyClass.MyAdapter.RecomItemAdapter;
import MyClass.Recom;
import MyClass.Utility.FocusTime;
import MyClass.Utility.LogToFile;
import cloudbook.gaoch.com.ActivityUserInfor;
import cloudbook.gaoch.com.BookActivity;
import cloudbook.gaoch.com.ConstValue;
import cloudbook.gaoch.com.FocusActivity;
import cloudbook.gaoch.com.MainActivity;
import cloudbook.gaoch.com.MyFocusHistoryActivity;
import cloudbook.gaoch.com.MyView.CircleExp;
import cloudbook.gaoch.com.MyView.LineHourlyView;
import cloudbook.gaoch.com.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class FragmentMain extends Fragment {
    private Button btn_scan,btn_focus,btn_myBookList,btn_write;
    private static final int REQUEST_CODE_SCAN = 1;
    private RecyclerView recyclerView,recyclerView_recom;
    private List<Recom>recomList;
    private RecomItemAdapter adapter_recom;
    private CircleImageView circleImageView;
    private int hasBlured_top1=0,hasBlured_top2=0,hasBlured_top3=0,hasBlured_top4=0;
    private  LinearLayout layout_1, layout_2, layout_4, layout_part1,layout_3;
    private LineHourlyView lineView;
    private CircleExp exp;
    List<FocusTime> focusTimesList;
    private TextView tv_name,tv_exp;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);
        btn_scan = view.findViewById(R.id.fragment_main_btn_sao);
        btn_focus=view.findViewById(R.id.fragment_main_btn_focus);
        btn_myBookList=view.findViewById(R.id.fragment_main_btn_myList);
        layout_1=view.findViewById(R.id.fragment_main_head_bkg);
        layout_2=view.findViewById(R.id.main_layout1);
        layout_4=view.findViewById(R.id.main_layout3);
        layout_3=view.findViewById(R.id.main_layout4);
        recyclerView_recom=view.findViewById(R.id.main_rv_recom);
        exp=view.findViewById(R.id.fragment_main_exp);
        tv_name=view.findViewById(R.id.fragment_main_tv_name);
        tv_exp=view.findViewById(R.id.fragment_main_tv_exp);


        //设置主界面高度为屏幕高度
        layout_part1 =view.findViewById(R.id.f_main_part1);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int height = dm.heightPixels;
        layout_part1.setMinimumHeight(height);

        lineView=view.findViewById(R.id.main_read);
        lineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MyFocusHistoryActivity.class);
                startActivity(intent);
            }
        });

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).startScanActivity();
            }
        });

        btn_focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),FocusActivity.class);
                startActivity(intent);
            }
        });
        btn_myBookList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getActivity().getSupportFragmentManager();
                FragmentTransaction transaction=manager.beginTransaction();
                transaction.replace(R.id.main_frame,new FragmentMyBook());
                transaction.commit();
            }
        });
        btn_write=view.findViewById(R.id.fragment_main_btn_write);
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getActivity().getSupportFragmentManager();
                FragmentTransaction transaction=manager.beginTransaction();
                transaction.replace(R.id.main_frame,new FragmentWrite());
                transaction.commit();
            }
        });

        recomList=new ArrayList<>();
        recomList.add(new Recom("0","2","三体","给时光以生命，给岁月以文明。"));
        recomList.add(new Recom("1","3","活着","最初我们来到这个世界，是因为不得不来；最终我们离开这个世界，是因为不得不走。"));
        recomList.add(new Recom("2","3","活着","作为一个词语，“ 活着”在我们中国的语言里充满了力量，它的力量不是来自于喊叫，也不是来自于进攻，而是忍受，去忍受生命赋予我们的责任，去忍受现实给予我们的幸福和苦难、无聊和平庸。"));

        adapter_recom=new RecomItemAdapter(recomList,getContext());
        adapter_recom.setOnItemClickListener(new RecomItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
               searchBook(recomList.get(position).book_id);
            }
        });
        recyclerView_recom.setAdapter(adapter_recom);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView_recom.setLayoutManager(layoutManager);
        //((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);



        circleImageView=view.findViewById(R.id.fragment_main_user_pic);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ActivityUserInfor.class);
                startActivity(intent);
            }
        });
        //layout_head=view.findViewById(R.id.fragment_main_head_bkg);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setBlur();
        exp.setAngle(180);
        tv_name.setText(getActivity().getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE).getString(ConstValue.databaseUserName,"御马亲征"));
        int exp= getActivity().getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE).getInt(ConstValue.databaseUserexp,999999);
        int exp_1=exp;
        int level=0;
        while (exp>0){
            exp=exp/10;
            level++;
        }
        tv_exp.setText("Lv."+level);
        Log.e("cloudbook","exp:"+exp_1);
        Log.e("cloudbook","username:"+getActivity().getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE).getString("username","御马亲征"));

    }

    @Override
    public void onResume() {
        super.onResume();
        RequestOptions options = new RequestOptions().placeholder(R.drawable.user_pic).error(R.drawable.user_pic).centerCrop();
        Glide.with(this).load(ConstValue.serverUserPic+((MainActivity)getActivity()).getUserID()+".png")
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("FragmentMain","加载用户头像失败");
                        LogToFile.e("FragmentMai","加载用户头像失败");
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(circleImageView);
        refreshFocus();
    }


    public void setBlur(){
        final View view_test=((MainActivity)getActivity()).drawer;
        if(((MainActivity) getActivity()).getIsBlur()){
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

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    ((MainActivity)getActivity()).closeProgressDialog();
                    Toast.makeText(getContext(), "该本书未收录!", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    ((MainActivity)getActivity()).closeProgressDialog();
                    Intent intent = new Intent(getContext(),BookActivity.class);
                    intent.putExtra("BOOK",(Book)(msg.getData().getSerializable("BOOK")));
                    startActivity(intent);
                    break;
                case 11:
                    Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).closeProgressDialog();
                    break;
                case 12:
                    Intent intent1 = new Intent(getContext(),BookActivity.class);
                    intent1.putExtra("BOOK",(Book)(msg.getData().getSerializable("BOOK")));
                    startActivity(intent1);
                    ((MainActivity)getActivity()).closeProgressDialog();
                    break;
            }
        }
    };


    void refreshHasList(){
        //if(bookList.size()>0) bookList.clear();
        SQLiteDatabase db =((MainActivity)getActivity()).dbHelper.getReadableDatabase();
        Cursor cursor = db.query("has",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                String bookid=cursor.getString(cursor.getColumnIndex("bookid"));
                String userid=cursor.getString(cursor.getColumnIndex("userid"));
                String bookName=cursor.getString(cursor.getColumnIndex("bookname"));
                String price=cursor.getString(cursor.getColumnIndex("price"));
                String writer=cursor.getString(cursor.getColumnIndex("writer"));
                int allPages=cursor.getInt(cursor.getColumnIndex("allpages"));
                int readPages=cursor.getInt(cursor.getColumnIndex("readpages"));
                String time = cursor.getString(cursor.getColumnIndex("lasttime"));
                BookHas book= new BookHas(bookid);
                book.setName(bookName);
                book.setWriter(writer);
                book.setPrice(Float.valueOf(price));
                book.setCover(cursor.getString(cursor.getColumnIndex("cover")));
                book.setAllPages(allPages);
                book.setReadPages(readPages);
                book.setLastRead(new Date(Long.valueOf(time)));
                //bookList.add(book);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        //adapter.notifyDataSetChanged();
    }


    void searchBook(final String bookId){
        ((MainActivity)getActivity()).showProgressDialog();
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
                    myHandler.sendMessage(msg);

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
    }

    void refreshFocus(){
        SQLiteDatabase db =((MainActivity)getActivity()).dbHelper.getReadableDatabase();
        String user_id=((MainActivity)getActivity()).getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE).getString(ConstValue.databaseUserId,"");
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
                Log.e("cloudbook","s:"+start+" e:"+end);
                int allSeconds= (int) ((end-start)/1000);
                int minute=allSeconds/60;
                int second=allSeconds-minute*60;
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
        if(focusTimesList.size()>=7){
            for(int i=focusTimesList.size()-7;i<focusTimesList.size();i++){
                minuteList.add(focusTimesList.get(i).getMinute());
            }
        }else if(focusTimesList.size()>0){
            for(int i=0;i<7-focusTimesList.size();i++){
                minuteList.add(0);
            }
            for(int i=0;i<focusTimesList.size();i++){
                minuteList.add(focusTimesList.get(i).getMinute());
            }
        }else{
            for(int i=0;i<7;i++){
                minuteList.add(0);
            }
        }
        Log.e("cloudbook","focuse_size:"+user_id+",+"+focusTimesList.size());

        lineView.addDots(minuteList);
        cursor.close();
        db.close();
    }







}
