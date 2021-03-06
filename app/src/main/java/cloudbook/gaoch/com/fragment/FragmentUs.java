package cloudbook.gaoch.com.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import MyClass.Blur;
import cloudbook.gaoch.com.ConstValue;
import cloudbook.gaoch.com.MainActivity;
import cloudbook.gaoch.com.R;

public class FragmentUs extends Fragment {
    private int hasBlured_top1=0,hasBlured_top2=0,hasBlured_top3=0,hasBlured_top4=0;
    private LinearLayout layout_1, layout_2, layout_3,layout_4;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_us,container,false);
        layout_1=view.findViewById(R.id.us_layout_1);
        layout_2=view.findViewById(R.id.us_layout_2);
        layout_3=view.findViewById(R.id.us_layout_3);
        layout_4=view.findViewById(R.id.us_layout_4);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setBlur();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void setBlur(){
        final View view_test=((MainActivity)getActivity()).drawer;
        if(((MainActivity) getActivity()).getIsBlur()){
            layout_1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    layout_1.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top1){
                        Blur.blur(view_test, layout_1,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top1=location[1];
                    }

                    return true;
                }
            });
            layout_2.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    layout_2.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top2){
                        Blur.blur(view_test, layout_2,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top2=location[1];
                    }

                    return true;
                }
            });
            layout_3.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    layout_3.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top3){
                        Blur.blur(view_test, layout_3,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top3=location[1];
                    }
                    return true;
                }
            });
            layout_4.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int []location=new int[2];
                    layout_4.getLocationInWindow(location);
                    if(location[1]!=hasBlured_top4){
                        Blur.blur(view_test, layout_4,ConstValue.radius,ConstValue.scaleFactor,ConstValue.RoundCorner);
                        hasBlured_top4=location[1];
                    }
                    return true;
                }
            });
        }
    }
}
