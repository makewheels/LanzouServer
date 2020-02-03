package com.eg.lanzouserver.bean.lanzou.uploadresponse;

import lombok.Data;

/**
 * 简单的，蓝奏云上传文件，返回
 *
 * @time 2020-02-03 12:34
 */
@Data
public class SimpleUploadResponse {
    private String fileId;
    private String shareId;
}
