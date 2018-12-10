package com.buntorotandjaja.www.capstoneproject;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class ThreeTwoImageView extends AppCompatImageView {
    public ThreeTwoImageView(Context context) {
        super(context);
    }

    public ThreeTwoImageView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
    }

    public ThreeTwoImageView(Context context, AttributeSet attrSet, int defStyle) {
        super(context, attrSet, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int threeTwoHeight = MeasureSpec.getSize(widthMeasureSpec) * 2 / 3;
        int threeTwoHeightSpec = MeasureSpec.makeMeasureSpec(threeTwoHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, threeTwoHeightSpec);
    }
}
