package com.eg.lanzouserver.util;

import com.alibaba.fastjson.JSON;
import com.eg.lanzouserver.bean.MyFile;
import com.eg.lanzouserver.bean.lanzou.DirectUrl;
import com.eg.lanzouserver.bean.lanzou.fileshareid.FileShareId;
import com.eg.lanzouserver.bean.lanzou.folderinfo.FolderInfo;
import com.eg.lanzouserver.bean.lanzou.folderinfo.Text;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @time 2020-02-01 20:04
 */
public class LanzouUtil {

    private static Map<String, String> getLanzouHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("Cookie", Constants.COOKIE_PHPDISK_INFO_KEY + "=" + Constants.COOKIE_PHPDISK_INFO_VALUE + ";");
        return header;
    }

    /**
     * 发请求获取文件夹信息
     *
     * @param folder_id
     * @param page
     * @return
     */
    private static FolderInfo getSingleFolderInfo(String folder_id, int page) {
        String params = "task=" + Constants.TASK_GET_FOLDER_INFO + "&folder_id=" + folder_id + "&pg=" + page;
        String json = HttpUtil.post(Constants.WOOZOOO_URL, getLanzouHeader(), params);
        return JSON.parseObject(json, FolderInfo.class);
    }

    /**
     * 保存单个文件夹信息到数据库
     *
     * @param folder_id
     * @param singleFolderInfo
     */
    private static void saveSingleFolder(String folder_id, FolderInfo singleFolderInfo) {
        List<Text> text = singleFolderInfo.getText();
        for (Text singleFileInfo : text) {
            MyFile myFile = new MyFile();
            String fileId = singleFileInfo.getId();
            myFile.setFileId(fileId);
            myFile.setFolderId(folder_id);
            String filename = singleFileInfo.getName();
            myFile.setFilename(filename);
            myFile.setCreateTime(new Date());
            //解析出tsId
            String tsId = filename.substring(0, filename.lastIndexOf(".ts.zip"));
            myFile.setTsId(tsId);
            //现在还需要拿到shareId
            String params = "task=" + Constants.TASK_GET_FILE_SHARE_ID + "&file_id=" + fileId;
            String json = HttpUtil.post(Constants.WOOZOOO_URL, getLanzouHeader(), params);
            FileShareId fileShareId = JSON.parseObject(json, FileShareId.class);
            myFile.setShareId(fileShareId.getInfo().getF_id());

            //这是pwd，没啥用
//            String pwd = fileShareId.getInfo().getPwd();

            String s = JSON.toJSONString(myFile);
            System.out.println(s);
        }
    }

    /**
     * 保存一个文件夹的所有文件的信息，主要是要保存文件的id，和分享id
     *
     * @param folder_id
     */
    public static void handleSaveFolder(String folder_id) {
        int page = 1;
        FolderInfo singleFolderInfo;
        do {
            singleFolderInfo = getSingleFolderInfo(folder_id, page);
            saveSingleFolder(folder_id, singleFolderInfo);
            page++;
        } while (CollectionUtils.isNotEmpty(singleFolderInfo.getText()));
    }

    /**
     * 通过文件shareId获取直链
     *
     * @param shareId
     * @return
     */
    public static String getDirectUrl(String shareId) {
        Document document = Jsoup.parse(HttpUtil.get(Constants.LANZOU_URL + "/" + shareId));
        Element iframe = document.getElementsByTag("iframe").get(0);
        String src = iframe.attr("src");
        String html = HttpUtil.get(Constants.LANZOU_URL + src);
        String sign = StringUtils.substringBetween(html, "var sg = '", "';");
        Map<String, String> header = new HashMap<>();
        header.put("referer", src);
        String json = HttpUtil.post(Constants.LANZOU_URL + "/ajaxm.php", header, "action=downprocess&sign=" + sign + "&ves=1");
        DirectUrl directUrl = JSON.parseObject(json, DirectUrl.class);
        return directUrl.getDom() + "/file/" + directUrl.getUrl();
    }

    public static void main(String[] args) {
        String s = getDirectUrl("i8z87ud");
        System.out.println(s);
    }

}
