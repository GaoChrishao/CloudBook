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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import MyClass.CommentMessage;
import MyClass.Recom;
import MyClass.Utility.LogToFile;
import cloudbook.gaoch.com.ConstValue;
import cloudbook.gaoch.com.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecomItemAdapter extends RecyclerView.Adapter<RecomItemAdapter.ViewHolder> implements View.OnClickListener  {
    private List<Recom> recomList =null;
    private Context mcontext;
    private RecomItemAdapter.OnItemClickListener mOnItemClickListener = null;
    public RecomItemAdapter(List<Recom> recomList, Context context){
        this.recomList = recomList;
        mcontext=context;
    }

    @NonNull
    @Override
    public RecomItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_recom,viewGroup,false);
       RecomItemAdapter.ViewHolder vh = new RecomItemAdapter.ViewHolder(itemView);
        itemView.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecomItemAdapter.ViewHolder viewHolder, int i) {

        viewHolder.rom_bookname.setText("--《"+recomList.get(i).book_name+"》");
        viewHolder.rom_content.setText(recomList.get(i).content);


        viewHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        if(recomList !=null){
            return recomList.size();
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

    public void setOnItemClickListener(RecomItemAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView rom_content, rom_bookname;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rom_content =itemView.findViewById(R.id.recom_content);
            rom_bookname =itemView.findViewById(R.id.recom_bookName);


        }
    }

    public void update(List<Recom>recomList){
        this.recomList.addAll(recomList);
        notifyDataSetChanged();
    }
}
