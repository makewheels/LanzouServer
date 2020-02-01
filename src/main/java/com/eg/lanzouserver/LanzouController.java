package com.eg.lanzouserver;

import com.eg.lanzouserver.util.LanzouUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @time 2020-02-01 19:47
 */
@Controller
public class LanzouController {

    @RequestMapping("saveFolder")
    public String saveFolder(@RequestParam String folder_id) {
        LanzouUtil.doSaveFolder(folder_id);
        return null;
    }


}
