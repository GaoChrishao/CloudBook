package MyClass.MyAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import MyClass.Book;
import MyClass.Utility.LogToFile;
import cloudbook.gaoch.com.R;

public class BookItemAdapter extends RecyclerView.Adapter<BookItemAdapter.ViewHolder> implements View.OnClickListener  {
    private List<Book> bookList=null;
    private Context mcontext;
    private BookItemAdapter.OnItemClickListener mOnItemClickListener = null;
    public BookItemAdapter(List<Book> bookList,Context context){
        this.bookList=bookList;
        mcontext=context;
    }

    @NonNull
    @Override
    public BookItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_book,viewGroup,false);
       BookItemAdapter.ViewHolder vh = new BookItemAdapter.ViewHolder(itemView);
        itemView.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BookItemAdapter.ViewHolder viewHolder, int i) {
        viewHolder.book_writer.setText(bookList.get(i).getWriter());
        viewHolder.book_cat.setText("类别：实体书");
        viewHolder.book_price.setText("999人喜欢");
        viewHolder.book_name.setText(bookList.get(i).getName());
        RequestOptions options = new RequestOptions().placeholder(R.drawable.book_pic_default).error(R.drawable.book_pic_default).centerCrop();
        Glide.with(mcontext).load(bookList.get(i).getCover())
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(viewHolder.bookPic);
        viewHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        if(bookList!=null){
            return bookList.size();
        }else{
            return 0;
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    public void setOnItemClickListener(BookItemAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView book_name,book_cat,book_price,book_writer;
        ImageView bookPic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           book_name=itemView.findViewById(R.id.book_item_name);
            book_price=itemView.findViewById(R.id.book_item_cost);
            book_cat=itemView.findViewById(R.id.book_item_cat);
            book_writer=itemView.findViewById(R.id.book_item_writer);
            bookPic=itemView.findViewById(R.id.book_item_pic);
        }
    }

    public void update(List<Book>bookList){
        this.bookList.addAll(bookList);
        notifyDataSetChanged();
    }
}
