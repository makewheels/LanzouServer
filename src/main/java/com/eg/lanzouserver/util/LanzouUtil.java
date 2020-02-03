package com.eg.lanzouserver.util;

import com.alibaba.fastjson.JSON;
import com.eg.lanzouserver.bean.MyFile;
import com.eg.lanzouserver.bean.Video;
import com.eg.lanzouserver.bean.lanzou.DirectUrl;
import com.eg.lanzouserver.bean.lanzou.fileshareid.FileShareId;
import com.eg.lanzouserver.bean.lanzou.folderinfo.FolderInfo;
import com.eg.lanzouserver.bean.lanzou.folderinfo.Text;
import com.eg.lanzouserver.bean.lanzou.uploadresponse.SimpleUploadResponse;
import com.eg.lanzouserver.bean.lanzou.uploadresponse.UploadResponse;
import com.eg.lanzouserver.repository.MyFileRepository;
import com.eg.lanzouserver.repository.VideoRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @time 2020-02-01 20:04
 */
public class LanzouUtil {
    private MyFileRepository myFileRepository;
    private VideoRepository videoRepository;

    private Map<String, String> getLanzouHeader() {
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
    private FolderInfo getSingleFolderInfo(String folder_id, int page) {
        String params = "task=" + Constants.TASK_GET_FOLDER_INFO + "&folder_id=" + folder_id + "&pg=" + page;
        String json = HttpUtil.post(Constants.WOOZOOO_URL, getLanzouHeader(), params);
        return JSON.parseObject(json, FolderInfo.class);
    }

    /**
     * 保存单个文件夹信息到数据库
     *
     * @param folder_id
     * @param singleFolderInfo
     * @param videoId
     */
    private void saveSingleFolder(String folder_id, FolderInfo singleFolderInfo, String videoId) {
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
            myFile.setVideoId(videoId);
            //保存myFile到数据库
            MyFile myFileByTsIdEquals = myFileRepository.findMyFileByTsIdEquals(tsId);
            //先判断数据库中是不是已经存了
            if (myFileByTsIdEquals != null) {
                return;
            }
            myFileRepository.save(myFile);
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
     * @param videoRepository
     * @param myFileRepository
     */
    public void handleSaveFolder(String folder_id, VideoRepository videoRepository, MyFileRepository myFileRepository) {
        this.myFileRepository = myFileRepository;
        this.videoRepository = videoRepository;
        //因为一个文件夹内的所有文件，就是一个video的碎片
        Video video = new Video();
        video.setCreateTime(new Date());
        int page = 1;
        //先获取一次
        FolderInfo singleFolderInfo = getSingleFolderInfo(folder_id, page);
        //拿到videoId，就是tsId的中划线的前部分
        String videoId = null;
        List<Text> fileInfoList = singleFolderInfo.getText();
        if (CollectionUtils.isNotEmpty(fileInfoList)) {
            String filename = fileInfoList.get(0).getName();
            videoId = filename.split("-")[0];
            //保存myFile
            saveSingleFolder(folder_id, singleFolderInfo, videoId);
            //保存video
            video.setVideoId(videoId);
            videoRepository.save(video);
        }
        //把剩下所有的文件都保存的数据库
        while (CollectionUtils.isNotEmpty(singleFolderInfo.getText())) {
            page++;
            singleFolderInfo = getSingleFolderInfo(folder_id, page);
            saveSingleFolder(folder_id, singleFolderInfo, videoId);
        }
    }

    /**
     * 通过文件shareId获取直链
     *
     * @param shareId
     * @return
     */
    public String getDirectUrl(String shareId) {
        Document document = Jsoup.parse(HttpUtil.get(Constants.LANZOU_URL + "/" + shareId));
        Element iframe = document.getElementsByTag("iframe").get(0);
        String src = iframe.attr("src");
        String html = HttpUtil.get(Constants.LANZOU_URL + src);
        String sign = StringUtils.substringBetween(html, "var sg = '", "';");
        Map<String, String> header = new HashMap<>();
        header.put("referer", src);
        String params = "action=downprocess&sign=" + sign + "&ves=1";
        String json = HttpUtil.post(Constants.LANZOU_URL + "/ajaxm.php", header, params);
        DirectUrl directUrl = JSON.parseObject(json, DirectUrl.class);
        return directUrl.getDom() + "/file/" + directUrl.getUrl();
    }

    /**
     * 上传文件到蓝奏云
     *
     * @param file
     * @param folderId
     * @return
     * @throws IOException
     */
    public UploadResponse uploadFile(File file, String folderId) throws IOException {
        //先给file改名字，后面加.zip
        File originalFile = new File(file.getAbsolutePath());
        File newFile = new File(file.getAbsolutePath() + ".zip");
        file.renameTo(newFile);
        file = newFile;
        Map<String, String> header = new HashMap();
        header.put("accept", "*/*");
        header.put("Cookie", Constants.COOKIE_PHPDISK_INFO_KEY + "=" + Constants.COOKIE_PHPDISK_INFO_VALUE);
        header.put("origin", "https://pc.woozooo.com");
        header.put("Referer", "https://pc.woozooo.com/mydisk.php?item=files&action=index");
        header.put("sec-fetch-mode", "cors");
        header.put("sec-fetch-site", "same-origin");

        Map<String, String> params = new HashMap();
        params.put("task", Constants.TASK_UPLOAD_FILE);
        params.put("folder_id", folderId);
        params.put("type", "application/zip");
        params.put("id", "WU_FILE_0");
        params.put("name", file.getName());
        params.put("size", file.length() + "");

        //上传
        String json = HttpUtil.uploadFile(Constants.UPLOAD_URL, header, params,
                "upload_file", file);

        //重命名回去
        file.renameTo(originalFile);

        //返回值
        UploadResponse uploadResponse = JSON.parseObject(json, UploadResponse.class);
        System.out.println("lanzou upload " + uploadResponse.getText().get(0).getName()
                + JSON.toJSONString(uploadResponse));
        return uploadResponse;
    }

    /**
     * 蓝奏云上传文件到upload文件夹
     *
     * @param file
     * @return
     */
    public SimpleUploadResponse simpleUploadFile(File file) {
        SimpleUploadResponse simpleUploadResponse = new SimpleUploadResponse();
        UploadResponse uploadResponse = null;
        try {
            uploadResponse = uploadFile(file, Constants.UPLOAD_FOLDER_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (uploadResponse == null) {
            return null;
        }
        com.eg.lanzouserver.bean.lanzou.uploadresponse.Text text = uploadResponse.getText().get(0);
        String fileId = text.getId();
        String shareId = text.getF_id();
        simpleUploadResponse.setFileId(fileId);
        simpleUploadResponse.setShareId(shareId);
        return simpleUploadResponse;
    }
}
