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

import java.text.DecimalFormat;
import java.util.List;

import MyClass.Blur;
import MyClass.BookHas;
import MyClass.Utility.LogToFile;
import cloudbook.gaoch.com.R;

public class BookItemReadingAdapter extends RecyclerView.Adapter<BookItemReadingAdapter.ViewHolder> implements View.OnClickListener  {
    private List<BookHas> bookList=null;
    private Context mcontext;
    private BookItemReadingAdapter.OnItemClickListener mOnItemClickListener = null;
    public BookItemReadingAdapter(List<BookHas> bookList, Context context){
        this.bookList=bookList;
        mcontext=context;
    }

    @NonNull
    @Override
    public BookItemReadingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_book_reading,viewGroup,false);
        BookItemReadingAdapter.ViewHolder vh = new BookItemReadingAdapter.ViewHolder(itemView);
        itemView.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final BookItemReadingAdapter.ViewHolder viewHolder, int i) {
        viewHolder.book_name.setText(bookList.get(i).getName());
        float hasRead=bookList.get(i).getReadPages()/bookList.get(i).getAllPages();
        DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p=decimalFormat.format(hasRead);//format 返回的是字符串
        viewHolder.book_hasread.setText(p+"%");

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

    public void setOnItemClickListener(BookItemReadingAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView book_name;
        TextView book_hasread;
        ImageView bookPic;
        LinearLayout book_words_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            book_name=itemView.findViewById(R.id.mybook_name);
            bookPic=itemView.findViewById(R.id.mybook_pic);
            book_words_layout=itemView.findViewById(R.id.mybook_words_bkg);
            book_hasread=itemView.findViewById(R.id.mybook_hasread);
        }
    }
    public void update(List<BookHas>bookList){
        this.bookList.addAll(bookList);
        notifyDataSetChanged();
    }

}
