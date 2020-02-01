package com.eg.lanzouserver;

import com.eg.lanzouserver.util.LanzouUtil;
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

    @RequestMapping("saveFolder")
    @ResponseBody
    public String saveFolder(@RequestParam String folder_id) {
        LanzouUtil.handleSaveFolder(folder_id);
        return "<h1>hello</h1>";
    }


}
