package MyClass.MyAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import MyClass.Write;
import cloudbook.gaoch.com.R;

public class WriteItemAdapter extends RecyclerView.Adapter<WriteItemAdapter.ViewHolder> implements View.OnClickListener  {
    private List<Write> writeList =null;
    private Context mcontext;
    private WriteItemAdapter.OnItemClickListener mOnItemClickListener = null;
    public WriteItemAdapter(List<Write> writesList, Context context){
        this.writeList=writesList;
        mcontext=context;
    }

    @NonNull
    @Override
    public WriteItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_write,viewGroup,false);
       WriteItemAdapter.ViewHolder vh = new WriteItemAdapter.ViewHolder(itemView);
        itemView.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull WriteItemAdapter.ViewHolder viewHolder, int i) {
        viewHolder.write_date.setText(writeList.get(i).date.toString());
        viewHolder.write_content.setText(writeList.get(i).content);
        viewHolder.write_bookName.setText(writeList.get(i).book_name);

        viewHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        if(writeList !=null){
            return writeList.size();
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

    public void setOnItemClickListener(WriteItemAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView write_bookName,write_content,write_date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           write_bookName= itemView.findViewById(R.id.write_bookName);
            write_content= itemView.findViewById(R.id.write_content);
            write_date= itemView.findViewById(R.id.write_time);

        }
    }

    public void update(List<Write>writeList){
        this.writeList.addAll(writeList);
        notifyDataSetChanged();
    }
}
