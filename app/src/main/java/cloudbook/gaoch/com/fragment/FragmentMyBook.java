package cloudbook.gaoch.com.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import MyClass.MyAdapter.BookItemHopeAdapter;
import MyClass.MyAdapter.BookItemReadingAdapter;
import MyClass.Utility.LogToFile;
import cloudbook.gaoch.com.BookActivity;
import cloudbook.gaoch.com.ConstValue;
import cloudbook.gaoch.com.MainActivity;
import cloudbook.gaoch.com.R;

public class FragmentMyBook extends Fragment {
    private RecyclerView recyclerView_hope;
    private List<Book> hopeList;
    private int hasBlured_top1=0,hasBlured_top2=0,hasBlured_top3=0,hasBlured_top4=0;
    private LinearLayout layout_1, layout_2, layout_3,layout_4;

    private RecyclerView recyclerView_has;
    private List<BookHas> bookList_has;
    private BookItemReadingAdapter adapter_has;
    private BookItemHopeAdapter adapter_hope;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mybook,container,false);
        recyclerView_hope =view.findViewById(R.id.fragment_mybook_rv_hope);
        layout_1=view.findViewById(R.id.fragment_mybook_layout1);
        layout_2=view.findViewById(R.id.fragment_mybook_layout2);
        recyclerView_has=view.findViewById(R.id.fragment_mybook_has);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hopeList =new ArrayList<>();
        adapter_hope =new BookItemHopeAdapter(hopeList,getContext());
        adapter_hope.setOnItemClickListener(new BookItemHopeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                ((MainActivity)getActivity()).showProgressDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Socket socket = null;
                        try {
                            socket = new Socket(ConstValue.serverIp,ConstValue.serverPortBook);
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            out.writeObject(new Book(hopeList.get(position).getId()));
                            out.flush();
                            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                            Book completeBook= (Book) in.readObject();
                            Message msg = new Message();
                            if(completeBook.getId().equals("0")){
                                msg.what=11;
                            }else{
                                msg.what=12;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("BOOK",completeBook);
                                msg.setData(bundle);
                            }
                            myHandler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogToFile.e("FragmentMyBook","获取图书错误");
                            LogToFile.e("FragmentMyBook",e.getMessage());
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            LogToFile.e("FragmentMyBook","获取图书错误");
                            LogToFile.e("FragmentMyBook",e.getMessage());
                        }

                    }
                }).start();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_hope.setAdapter(adapter_hope);
        recyclerView_hope.setLayoutManager(layoutManager);



        bookList_has=new ArrayList<>();
        adapter_has=new BookItemReadingAdapter(bookList_has,getContext());
        adapter_has.setOnItemClickListener(new BookItemReadingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                ((MainActivity)getActivity()).showProgressDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Socket socket = null;
                        try {
                            socket = new Socket(ConstValue.serverIp,ConstValue.serverPortBook);
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            out.writeObject(new Book(bookList_has.get(position).getId()));
                            out.flush();
                            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                            Book completeBook= (Book) in.readObject();
                            Message msg = new Message();
                            if(completeBook.getId().equals("0")){
                                msg.what=11;
                            }else{
                                msg.what=12;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("BOOK",completeBook);
                                msg.setData(bundle);
                            }
                            myHandler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogToFile.e("FragmentMain","获取图书错误");
                            LogToFile.e("FragmentMain",e.getMessage());
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            LogToFile.e("FragmentMain","获取图书错误");
                            LogToFile.e("FragmentMain",e.getMessage());
                        }

                    }
                }).start();
            }
        });
        RecyclerView.LayoutManager layoutManager_has = new LinearLayoutManager(getContext());
        ((LinearLayoutManager) layoutManager_has).setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_has.setAdapter(adapter_has);
        recyclerView_has.setLayoutManager(layoutManager_has);

        //refreshHopeList();
        setBlur();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshHopeList();
        refreshHasList();
    }

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 11:
                    Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).closeProgressDialog();
                    break;
                case 12:
                    Intent intent = new Intent(getContext(),BookActivity.class);
                    intent.putExtra("BOOK",(Book)(msg.getData().getSerializable("BOOK")));
                    startActivity(intent);
                    ((MainActivity)getActivity()).closeProgressDialog();
                    break;
            }
        }
    };


    public void setBlur(){
        final View view_test=((MainActivity)getActivity()).drawer;
        if(((MainActivity) getActivity()).getIsBlur()){
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
        }
    }



    void refreshHopeList(){
        if(hopeList.size()>0) hopeList.clear();
        SQLiteDatabase db =((MainActivity)getActivity()).dbHelper.getReadableDatabase();
        Cursor cursor = db.query("hope",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                //String id=cursor.getString(cursor.getColumnIndex("id"));
                String bookid=cursor.getString(cursor.getColumnIndex("bookid"));
                String userid=cursor.getString(cursor.getColumnIndex("userid"));
                String bookName=cursor.getString(cursor.getColumnIndex("bookname"));
                String price=cursor.getString(cursor.getColumnIndex("price"));
                String writer=cursor.getString(cursor.getColumnIndex("writer"));
                Book book= new Book(bookid);
                book.setName(bookName);
                book.setWriter(writer);
                book.setPrice(Float.valueOf(price));
                book.setCover(cursor.getString(cursor.getColumnIndex("cover")));
                hopeList.add(book);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        adapter_hope.notifyDataSetChanged();
    }


    void refreshHasList(){
        if(bookList_has.size()>0) bookList_has.clear();
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
                bookList_has.add(book);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        adapter_has.notifyDataSetChanged();
    }
}
