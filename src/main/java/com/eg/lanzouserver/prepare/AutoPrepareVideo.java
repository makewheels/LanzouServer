package com.eg.lanzouserver.prepare;

import com.eg.lanzouserver.bean.Video;
import com.eg.lanzouserver.bean.lanzou.LanzouFile;
import com.eg.lanzouserver.repository.VideoRepository;
import com.eg.lanzouserver.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全自动准备视频
 *
 * @time 2020-02-03 12:47
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AutoPrepareVideo {
    @Autowired
    VideoRepository videoRepository;

    /**
     * 准备单个视频文件
     *
     * @param videoFile
     * @throws IOException
     */
    private Video prepareSingleVideo(File videoFile) {
        Video video = new Video();
        video.setCreateTime(new Date());
        //标题
        String title = FilenameUtils.getBaseName(videoFile.getName());
        video.setTitle(title);
        //生成视频id
        String videoId = UuidUtil.getUuid();
        video.setVideoId(videoId);
        //转码
        MakeM3u8Result makeM3u8Result = null;
        try {
            makeM3u8Result = VideoUtil.makeM3u8(videoFile, videoId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("转码完成，开始上传，ts碎片总共 " + makeM3u8Result.getTsFileList().size() + " 个");
        //上传ts碎片到蓝奏云
        LanzouUtil lanzouUtil = new LanzouUtil();
        List<MakeM3u8Result.Ts> tsList = makeM3u8Result.getTsFileList();
        //用文件大小显示进度，先统计所有碎片总大小
        long totalSize = 0;
        long uploadSize = 0;
        for (MakeM3u8Result.Ts ts : tsList) {
            totalSize += ts.getFile().length();
        }
        //执行上传
        for (int i = 0; i < tsList.size(); i++) {
            MakeM3u8Result.Ts ts = tsList.get(i);
            LanzouFile lanzouFile = lanzouUtil.simpleUploadAndSave(ts.getFile());
            ts.setLanzouFile(lanzouFile);
            //显示进度
            uploadSize += ts.getFile().length();
            double progress = uploadSize * 1.0 / totalSize * 100;
            String format = String.format("%.2f", progress);
            System.err.println("progress: " + title + " "
                    + format + "% (" + (i + 1) + "/" + tsList.size() + ")");
        }
        //修改m3u8文件
        File m3u8File = makeM3u8Result.getM3u8File();
        List<String> lines = null;
        try {
            lines = FileUtils.readLines(m3u8File, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("#")) {
                continue;
            }
            //line就是filename
            //根据文件名在tsList中找到shareId
            String shareId = "";
            for (MakeM3u8Result.Ts ts : tsList) {
                if (line.equals(ts.getFile().getName())) {
                    shareId = ts.getLanzouFile().getShareId();
                }
            }
            //是绝对的http url，还是相对路径
//            String newLine = Constants.BASE_URL + "/lanzou/getFileByShareId?shareId=" + shareId;
            String newLine = "getFileByShareId?shareId=" + shareId;
            lines.set(i, newLine);
        }
        //重写m3u8文件
        try {
            FileUtils.writeLines(m3u8File, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //上传m3u8文件到蓝奏云
        LanzouFile m3u8LanzouFile = lanzouUtil.simpleUploadAndSave(m3u8File);
        String m3u8Url = Constants.BASE_URL + "/lanzou/getFileByShareId?shareId=" + m3u8LanzouFile.getShareId();
        System.out.println("m3u8Url = " + m3u8Url + "&a=b.m3u8");
        //制作html
        Map<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("m3u8Url", m3u8Url);
        File htmlFile = FreemakerUtil.createHtmlByMode("video.html.ftl", "video.html", params);
        //上传html文件到腾讯云对象存储
        QcloudCosUtil.saveToQcloud(htmlFile, "video/" + title + "-" + videoId + "/video.html");
        String htmlUrl = null;
        try {
            htmlUrl = "http://bucket-1253319037.cos.ap-beijing.myqcloud.com/video/"
                    + URLEncoder.encode(title, "utf-8") + "-" + videoId + "/video.html";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //删除html文件
        htmlFile.delete();
        System.out.println(htmlUrl);
        //删除folder所有转码文件
        m3u8File.delete();
        for (MakeM3u8Result.Ts ts : tsList) {
            ts.getFile().delete();
        }
        makeM3u8Result.getFolder().delete();
        //设置video参数
        video.setM3u8Url(m3u8Url);
        video.setHtmlUrl(htmlUrl);
        //保存video
        videoRepository.save(video);
        return video;
    }

    @Test
    public void run() {
        //上传一个文件夹
        File folder = new File("D:\\BaiduNetdiskDownload\\007系列全集.外挂国语.中英字幕");
        File[] files = folder.listFiles();
        for (File videoFile : files) {
            Video video = prepareSingleVideo(videoFile);
            System.out.println(video);
        }
    }
}
