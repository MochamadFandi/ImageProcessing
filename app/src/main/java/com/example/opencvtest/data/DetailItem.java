package com.example.opencvtest.data;

import android.graphics.Bitmap;

public class DetailItem {

    private String text;
    private Bitmap photo;

    public DetailItem(String text, Bitmap photo) {
        this.text = text;
        this.photo = photo;
    }

    public String getText() {
        return text;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setText(String text) {
        this.text = text;
    }


}
