package com.eg.lanzouserver.util;

import com.alibaba.fastjson.JSON;
import com.eg.lanzouserver.bean.folderinfo.FolderInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @time 2020-02-01 20:04
 */
public class LanzouUtil {
    private static FolderInfo getSingleFolderInfo(String folder_id, int page) {
        Map<String, String> header = new HashMap<>();
        header.put(Constants.COOKIE_PHPDISK_INFO_KEY, Constants.COOKIE_PHPDISK_INFO_VALUE);
        Map<String, String> params = new HashMap<>();
        params.put("task", Constants.TASK_GET_FOLDER_INFO);
        params.put("folder_id", folder_id);
        params.put("pg", page + "");
        String json = HttpUtil.post(Constants.BASE_URL, header, params);
        return JSON.parseObject(json, FolderInfo.class);
    }

    /**
     * 保存一个文件夹的所有文件的信息，主要是要保存文件的id，和分享id
     *
     * @param folder_id
     */
    public static void doSaveFolder(String folder_id) {

    }
}
