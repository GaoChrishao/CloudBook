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

import MyClass.CommentMessage;
import MyClass.Utility.LogToFile;
import cloudbook.gaoch.com.ConstValue;
import cloudbook.gaoch.com.MainActivity;
import cloudbook.gaoch.com.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentItemAdapter extends RecyclerView.Adapter<CommentItemAdapter.ViewHolder> implements View.OnClickListener  {
    private List<CommentMessage> commentList =null;
    private Context mcontext;
    private CommentItemAdapter.OnItemClickListener mOnItemClickListener = null;
    public CommentItemAdapter(List<CommentMessage> commentList, Context context){
        this.commentList =commentList;
        mcontext=context;
    }

    @NonNull
    @Override
    public CommentItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.comment_item,viewGroup,false);
       CommentItemAdapter.ViewHolder vh = new CommentItemAdapter.ViewHolder(itemView);
        itemView.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentItemAdapter.ViewHolder viewHolder, int i) {
        
        viewHolder.cmt_date.setText(commentList.get(i).getDate().toString());
        viewHolder.cmt_speaker.setText(commentList.get(i).getAccount());
        viewHolder.cmt_content.setText(commentList.get(i).getComment());

        RequestOptions options = new RequestOptions().placeholder(R.drawable.user_pic).error(R.drawable.user_pic).centerCrop();
        Glide.with(mcontext).load(ConstValue.serverUserPic+commentList.get(i).getAccount()+".png")
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
                }).into(viewHolder.cmt_pic);

        viewHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        if(commentList !=null){
            return commentList.size();
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

    public void setOnItemClickListener(CommentItemAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView cmt_speaker, cmt_date,cmt_content;
        CircleImageView cmt_pic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cmt_speaker =itemView.findViewById(R.id.cmt_userName);
            cmt_date =itemView.findViewById(R.id.cmt_date);
            cmt_content=itemView.findViewById(R.id.cmt_content);
            cmt_pic=itemView.findViewById(R.id.cmt_pic);

        }
    }

    public void update(List<CommentMessage>commentList){
        this.commentList.addAll(commentList);
        notifyDataSetChanged();
    }
}
