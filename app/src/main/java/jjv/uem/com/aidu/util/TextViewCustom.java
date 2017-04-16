package jjv.uem.com.aidu.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Victor on 16/04/2017.
 */

public class TextViewCustom extends android.support.v7.widget.AppCompatTextView {
    public TextViewCustom(Context context) {
        super(context);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/BubblerOne-Regular.ttf");
        this.setTypeface(tf);
    }

    public TextViewCustom(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/BubblerOne-Regular.ttf");
        this.setTypeface(tf);
    }

    public TextViewCustom(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/BubblerOne-Regular.ttf");
        this.setTypeface(tf);
    }
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }


}
