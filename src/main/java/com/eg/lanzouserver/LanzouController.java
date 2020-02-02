package com.eg.lanzouserver;

import com.eg.lanzouserver.bean.MyFile;
import com.eg.lanzouserver.repository.MyFileRepository;
import com.eg.lanzouserver.repository.VideoRepository;
import com.eg.lanzouserver.util.LanzouUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @time 2020-02-01 19:47
 */
@Controller
@RequestMapping("lanzou")
public class LanzouController {
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    MyFileRepository myFileRepository;

    /**
     * 根据文件夹id获取文件shareId，并存数据库
     *
     * @param folder_id
     * @return
     */
    @RequestMapping("saveFolder")
    @ResponseBody
    public String saveFolder(@RequestParam String folder_id) {
        new LanzouUtil().handleSaveFolder(folder_id, videoRepository, myFileRepository);
        return "<h1>hello</h1>";
    }

    /**
     * 根据tsId获取文件直链，因为这里已经是在浏览器播放了，需要返回重定向
     *
     * @param tsId
     * @return
     */
    @RequestMapping("getFile")
    @CrossOrigin
    public String getFile(@RequestParam String tsId, HttpServletResponse response) {
        tsId = tsId.replace(".js", "");
        MyFile myFile = myFileRepository.findMyFileByTsIdEquals(tsId);
        String shareId = myFile.getShareId();
        System.out.println("getFile tsId = " + myFile.getTsId());
        return "redirect:" + new LanzouUtil().getDirectUrl(shareId);
    }

}
