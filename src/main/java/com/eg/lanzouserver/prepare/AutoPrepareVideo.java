package com.eg.lanzouserver.prepare;

import com.eg.lanzouserver.bean.lanzou.LanzouFile;
import com.eg.lanzouserver.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
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

    @Test
    public void run() throws IOException {
        //视频文件
        File videoFile = new File("D:\\zBILIBILI\\1_我的影片17.flv");
        //标题
        String title = FilenameUtils.getBaseName(videoFile.getName());
        //生成视频id
        String videoId = UuidUtil.getUuid();
        //转码
        MakeM3u8Result makeM3u8Result = VideoUtil.makeM3u8(videoFile, videoId);
        //上传ts碎片
        LanzouUtil lanzouUtil = new LanzouUtil();
        List<MakeM3u8Result.Ts> tsList = makeM3u8Result.getTsFileList();
        for (MakeM3u8Result.Ts ts : tsList) {
            LanzouFile lanzouFile = lanzouUtil.simpleUploadAndSave(ts.getFile());
            ts.setLanzouFile(lanzouFile);
        }
        //修改m3u8文件
        File m3u8File = makeM3u8Result.getM3u8File();
        List<String> lines = FileUtils.readLines(m3u8File, "utf-8");
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
        FileUtils.writeLines(m3u8File, lines);
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
        String htmlUrl = "http://bucket-1253319037.cos.ap-beijing.myqcloud.com/video/"
                + URLEncoder.encode(title, "utf-8") + "-" + videoId + "/video.html";
        //删除html文件
        htmlFile.delete();
        System.out.println(htmlUrl);

        //删除folder所有转码文件
        m3u8File.delete();
        for (MakeM3u8Result.Ts ts : tsList) {
            ts.getFile().delete();
        }
        makeM3u8Result.getFolder().delete();
    }
}
