package MyClass.MyAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import MyClass.Blur;
import MyClass.Book;
import MyClass.Utility.LogToFile;
import cloudbook.gaoch.com.R;

public class BookItemHopeAdapter extends RecyclerView.Adapter<BookItemHopeAdapter.ViewHolder> implements View.OnClickListener  {
    private List<Book> bookList=null;
    private Context mcontext;
    private BookItemHopeAdapter.OnItemClickListener mOnItemClickListener = null;
    public BookItemHopeAdapter(List<Book> bookList, Context context){
        this.bookList=bookList;
        mcontext=context;
    }

    @NonNull
    @Override
    public BookItemHopeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_book_hope,viewGroup,false);
        BookItemHopeAdapter.ViewHolder vh = new BookItemHopeAdapter.ViewHolder(itemView);
        itemView.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final BookItemHopeAdapter.ViewHolder viewHolder, int i) {
        viewHolder.book_name.setText(bookList.get(i).getName());
        viewHolder.book_writer.setText(bookList.get(i).getWriter());
        viewHolder.book_price.setText(bookList.get(i).getPrice()+"￥");
        RequestOptions options = new RequestOptions().placeholder(R.drawable.book_pic_default).error(R.drawable.book_pic_default).centerCrop();
        Glide.with(mcontext).load(bookList.get(i).getCover())
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("BookActivity","加载失败");
                        LogToFile.e("BookActivity","加载网络图片失败");
                        Bitmap bd =((BitmapDrawable) viewHolder.bookPic.getBackground()).getBitmap();
                        float height2w=0.5f;
                        Bitmap bitmap= Bitmap.createBitmap(bd,bd.getWidth()/4,bd.getHeight()/3,bd.getWidth()/2,(int)(bd.getWidth()/2*height2w));
                        viewHolder.book_words_layout.setBackground(new BitmapDrawable(mcontext.getResources(),Blur.blurBitmap(mcontext,bitmap,20)));
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Bitmap bd = ((BitmapDrawable)resource).getBitmap();
                        float height2w=0.5f;
                        Bitmap bitmap= Bitmap.createBitmap(bd,bd.getWidth()/4,bd.getHeight()/3,bd.getWidth()/2,(int)(bd.getWidth()/2*height2w));
                        viewHolder.book_words_layout.setBackground(new BitmapDrawable(mcontext.getResources(),Blur.blurBitmap(mcontext,bitmap,20)));
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

    public void setOnItemClickListener(BookItemHopeAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView book_name;
        TextView book_writer;
        TextView book_price;
        ImageView bookPic;
        LinearLayout book_words_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            book_name=itemView.findViewById(R.id.book_item_hope_name);
            bookPic=itemView.findViewById(R.id.book_item_hope_pic);
            book_writer=itemView.findViewById(R.id.book_item_hope_writer);
            book_price=itemView.findViewById(R.id.book_item_hope_cost);
            book_words_layout=itemView.findViewById(R.id.book_item_hope_bkg);
        }
    }
    public void update(List<Book>bookList){
        this.bookList.addAll(bookList);
        notifyDataSetChanged();
    }

}
