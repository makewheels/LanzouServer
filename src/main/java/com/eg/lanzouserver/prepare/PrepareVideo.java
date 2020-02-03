package com.eg.lanzouserver.prepare;

import com.eg.lanzouserver.util.Constants;
import com.eg.lanzouserver.util.FreemakerUtil;
import com.eg.lanzouserver.util.QcloudCosUtil;
import com.eg.lanzouserver.util.UuidUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 准备视频
 *
 * @time 2020-02-02 18:41
 */
public class PrepareVideo {
    public static void main(String[] args) throws IOException {
        String title = "我们的测试";
        //视频id
        String videoId = UuidUtil.getUuid();
        //把所有ts文件改名，后面加.zip
        //还会把生成的videoId也改进去
        File folder = new File(Constants.VIDEO_FOLDER);
        File[] files = folder.listFiles();
        for (File file : files) {
            String extension = FilenameUtils.getExtension(file.getName());
            if (extension.equals("ts") == false) {
                continue;
            }
            String index = FilenameUtils.getBaseName(file.getName()).split("-")[1];
            File newFile = new File(file.getParent(), videoId + "-" + index + ".ts.zip");
            file.renameTo(newFile);
        }
        //修改m3u8文件
        File m3u8File = new File(Constants.VIDEO_FOLDER, "out.m3u8");
        List<String> lines = FileUtils.readLines(m3u8File, "utf-8");
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("#")) {
                continue;
            }
            String index = FilenameUtils.getBaseName(line).split("-")[1];
            String newLine = Constants.BASE_URL + "/lanzou/getFile?tsId=" + videoId + "-" + index;
            lines.set(i, newLine);
        }
        FileUtils.writeLines(m3u8File, lines);
        //把m3u8文件上传到腾讯云对象存储
        String m3u8Url = QcloudCosUtil.saveToQcloud(m3u8File, "video/" + title + "-" + videoId + "/out.m3u8");
        //使用freemarker生成html文件
        Map<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("m3u8Url", m3u8Url);
        File htmlFile = FreemakerUtil.createHtmlByMode("video.html.ftl", "video.html", params);
        //上传html文件到腾讯云对象存储
        QcloudCosUtil.saveToQcloud(htmlFile, "video/" + title + "-" + videoId + "/video.html");
        String htmlUrl = "http://bucket-1253319037.cos.ap-beijing.myqcloud.com/video/"
                + URLEncoder.encode(title, "utf-8") + "-" + videoId + "/video.html";
        System.out.println(htmlUrl);
    }
}
