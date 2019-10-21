package com.example.opencvtest.data;

public class Slide {
    private int Image;
    private String Title;
    private int Tab;

    public int getImage() {
        return Image;
    }

    public String getTitle() {
        return Title;
    }

    public int getTab() {
        return Tab;
    }

    public Slide(int image, String title, int tab) {
        Image = image;
        Title = title;
        Tab = tab;


    }
}
