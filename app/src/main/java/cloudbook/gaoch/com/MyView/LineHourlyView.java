package cloudbook.gaoch.com.MyView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import MyClass.Utility.Utility;

public class LineHourlyView extends View {
    private int maxValue,minValue;
    private List<Integer> dotList;
    private Paint mPaint;
    private float maxWidth,maxHeight;
    private float db;
    private float sp;
    public LineHourlyView(Context context){
        super(context);
        maxValue=40;
        minValue=0;
        mPaint = new Paint();
        dotList=new ArrayList<>();
        db=Utility.dp2px(context,1);
        sp=Utility.sp2px(context,1);
    }
    public LineHourlyView(Context context, AttributeSet attrs){
        super(context,attrs);
        maxValue=40;
        minValue=0;
        mPaint = new Paint();
        dotList=new ArrayList<Integer>();
    }
    public void addDots(List<Integer> list){
        if(!list.isEmpty()){
            Log.d("LineView:",list.size()+"");
            dotList.clear();
            dotList.addAll(list);
            maxValue=-100;minValue=100;
            for(int i=0;i<dotList.size();i++){
                int high=Integer.valueOf(list.get(i));
                maxValue=high>maxValue?high:maxValue;
                minValue=high<minValue?high:minValue;
            }
            maxValue+=5;
            minValue-=5;
        }

    }

    public Bitmap getLines(){
        maxWidth=getWidth();
        maxHeight=getHeight();
        Bitmap b=Bitmap.createBitmap((int) maxWidth,(int)maxHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        float width= maxWidth /(dotList.size());
        float height=(maxHeight/3)/(maxValue-minValue);  //每度的高度
        float startY=maxHeight/5;
        float startX=(float)(width/2.5);
        p.setStrokeWidth(Utility.dp2px(getContext(),2));
        Log.e("LineView",db+"");
        Log.e("LineView",sp+"");
        p.setAntiAlias(true);
        int textSize=Utility.sp2px(getContext(),10);
        p.setTextSize(textSize);
        int picSize=(int)Utility.dp2px(getContext(),20);
        for(int i=0;i<dotList.size()-1;i++){
            int high=Integer.valueOf(dotList.get(i));
            int high_next=Integer.valueOf(dotList.get(i+1));
            canvas.drawLine(startX+width*i,startY-(high-maxValue)*height,startX+width*(i+1),startY-(high_next-maxValue)*height,p);
            canvas.drawText(dotList.get(i)+"m",startX+width*i-textSize/2,startY-(high-maxValue)*height-(int)(textSize*1.5),p);
        }
        int last_index=dotList.size()-1;
        canvas.drawText(dotList.get(last_index)+"m",startX+width*(last_index)-textSize/2,startY-(Integer.valueOf(dotList.get( last_index))-maxValue)*height-(int)(textSize*1.5),p);

        canvas.drawBitmap(b,0,0,p);
        return b;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!dotList.isEmpty()){
            canvas.drawBitmap(getLines(),0,0,mPaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        Log.e("LineView", "---minimumWidth = " + minimumWidth + "");
        Log.e("LineView", "---minimumHeight = " + minimumHeight + "");
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int defaultWidth, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth =  getPaddingLeft() + getPaddingRight();
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        Log.d("LineView","Width:"+defaultWidth);
        return defaultWidth;
    }


    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
       switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight =  getPaddingTop() + getPaddingBottom();
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
                break;
        }
        Log.d("LineView","Height:"+defaultHeight);
        return defaultHeight;


    }

    public String data2simData(String origin_date){
        String odate[]=origin_date.split(" ");
        String date[] = odate[0].split("-");
        String date_month = date[date.length-2];
        String date_day = date[date.length-1];
        date_month = date_month.replaceFirst("^0*", "");
        date_day = date_day.replaceFirst("^0*","");
        return date_month+"."+date_day;
    }

    public String data2simData1(String origin_date){
        String odate[]=origin_date.split(" ");
        return odate[1];
    }

    public static void drawImage(Canvas canvas, Bitmap blt, int x, int y, int w, int h, int bx, int by) {
        Rect src = new Rect();// 图片 >>原矩形
        Rect dst = new Rect();// 屏幕 >>目标矩形

        src.left = bx;
        src.top = by;
        src.right = bx + w;
        src.bottom = by + h;

        dst.left = x;
        dst.top = y;
        dst.right = x + w;
        dst.bottom = y + h;
        // 画出指定的位图，位图将自动--》缩放/自动转换，以填补目标矩形
        // 这个方法的意思就像 将一个位图按照需求重画一遍，画后的位图就是我们需要的了
        canvas.drawBitmap(blt, null, dst, null);
        src = null;
        dst = null;
    }
}
