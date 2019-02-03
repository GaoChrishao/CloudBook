package cloudbook.gaoch.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import MyClass.Book;
import MyClass.MyAdapter.BookItemAdapter;
import MyClass.Utility.LogToFile;
import cloudbook.gaoch.com.BookActivity;
import cloudbook.gaoch.com.ConstValue;
import cloudbook.gaoch.com.MainActivity;
import cloudbook.gaoch.com.R;

public class FragmentStore extends Fragment {

    private List<Book> bookList;
    private RecyclerView recyclerView;
    private BookItemAdapter bookAdapter;
    private ViewPager viewPager;
    private ArrayList<View>pageView;

    // 滚动条图片
    private ImageView scrollbar;
    // 滚动条初始偏移量
    private int offset = 0;
    // 当前页编号
    private int currIndex = 0;
    // 滚动条宽度
    private int bmpW;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store,container,false);
        recyclerView=view.findViewById(R.id.recyclerView_book);
        bookList=new ArrayList<>();
        bookList.clear();
        bookList.add(new Book("2","刘慈欣",99,"三体","这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容",12,"http://gcloudpan.tk/books/santi.jpg",99));
        bookList.add(new Book("3","余华",99,"活着","这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容",12,"http://gcloudpan.tk/books/live.png",99));
        bookList.add(new Book("4","E.Knuth",99,"计算机程序设计艺术","这是简介这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容",12,"http://gcloudpan.tk/books/art.png",99));
        bookList.add(new Book("5","鲁迅",99,"朝花夕拾","这是简介这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容",12,"http://gcloudpan.tk/books/zhxs.png",88));
        bookList.add(new Book("6","东野圭吾",99,"解忧杂货店","这是简介这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容",12,"http://gcloudpan.tk/books/jyzhd.png",88));
        bookList.add(new Book("7","冷妍",99,"金瓶梅与世情小说","这是简介这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容这是简介内容",12,"http://gcloudpan.tk/books/jpm.png",88));

        bookAdapter=new BookItemAdapter(bookList,getContext());
        bookAdapter.setOnItemClickListener(new BookItemAdapter.OnItemClickListener() {
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
                            out.writeObject(new Book(bookList.get(position).getId()));
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
                            LogToFile.e("FragmentStore","获取图书错误");
                            LogToFile.e("FragmentStore",e.getMessage());
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            LogToFile.e("FragmentStore","获取图书错误");
                            LogToFile.e("FragmentStore",e.getMessage());
                        }

                    }
                }).start();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
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
}
