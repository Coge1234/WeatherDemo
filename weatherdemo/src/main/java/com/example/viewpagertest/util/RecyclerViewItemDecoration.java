package com.example.viewpagertest.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/2/12.
 */

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
    public static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private final Drawable mDrawable;

    public RecyclerViewItemDecoration(Context context) {

        TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDrawable = a.getDrawable(0);
        //记得回收资源
        a.recycle();
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        int left = parent.getLeft();
        int right = parent.getRight();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int top = child.getBottom();
            int bottom = top + mDrawable.getIntrinsicHeight();
            //设置参数，并且画出来
            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(c);
        }


    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, mDrawable.getIntrinsicHeight());
    }
}
