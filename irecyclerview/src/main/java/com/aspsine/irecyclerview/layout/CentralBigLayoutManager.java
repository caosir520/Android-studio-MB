package com.aspsine.irecyclerview.layout;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.wise.common.commonutils.LogUtil;

/**
 *
 * Created by Administrator on 2017/11/29/029.
 */

public class CentralBigLayoutManager extends RecyclerView.LayoutManager {
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //子文件布局
    private int mVerticealOffset ;//竖直偏移量
    private int mFristVisiPos; //屏幕可见的第一个View的Position
    private int mLastVisiPos ;//屏幕可见的最后一个View的Position

    private int topX;//中间的View的上边界显示位置x
    private int topY;//中间View的上边界显示的位置Y
    private int bottomX ;//下边界的显示的位置X
    private int bottomY;//下边界的显示位置的Y
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if(getItemCount()==0){ //页面可显示的Item位0
            detachAndScrapAttachedViews(recycler); //detach 回收所有的View， detach 回收，马上就会被用到
            return;
        }
        //state.isPreayout 是有动画的
        if(getChildCount()==0&&state.isPreLayout()){
            return ;
        }
        detachAndScrapAttachedViews(recycler); //缓存池的Item全部detach ，以便等会直接用
        mVerticealOffset = getHeight()/2;
        mFristVisiPos = 0;
        mLastVisiPos = 0;
        fillView(recycler,state);
    }
    //填充子View
    private void fillView(RecyclerView.Recycler recycler, RecyclerView.State state){
        //获得偏移量
        int topOffset =getPaddingTop();
        int leftOffset = getPaddingLeft();
        int rightOffset =getPaddingRight();
        //定义显示的子view的初始化的时候假设是 0到所有的-1 也就是全部的ziview
        int minPos = mFristVisiPos;
        //记录下选中框的位置：
        if(getChildCount()==0){
            View view =recycler.getViewForPosition(0);
            addView(view);
            measureChildWithMargins(view,0,0);
            int hight=getDecoratedMeasuredHeight(view);
            int width=getDecoratedMeasuredWidth(view);
            topX = ((getWidth()-width)/2);
            topY =((getHeight()-hight)/2);
            bottomX = topX+width;
            bottomY= topY+hight;
            detachAndScrapView(view,recycler);
        }


        //顺序addView
        for (int i = minPos; i < getItemCount(); i++) {
            View view = recycler.getViewForPosition(i);

            addView(view);
            measureChildWithMargins(view ,0,0); //计算宽高
            //获取这个View的宽高
            int hight = getDecoratedMeasuredHeight(view);
            int width = getDecoratedMeasuredWidth(view);

            //这个计算这个view应该放入的位子
            int startW = ((getWidth()-width)/2);
            int startH = ((getHeight()-hight)/2)+i*hight;
            int endW = startW+width;
            int endH = startH+hight;
            if(startH>getHeight()){
                //超出边界回收这个View
                removeAndRecycleView(view,recycler);
            }else{
                //布局这个view
                layoutDecoratedWithMargins(view,startW,startH,endW,endH);
                getDecoratedTop(view);
            }
        }

    }
    //是否能垂直滑动
    @Override
    public boolean canScrollVertically() {
        return true;
    }
    //滑动的具体长度
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int realOffset = dy ; //滑动的实际距离

        // dy > 0的时候是上滑，判断下边界范围，最后一条数据显示出来了，并且在-dy高度后小于默认高度
        if(realOffset>0){
            //更具最后一天数据来判断是否下边界
            View lastView= getChildAt(getChildCount()-1);
            if((getPosition(lastView))==getItemCount()-1){
                int lastBottom=getDecoratedBottom(lastView);
                if(lastBottom-dy<=bottomY){
                    realOffset=bottomY-(lastBottom-dy);
                }
            }
        }else{
            //上边界判断
            View fristView =getChildAt(0); //获得显示的第一条数据
            if(getPosition(fristView)==0){ //判断是不是适配器的第一条数据
               //获取这条数据top高度
                int fristTop = getDecoratedTop(fristView);
                if(fristTop-dy>=topY){
                    realOffset = topY-(fristTop-dy);
                }
            }
        }

        LogUtil.d(""+realOffset);
        offsetChildrenVertical(-realOffset);//其实是一种窗口的机制 ，
        return realOffset;
    }
}
