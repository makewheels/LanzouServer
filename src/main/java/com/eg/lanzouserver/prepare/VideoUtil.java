package com.eg.lanzouserver.prepare;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @time 2020-02-03 18:59
 */
public class VideoUtil {

    /**
     * 制作m3u8文件
     *
     * @param videoFile
     * @param videoId
     * @return
     * @throws IOException
     */
    public static MakeM3u8Result makeM3u8(File videoFile, String videoId) throws IOException {
        //创建目录
        File folder = new File(videoFile.getParent(), videoId);
        if (folder.exists() == false) {
            folder.mkdirs();
        }
        File m3u8File = new File(folder.getAbsolutePath() + File.separator + videoId + ".m3u8");
        //转码视频
        String cmd = "ffmpeg -i " + videoFile.getAbsolutePath()
                + " -codec copy -vbsf h264_mp4toannexb -map 0 -f segment -segment_list "
                + m3u8File.getAbsolutePath() + " -segment_time 10 " + folder.getAbsolutePath()
                + File.separator + videoId + "-%d.ts";
        Process process = Runtime.getRuntime().exec(cmd);
        InputStreamReader inputStreamReader = new InputStreamReader(process.getErrorStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        //设置返回的结果
        MakeM3u8Result makeM3u8Result = new MakeM3u8Result();
        makeM3u8Result.setId(videoId);
        makeM3u8Result.setFolder(folder);
        makeM3u8Result.setM3u8File(m3u8File);
        List<MakeM3u8Result.Ts> tsList = new ArrayList<>();
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".ts")) {
                MakeM3u8Result.Ts ts = new MakeM3u8Result.Ts();
                ts.setFile(file);
                tsList.add(ts);
            }
        }
        makeM3u8Result.setTsFileList(tsList);
        return makeM3u8Result;
    }

}
