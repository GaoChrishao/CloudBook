package cloudbook.gaoch.com.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import MyClass.Blur;
import cloudbook.gaoch.com.ConstValue;
import cloudbook.gaoch.com.MainActivity;
import cloudbook.gaoch.com.R;

public class FragmentRank extends Fragment {
    FrameLayout frameLayout;
    private Button button1,button2;
    private int hasBlured_top1=0,hasBlured_top2=0,hasBlured_top3=0,hasBlured_top4=0;
    private LinearLayout layout_1, layout_2, layout_3,layout_4;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank,container,false);
        frameLayout=view.findViewById(R.id.rank_frameLayout);
        button1=view.findViewById(R.id.btn_rank_book);
        button2=view.findViewById(R.id.btn_rank_comments);
        layout_1=view.findViewById(R.id.rank_layout1);
        layout_2=view.findViewById(R.id.rank_layout2);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager manager=getActivity().getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.rank_frameLayout,new FragmentStore());
        transaction.commit();
        button1.setBackgroundResource(R.drawable.background_btn_2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setBackgroundResource(R.drawable.background_btn_2);
                button2.setBackgroundResource(R.drawable.background_btn_2_1);
                FragmentManager manager=getActivity().getSupportFragmentManager();
                FragmentTransaction transaction=manager.beginTransaction();
                transaction.replace(R.id.rank_frameLayout,new FragmentStore());
                transaction.commit();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                button2.setBackgroundResource(R.drawable.background_btn_2_2);
                button1.setBackgroundResource(R.drawable.background_btn_1);
                FragmentManager manager=getActivity().getSupportFragmentManager();
                FragmentTransaction transaction=manager.beginTransaction();
                transaction.replace(R.id.rank_frameLayout,new FragmentComments());
                transaction.commit();
            }
        });

        setBlur();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setBlur(){
        final View view_test=((MainActivity)getActivity()).drawer;
        if(((MainActivity) getActivity()).getIsBlur()){
            layout_2.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    layout_2.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top1){
                        Blur.blur(view_test, layout_2,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top1=location[1];
                    }

                    return true;
                }
            });
            layout_1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    layout_1.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top2){
                        Blur.blur(view_test, layout_1,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top2=location[1];
                    }

                    return true;
                }
            });
        }
    }
}
