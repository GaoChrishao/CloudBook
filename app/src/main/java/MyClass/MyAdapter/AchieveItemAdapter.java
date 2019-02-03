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

import MyClass.Achievement;
import MyClass.Recom;
import MyClass.Utility.LogToFile;
import cloudbook.gaoch.com.R;

public class AchieveItemAdapter extends RecyclerView.Adapter<AchieveItemAdapter.ViewHolder> implements View.OnClickListener  {
    private List<Achievement> achList =null;
    private Context mcontext;
    private AchieveItemAdapter.OnItemClickListener mOnItemClickListener = null;
    public AchieveItemAdapter(List<Achievement> achList, Context context){
        this.achList = achList;
        mcontext=context;
    }

    @NonNull
    @Override
    public AchieveItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_recom,viewGroup,false);
       AchieveItemAdapter.ViewHolder vh = new AchieveItemAdapter.ViewHolder(itemView);
        itemView.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AchieveItemAdapter.ViewHolder viewHolder, int i) {

        viewHolder.ach_content.setText(achList.get(i).getContent());
        RequestOptions options = new RequestOptions().placeholder(R.drawable.cj_default).error(R.drawable.cj_default).centerCrop();
        Glide.with(mcontext).load(achList.get(i).getPic_path())
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
                }).into(viewHolder.ach_pic);


        viewHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        if(achList !=null){
            return achList.size();
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

    public void setOnItemClickListener(AchieveItemAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ach_content;
        ImageView ach_pic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ach_content =itemView.findViewById(R.id.item_achieve_content);
            ach_pic =itemView.findViewById(R.id.item_achieve_pic);


        }
    }

    public void update(List<Achievement>achList){
        this.achList.addAll(achList);
        notifyDataSetChanged();
    }
}
