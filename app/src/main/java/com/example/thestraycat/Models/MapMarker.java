package com.example.thestraycat.Models;

public class MapMarker {

    private String title;
    private String v;
    private String v1;
    private String information;
    private String condition;

    public MapMarker(){}
    public MapMarker(String title, String v, String v1, String information, String condition) {
        this.title = title;
        this.v = v;
        this.v1 = v1;
        this.information = information;
        this.condition = condition;
    }

    public String getCondition() { return condition; }

    public void setCondition(String condition) { this.condition = condition; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getV() {
        return v;
    }

    public String getInformation() { return information; }

    public void setInformation(String information) { this.information = information; }

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
