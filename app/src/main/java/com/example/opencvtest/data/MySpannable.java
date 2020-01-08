package com.example.opencvtest.data;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class MySpannable extends ClickableSpan {

    private boolean isUnderline;


    /**
     * Constructor
     */
    protected MySpannable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {

        ds.setUnderlineText(isUnderline);
        ds.setFakeBoldText(true);
        ds.setColor(Color.parseColor("#AF364A"));
    }

    @Override
    public void onClick(@NonNull View widget) {


    }
}