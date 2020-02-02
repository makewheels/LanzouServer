package com.eg.lanzouserver.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @time 2020-02-01 20:32
 */
@Data
@Document
public class MyFile {
    @Id
    private String _id;
    private String videoId;
    private String tsId;//m3u8视频文件碎片的索引
    private String fileId;
    private String folderId;//蓝奏云folder_id
    private String shareId;//蓝奏云分享id
    private String filename;
    private Date createTime;
}
