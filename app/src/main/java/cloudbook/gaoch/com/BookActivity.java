package cloudbook.gaoch.com;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import MyClass.CommentList;
import MyClass.CommentMessage;
import MyClass.CommentPermission;
import MyClass.MyAdapter.CommentItemAdapter;
import MyClass.UserMessage;
import MyClass.Utility.LocalDatabaseHelper;
import MyClass.Utility.LogToFile;
import MyClass.Write;

public class BookActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_CODE_SCAN = 1;
    private TextView tv_1,book_title,book_writer,book_content,book_likes,book_comments,book_price,book_buynumber;
    private ImageView book_pic;
    private LinearLayout book_main;
    private boolean hasScaned=false;
    int hasBlured_top2=0;
    private List<CommentMessage>commentList;
    private RecyclerView recyclerView;
    private CommentItemAdapter commentAdapter;
    private EditText ed_comment;
    private Button btn_comment;
    private Button iv_add,iv_like;
    Book book;
    private LocalDatabaseHelper dbHelper;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_des);
        book = (Book) (getIntent().getSerializableExtra("BOOK"));
        initView();
        if(book!=null){
            try {
                freshBook(book);
                swipeRefreshLayout.setRefreshing(true);
                getComments();
            }catch (Exception e){
                LogToFile.e("BookActivity",e.getMessage());
            }
            if(book.getComments()<0){

            }

        }
    }

    void initView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |  View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        swipeRefreshLayout=findViewById(R.id.book_swipe);
        dbHelper =new LocalDatabaseHelper(this,ConstValue.LocalDatabaseName,null,LocalDatabaseHelper.NEW_VERSION);

        book_pic=findViewById(R.id.scan_book_pic);
        book_main=findViewById(R.id.scan_book_main);
        recyclerView=findViewById(R.id.scan_book_rv);

        book_comments=findViewById(R.id.scan_book_comments);
        book_content=findViewById(R.id.scan_book_des);
        book_likes=findViewById(R.id.scan_book_likes);
        book_title=findViewById(R.id.scan_book_name);
        book_writer=findViewById(R.id.scan_book_writer);
        book_price=findViewById(R.id.scan_book_price);
        book_buynumber=findViewById(R.id.scan_book_buynumber);


        ed_comment=findViewById(R.id.book_ev_cmt);
        btn_comment=findViewById(R.id.book_btn_cmt);

        iv_add=findViewById(R.id.scan_book_btnadd);
        iv_like=findViewById(R.id.scan_book_btnlike);
        //点击添加图标
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(has_Has()){
//                    Toast.makeText(BookActivity.this, "已经在书架里面了", Toast.LENGTH_SHORT).show();
//                }else{
//                    addToHas();
//                    Toast.makeText(BookActivity.this, "添加书架成功!", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        //点击爱心图标
        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(has_Hope()){
                    SQLiteDatabase db =dbHelper.getWritableDatabase();
                    db.delete("hope","bookid=?",new String[]{book.getId()});
                    //关闭数据库
                    db.close();
                    iv_like.setBackground(null);
                    iv_like.setBackground(getResources().getDrawable(R.drawable.ic_like));
                    Toast.makeText(BookActivity.this, "去除心愿", Toast.LENGTH_SHORT).show();
                }else{
                    //设置为实心图标
                    iv_like.setBackground(null);
                    iv_like.setBackground(getResources().getDrawable(R.drawable.ic_like_fill));
                    addToHope();
                    Toast.makeText(BookActivity.this, "添加心愿", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content=ed_comment.getText().toString()+"";
                if(!content.equals("")&&content.length()>6){
                    SharedPreferences preferences = getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE);
                    String account=preferences.getString(ConstValue.databaseUserId,"000");
                    String pwd=preferences.getString(ConstValue.databaseUserpassword,"000");
                    final CommentMessage cmt = new CommentMessage(account,content,pwd,book.getId());
                    cmt.setDate(new Date(System.currentTimeMillis()));
                    commentList.add(cmt);
                    commentAdapter.notifyDataSetChanged();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Socket socket = new Socket(ConstValue.serverIp,ConstValue.serverPortComment);
                                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                out.writeObject(cmt);
                                out.flush();
                                ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                                CommentPermission permission= (CommentPermission) in.readObject();
                                Message msg = new Message();
                                if(permission!=null){
                                    msg.what=permission.permissionCode;
                                }else{
                                    msg.what=ConstValue.CommentReturnCode_wrong;
                                }
                                socket.close();
                                myhandle.sendMessage(msg);
                            }catch (IOException e){
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else{
                    Toast.makeText(BookActivity.this, "评论内容太短", Toast.LENGTH_SHORT).show();
                }
            }
        });



        commentList=new ArrayList<>();
        commentList.clear();

        commentAdapter=new CommentItemAdapter(commentList,this);
        commentAdapter.setOnItemClickListener(new CommentItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String userId=commentList.get(position).getAccount();
                final UserMessage userMessage = new UserMessage(userId);
                showProgressDialog();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Socket socket = new Socket(ConstValue.serverIp,ConstValue.serverPortUserInfor);
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            out.writeObject(userMessage);
                            out.flush();
                            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                            UserMessage userMessage= (UserMessage) in.readObject();
                            Message msg = new Message();
                            msg.what=22;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("USER",userMessage);
                            msg.setData(bundle);
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        if(has_Hope()){
            iv_like.setBackground(null);
            iv_like.setBackground(getResources().getDrawable(R.drawable.ic_like_fill));
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getComments();
            }
        });
    }

    void freshBook(final Book book){
        book_writer.setText(book.getWriter());
        book_title.setText(book.getName());
        book_likes.setText(""+book.getLikes()+"");
        book_content.setText(book.getContent());
        book_content.setMovementMethod(ScrollingMovementMethod.getInstance());
        book_comments.setText(""+book.getComments()+"");
        book_price.setText(book.getPrice()+"");
        book_buynumber.setText(book.buynumber+"");
        Log.e("BookActivity","尝试加载图片");
        //String urlPath="http://gcloudpan.tk/books/"+book.getCover();
        String urlPath=book.getCover();
        Log.e("BookActivity",urlPath);
        LogToFile.e("BookActivity",urlPath);

        //加载网络图片
        RequestOptions options = new RequestOptions().placeholder(R.drawable.book_pic_default).error(R.drawable.book_pic_default).centerCrop();
        Glide.with(BookActivity.this).load(urlPath)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.d("BookActivity","加载失败");
                LogToFile.e("BookActivity","加载网络图片失败");
                book_pic.setBackground(getDrawable(R.drawable.book_pic_default));
                makeBlur(book_pic.getBackground());
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                book_pic.setBackground(null);
                makeBlur(resource);
                Log.e("BookActivity","加载完成");
                LogToFile.e("BookActivity","加载网络图片成功");
                return false;
            }
        }).into(book_pic);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void makeBlur(Drawable drawable){
        Bitmap bd = ((BitmapDrawable)drawable).getBitmap();
        float height2w=0.5f;
        Bitmap bitmap= Bitmap.createBitmap(bd,bd.getWidth()/4,bd.getHeight()/3,bd.getWidth()/2,(int)(bd.getWidth()/2*height2w));
        book_main.setBackground(new BitmapDrawable(getResources(),Blur.blurBitmap(this,bitmap,20)));
    }

    Handler myhandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstValue.CommentReturnCode_contentTooShort:
                    Toast.makeText( BookActivity.this, "评论内容不能小于6个字符！", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValue.CommentReturnCode_noAccount:
                    Toast.makeText( BookActivity.this, "请登入账号后再评论！", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValue.CommentReturnCode_noBook:
                    Toast.makeText( BookActivity.this, "评论的书籍不存在！", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValue.CommentReturnCode_serverWrong:
                    Toast.makeText( BookActivity.this, "服务器出错！", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValue.CommentReturnCode_success:
                    ed_comment.setText("");
                    Toast.makeText( BookActivity.this, "发表评论成功", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValue.CommentReturnCode_wrong:
                    Toast.makeText( BookActivity.this, "发表评论失败！", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    swipeRefreshLayout.setRefreshing(false);
                    CommentList cl = (CommentList) msg.getData().get("cmtList");
                    commentList.clear();
                    commentList.addAll(cl.commentsList);
                    commentAdapter.notifyDataSetChanged();
                    break;
                case 11:
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case 12:
                    book= (Book) msg.getData().get("BOOK");
                    book_content.setText(book.getContent());
                    book_comments.setText("评论"+book.getComments());
                    break;
                case 22:
                    closeProgressDialog();
                    Intent intent = new Intent(BookActivity.this,ActivityUserInfor.class);
                    intent.putExtra("USER",msg.getData().getSerializable("USER"));
                    startActivity(intent);
                    break;
            }

        }
    };

    //获取服务器上关于某本书籍的评论
    void getComments(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences(ConstValue.spAccount,MODE_PRIVATE);
                String account=preferences.getString(ConstValue.databaseUserId,"000");
                String pwd=preferences.getString(ConstValue.databaseUserpassword,"000");
                final CommentMessage cmt = new CommentMessage(account,"",pwd,book.getId());
                try{
                    Socket socket = new Socket(ConstValue.serverIp,ConstValue.serverPortgetComments);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(cmt);
                    out.flush();
                    ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    CommentList cl= (CommentList) in.readObject();
                    Message msg = new Message();
                    msg.what=4;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("cmtList",cl);
                    msg.setData(bundle);
                    socket.close();
                    myhandle.sendMessage(msg);
                }catch (IOException e){
                    Message msg = new Message();
                    msg.what=11;
                    myhandle.sendMessage(msg);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    void addToHope(){
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
        value.put("price",price);
        db.insert("hope",null,value);
        db.close();
        Toast.makeText(BookActivity.this, "添加愿望！", Toast.LENGTH_SHORT).show();
    }



    boolean has_Hope(){
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from hope where bookid="+book.getId(), null);
        if (cursor != null && cursor.getCount() > 0) {
            return true;
        }
        cursor.close();
        //5.关闭数据库
        db.close();
        return false;
    }



    void addToHas(){
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
        //Toast.makeText(BookActivity.this, "添加书架！", Toast.LENGTH_SHORT).show();
    }

    boolean has_Has(){
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from has where bookid="+book.getId(), null);
        if (cursor != null && cursor.getCount() > 0) {
            return true;
        }
        cursor.close();
        db.close();
        return false;
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




}
