package com.example.viewpagertest.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/10.
 */

public class Message implements Serializable {
    private String tvString;
    private String btnString;
    private int ivresId;
    private String weatherId;

    public Message(String tvString, String btnString, int ivresId, String weatherId) {
        this.tvString = tvString;
        this.btnString = btnString;
        this.ivresId = ivresId;
        this.weatherId = weatherId;
    }

    public String getTvString() {
        return tvString;
    }

    public void setTvString(String tvString) {
        this.tvString = tvString;
    }

    public String getBtnString() {
        return btnString;
    }

    public void setBtnString(String btnString) {
        this.btnString = btnString;
    }

    public int getIvresId() {
        return ivresId;
    }

    public void setIvresId(int ivresId) {
        this.ivresId = ivresId;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
