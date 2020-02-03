package com.eg.lanzouserver.bean.lanzou;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 我的蓝奏云文件，用于我保存到数据库
 *
 * @time 2020-02-03 12:55
 */
@Data
@Document
public class LanzouFile {
    @Id
    private String _id;
    private String shareId;     //i906jhg
    private String fileId;      //16852626
    private String folderId;    //1323157
    private String realName;    //1.ts
    private String fakeName;    //1.ts.zip
    private String realExtension;//ts
    private String fakeExtension;//zip

    private long size;
    private Date createTime;
}
