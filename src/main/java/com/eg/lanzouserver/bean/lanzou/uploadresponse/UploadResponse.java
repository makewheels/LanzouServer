/**
 * Copyright 2020 bejson.com
 */
package com.eg.lanzouserver.bean.lanzou.uploadresponse;

import java.util.List;

/**
 * Auto-generated: 2020-02-03 12:11:20
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class UploadResponse {

    private int zt;
    private String info;
    private List<Text> text;

    public void setZt(int zt) {
        this.zt = zt;
    }

    public int getZt() {
        return zt;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setText(List<Text> text) {
        this.text = text;
    }

    public List<Text> getText() {
        return text;
    }

}