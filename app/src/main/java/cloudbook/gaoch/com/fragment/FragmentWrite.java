package cloudbook.gaoch.com.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import MyClass.Blur;
import MyClass.BookHas;
import MyClass.MyAdapter.BookItemReadingAdapter;
import MyClass.MyAdapter.WriteItemAdapter;
import MyClass.Utility.LocalDatabaseHelper;
import MyClass.Write;
import cloudbook.gaoch.com.BookActivity;
import cloudbook.gaoch.com.ConstValue;
import cloudbook.gaoch.com.MainActivity;
import cloudbook.gaoch.com.MyBookActivity;
import cloudbook.gaoch.com.R;
import cloudbook.gaoch.com.WriteActivity;

public class FragmentWrite extends Fragment {
    private RecyclerView recyclerView,recyclerView_write;
    private List<BookHas>bookList;
    public List<Write>writeList;
    public BookItemReadingAdapter adapter_has;
    private WriteItemAdapter adapter_writer;
    private int hasBlured_top1=0,hasBlured_top2=0,hasBlured_top3=0,hasBlured_top4=0;
    private LinearLayout layout_1, layout_2, layout_3,layout_4;
    private FloatingActionButton floatingActionButton;
    private LocalDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write,container,false);
        recyclerView=view.findViewById(R.id.fragment_write_mybook);
        recyclerView_write=view.findViewById(R.id.fragment_write_rv);
        layout_1=view.findViewById(R.id.fragment_write_layout1);
        layout_2=view.findViewById(R.id.fragment_write_layout2);
        floatingActionButton = view.findViewById(R.id.fragment_write_writebtn);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bookList=new ArrayList<>();
        bookList.clear();
        adapter_has =new BookItemReadingAdapter(bookList,getContext());
        adapter_has.setOnItemClickListener(new BookItemReadingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(),MyBookActivity.class);
                intent.putExtra("BOOK",bookList.get(position));
                startActivity(intent);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setAdapter(adapter_has);
        recyclerView.setLayoutManager(layoutManager);


        /**
         * 笔记心得
         */
        writeList=new ArrayList<>();
        writeList.clear();

        dbHelper=new LocalDatabaseHelper(getContext(),ConstValue.LocalDatabaseName,null,LocalDatabaseHelper.NEW_VERSION);
        adapter_writer = new WriteItemAdapter(writeList,getContext());
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        recyclerView_write.setAdapter(adapter_writer);
        recyclerView_write.setLayoutManager(layoutManager1);

        adapter_writer.setOnItemClickListener(new WriteItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Write write = writeList.get(position);
                if(write.book_id.equals("0")){
                    Toast.makeText(getContext(), "无效书籍！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(),WriteActivity.class);

                //更新操作
                Bundle bundle = new Bundle();
                bundle.putSerializable("write",write);
                intent.putExtra("write",bundle);
                intent.putExtra("type",1);
                startActivity(intent);
            }
        });


        if(((MainActivity)getActivity()).primaryColor!=0){
            floatingActionButton.setBackgroundColor(((MainActivity)getActivity()).primaryColor);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),WriteActivity.class);
                intent.putExtra("type",2);
                startActivityForResult(intent,1);


            }
        });
        setBlur();
        refreshWrite();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 2:
                if(requestCode==1){
                    Write write = (Write) data.getSerializableExtra("WRITE");
                    //writeList.add(0,write);
                    Log.e("FragmentWriter",write.book_name);
                    //adapter_writer.notifyDataSetChanged();
                    Log.e("FragmentWriter","get result");
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshHasList();
        refreshWrite();
        Log.e("FragmentWrite","OnResume");
    }

    public void setBlur(){
        final View view_test=((MainActivity)getActivity()).drawer;
        if(((MainActivity) getActivity()).getIsBlur()){
            layout_2.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    layout_2.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top1){
                        Blur.blur(view_test, layout_2,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top1=location[1];
                    }

                    return true;
                }
            });
            layout_1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    layout_1.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top2){
                        Blur.blur(view_test, layout_1,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top2=location[1];
                    }

                    return true;
                }
            });
        }
    }

    void refreshWrite(){
        writeList.clear();
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.query("write",null,null,null,null,null,null);
        String myid=getActivity().getSharedPreferences(ConstValue.spAccount,Context.MODE_PRIVATE).getString(ConstValue.databaseUserId,"0");
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
        if(writeList.size()<1)writeList.add(new Write("0","《操作指南》",new Date(System.currentTimeMillis()),"点击右下角的画笔既可以添加记录~\n祝您使用愉快~"));
        adapter_writer.notifyDataSetChanged();
    }


    void refreshHasList(){
        if(bookList.size()>0) bookList.clear();
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
                bookList.add(book);
                Log.e("CloudBook",bookid+" "+bookName+" "+allPages+" "+readPages+" "+time+" "+writer+" "+bookName );
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        adapter_has.notifyDataSetChanged();
    }


}
