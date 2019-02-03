package cloudbook.gaoch.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import MyClass.Book;
import MyClass.CommentMessage;
import MyClass.MyAdapter.CommentItemAdapter;
import cloudbook.gaoch.com.BookActivity;
import cloudbook.gaoch.com.MainActivity;
import cloudbook.gaoch.com.R;

public class FragmentComments extends Fragment {

    private List<CommentMessage> commentsList;
    private RecyclerView recyclerView;
    private CommentItemAdapter adapter;
    private ViewPager viewPager;
    private ArrayList<View>pageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments,container,false);
        recyclerView=view.findViewById(R.id.rank_comments);
        commentsList=new ArrayList<>();
        commentsList.clear();


        for(int i=0;i<15;i++){
            commentsList.add(new CommentMessage("书籍"+i,"这是示范评论内容"+i,"","1",new Date(System.currentTimeMillis())));
        }


        adapter=new CommentItemAdapter(commentsList,getContext());
        adapter.setOnItemClickListener(new CommentItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
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
