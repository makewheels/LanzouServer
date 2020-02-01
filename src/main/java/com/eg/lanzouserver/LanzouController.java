package com.eg.lanzouserver;

import com.eg.lanzouserver.util.LanzouUtil;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @time 2020-02-01 19:47
 */
@Controller
@RequestMapping("lanzou")
public class LanzouController {
    /**
     * 根据文件夹id获取文件shareId，并存数据库
     *
     * @param folder_id
     * @return
     */
    @RequestMapping("saveFolder")
    @ResponseBody
    public String saveFolder(@RequestParam String folder_id) {
        LanzouUtil.handleSaveFolder(folder_id);
        return "<h1>hello</h1>";
    }

    /**
     * 根据tsId获取文件直链，因为这里已经是在浏览器播放了，需要返回重定向
     *
     * @param tsId
     * @return
     */
    @RequestMapping("getFile")
    public String getFile(@RequestParam @Nullable String tsId) {
        return "redirect:http://www.baidu.com";
    }

}
