package cloudbook.gaoch.com;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.util.ArrayList;
import java.util.List;

import MyClass.Blur;
import MyClass.BookHas;
import MyClass.MyAdapter.WriteItemAdapter;
import MyClass.Utility.LocalDatabaseHelper;
import MyClass.Utility.LogToFile;
import MyClass.Write;


public class MyBookActivity extends AppCompatActivity {
    LinearLayout layout_main;
    RecyclerView rv_1;
    ImageView iv_book;
    TextView tv_name,tv_all,tv_read,tv_time,tv_writer;
    private LocalDatabaseHelper dbHelper;
    private WriteItemAdapter adapter_writer;
    public List<Write> writeList;
    public BookHas boook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybook);
        initView();

        Intent intent=getIntent();
        boook= (BookHas) intent.getSerializableExtra("BOOK");
        if(boook!=null){
            tv_all.setText("总页数:"+boook.getAllPages());
            tv_read.setText("已经阅读页数："+boook.getReadPages());
            tv_name.setText(boook.getName());
            tv_writer.setText(boook.getWriter());
            tv_time.setText("阅读总时长：30m");
        }

        //加载网络图片
        RequestOptions options = new RequestOptions().placeholder(R.drawable.book_pic_default).error(R.drawable.book_pic_default).centerCrop();
        Glide.with(MyBookActivity.this).load(boook.getCover())
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        iv_book.setBackground(getDrawable(R.drawable.book_pic_default));
                        makeBlur(iv_book.getBackground());
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        iv_book.setBackground(null);
                        makeBlur(resource);
                        return false;
                    }
                }).into(iv_book);
        refreshWrite();

    }

    void initView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        layout_main=findViewById(R.id.activity_mybook_layout_main);
        rv_1=findViewById(R.id.activity_mybook_rv);
        iv_book=findViewById(R.id.activity_mybook_iv_book);
        tv_all=findViewById(R.id.activity_mybook_allPages);
        tv_read=findViewById(R.id.activity_mybook_readPages);
        tv_name=findViewById(R.id.activity_mybook_bookname);
        tv_writer=findViewById(R.id.activity_mybook_writer);
        tv_time=findViewById(R.id.activity_mybook_readTime);



        /**
         * 笔记心得
         */
        writeList=new ArrayList<>();
        writeList.clear();

        dbHelper=new LocalDatabaseHelper(this,ConstValue.LocalDatabaseName,null,LocalDatabaseHelper.NEW_VERSION);
        adapter_writer = new WriteItemAdapter(writeList,this);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
        rv_1.setAdapter(adapter_writer);
        rv_1.setLayoutManager(layoutManager1);

        adapter_writer.setOnItemClickListener(new WriteItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Write write = writeList.get(position);
                if(write.book_id.equals("0")){
                    Toast.makeText(getApplicationContext(), "无效书籍！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(),WriteActivity.class);

                //更新操作
                Bundle bundle = new Bundle();
                bundle.putSerializable("write",write);
                intent.putExtra("write",bundle);
                intent.putExtra("type",1);
                startActivity(intent);
            }
        });
    }


    void refreshWrite(){
        writeList.clear();
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.query("write", null, "bookid=?", new String[] { boook.getId()}, null, null, null);
        String myid=getSharedPreferences(ConstValue.spAccount,Context.MODE_PRIVATE).getString(ConstValue.databaseUserId,"0");
        if(cursor.moveToFirst()){
            do{
                String id=cursor.getString(cursor.getColumnIndex("id"));
                String bookid=cursor.getString(cursor.getColumnIndex("bookid"));
                String userid=cursor.getString(cursor.getColumnIndex("userid"));
                String bookName=cursor.getString(cursor.getColumnIndex("bookname"));
                String time=cursor.getString(cursor.getColumnIndex("time"));
                String content=cursor.getString(cursor.getColumnIndex("content"));
                Write writer=new Write(bookid,bookName,Date.valueOf(time),content);
                writer.id=id;
                writer.user_id=userid;
                System.out.println(id+" "+bookid+" "+userid+" "+content+" "+time);
                if(userid.equals(myid)){
                    writeList.add(writer);
                }

            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        adapter_writer.notifyDataSetChanged();
    }

    void makeBlur(Drawable drawable){
        Bitmap bd = ((BitmapDrawable)drawable).getBitmap();
        Bitmap bitmap= Bitmap.createBitmap(bd,0,0,bd.getWidth(),bd.getHeight());
        layout_main.setBackground(new BitmapDrawable(getResources(),Blur.blurBitmap(this,bitmap,20)));
    }




}
