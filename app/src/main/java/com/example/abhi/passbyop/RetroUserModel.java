package com.example.abhi.passbyop;

import com.google.gson.annotations.SerializedName;

public class RetroUserModel {

    @SerializedName("username")
    private String username;

    @SerializedName("x0")
    private Integer x0;
    @SerializedName("y0")
    private Integer y0;
    @SerializedName("x1")
    private Integer x1;
    @SerializedName("y1")
    private Integer y1;
    @SerializedName("x2")
    private Integer x2;
    @SerializedName("y2")
    private Integer y2;
    @SerializedName("x3")
    private Integer x3;
    @SerializedName("y3")
    private Integer y3;

    @SerializedName("image_url")
    private String image_url;


    public RetroUserModel(String username, Integer x0, Integer y0, Integer x1, Integer y1, Integer x2, Integer y2, Integer x3, Integer y3, String image_url) {
        this.username = username;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.image_url = image_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getX0() {
        return x0;
    }

    public void setX0(Integer x0) {
        this.x0 = x0;
    }

    public Integer getY0() {
        return y0;
    }

    public void setY0(Integer y0) {
        this.y0 = y0;
    }

    public Integer getX1() {
        return x1;
    }

    public void setX1(Integer x1) {
        this.x1 = x1;
    }

    public Integer getY1() {
        return y1;
    }

    public void setY1(Integer y1) {
        this.y1 = y1;
    }

    public Integer getX2() {
        return x2;
    }

    public void setX2(Integer x2) {
        this.x2 = x2;
    }

    public Integer getY2() {
        return y2;
    }

    public void setY2(Integer y2) {
        this.y2 = y2;
    }

    public Integer getX3() {
        return x3;
    }

    public void setX3(Integer x3) {
        this.x3 = x3;
    }

    public Integer getY3() {
        return y3;
    }

    public void setY3(Integer y3) {
        this.y3 = y3;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
