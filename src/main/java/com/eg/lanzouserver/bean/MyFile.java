package com.eg.lanzouserver.bean;

import lombok.Data;

import java.util.Date;

/**
 * @time 2020-02-01 20:32
 */
@Data
public class MyFile {

    private String _id;
    private String tsId;//m3u8视频文件碎片的索引
    private String fileId;
    private String folderId;//蓝奏云folder_id
    private String filename;
    private String shareId;//蓝奏云分享id
    private Date createTime;
}
