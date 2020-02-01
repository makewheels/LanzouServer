/**
  * Copyright 2020 bejson.com 
  */
package com.eg.lanzouserver.bean.folderinfo;

import java.util.List;

/**
 * Auto-generated: 2020-02-01 20:7:2
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class FolderInfo {

    private int zt;
    private int info;
    private List<Text> text;
    public void setZt(int zt) {
         this.zt = zt;
     }
     public int getZt() {
         return zt;
     }

    public void setInfo(int info) {
         this.info = info;
     }
     public int getInfo() {
         return info;
     }

    public void setText(List<Text> text) {
         this.text = text;
     }
     public List<Text> getText() {
         return text;
     }

}