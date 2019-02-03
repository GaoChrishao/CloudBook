package MyClass.MyAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import MyClass.Focus;
import MyClass.Utility.FocusTime;
import cloudbook.gaoch.com.R;

public class FocusItemAdapter extends RecyclerView.Adapter<FocusItemAdapter.ViewHolder> implements View.OnClickListener  {
    private List<FocusTime> focusList =null;
    private Context mcontext;
    private FocusItemAdapter.OnItemClickListener mOnItemClickListener = null;
    public FocusItemAdapter(List<FocusTime> focusList, Context context){
        this.focusList=focusList;
        mcontext=context;
    }

    @NonNull
    @Override
    public FocusItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_focus,viewGroup,false);
       FocusItemAdapter.ViewHolder vh = new FocusItemAdapter.ViewHolder(itemView);
        itemView.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.focus_endtime.setText(new Timestamp(focusList.get(i).end_time).getHours()+":"+new Timestamp(focusList.get(i).end_time).getMinutes()+":"+new Timestamp(focusList.get(i).end_time).getSeconds());
        viewHolder.focus_starttime.setText(new Timestamp(focusList.get(i).start_time).getHours()+":"+new Timestamp(focusList.get(i).start_time).getMinutes()+":"+new Timestamp(focusList.get(i).start_time).getMinutes());
        viewHolder.focus_bookName.setText(focusList.get(i).getBookName());
        viewHolder.focus_date.setText( new Date(focusList.get(i).start_time).toString());
        int allSeconds= (int) ((focusList.get(i).end_time-focusList.get(i).start_time)/1000);
        int minute=allSeconds/60;
        int hour=minute/60;
        viewHolder.focus_duration.setText("阅读时长："+minute+"分钟");
        viewHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        if(focusList !=null){
            return focusList.size();
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

    public void setOnItemClickListener(FocusItemAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView focus_bookName, focus_starttime, focus_endtime,focus_date,focus_duration;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           focus_bookName = itemView.findViewById(R.id.item_focus_bookname);
            focus_starttime = itemView.findViewById(R.id.item_focus_startTime);
            focus_endtime = itemView.findViewById(R.id.item_focus_endTime);
            focus_date = itemView.findViewById(R.id.item_focus_date);
            focus_duration=itemView.findViewById(R.id.item_focus_duration);

        }
    }

    public void update(List<FocusTime>focusList){
        this.focusList.addAll(focusList);
        notifyDataSetChanged();
    }



}
