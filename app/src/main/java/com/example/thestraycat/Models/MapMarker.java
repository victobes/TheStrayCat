package com.example.thestraycat.Models;

public class MapMarker {

    private String title;
    private String v;
    private String v1;

    public MapMarker(){}
    public MapMarker(String title, String v, String v1) {
        this.title = title;
        this.v = v;
        this.v1 = v1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getV1() {
        return v1;
    }

    public void setV1(String v1) {
        this.v1 = v1;
    }
}
