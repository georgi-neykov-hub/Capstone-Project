package com.neykov.podcastportal.view.widget;

import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
    }

    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;

    private int spanCount;
    private int horizontalEndSpace;
    private int horizontalSpace;
    private int verticalSpace;
    private int verticalEndSpace;
    private final
    @Direction
    int direction;

    public GridSpaceItemDecoration(int spanCount, @Direction int direction) {
        this.spanCount = spanCount;
        this.direction = direction;
    }

    public void setInnerSpacing(int pixels) {
        setHorizontalInnerSpacing(pixels);
        setVerticalInnerSpacing(pixels);
    }

    public void setHorizontalInnerSpacing(int spacePixels) {
        this.horizontalSpace = spacePixels;
    }

    public void setVerticalInnerSpacing(int spacePixels) {
        this.verticalSpace = spacePixels;
    }

    public void setHorizontalEndSpacing(int spacePixels) {
        this.horizontalEndSpace = spacePixels;
    }

    public void setVerticalEndSpacing(int spacePixels) {
        this.verticalEndSpace = spacePixels;
    }


    public void setHorizontalInnerSpacing(Resources resources, @DimenRes int dimenResId) {
        this.horizontalSpace = resources.getDimensionPixelSize(dimenResId);
    }

    public void setVerticalInnerSpacing(Resources resources, @DimenRes int dimenResId) {
        this.verticalSpace = resources.getDimensionPixelSize(dimenResId);
    }

    public void setHorizontalEndSpacing(Resources resources, @DimenRes int dimenResId) {
        this.horizontalEndSpace = resources.getDimensionPixelSize(dimenResId);
    }

    public void setVerticalEndSpacing(Resources resources, @DimenRes int dimenResId) {
        this.verticalEndSpace = resources.getDimensionPixelSize(dimenResId);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view); // item position

        int column = position % spanCount; // item column

        outRect.left = column == 0 ? horizontalEndSpace : horizontalSpace;
        outRect.right = (column == spanCount - 1) ? horizontalEndSpace : 0;

        outRect.top = position < spanCount ? verticalEndSpace : verticalSpace;

        int itemCount = parent.getAdapter().getItemCount();
        int lastRow = (itemCount - 1) / spanCount;
        int row = position / spanCount;
        if (row == lastRow) {
            outRect.bottom = verticalEndSpace;
        } else {
            outRect.bottom = 0;
        }
    }
}