package com.eg.lanzouserver.prepare;

import com.eg.lanzouserver.bean.lanzou.LanzouFile;
import lombok.Data;

import java.io.File;
import java.util.List;

/**
 * @time 2020-02-03 19:26
 */
@Data
public class MakeM3u8Result {
    private String id;
    private File folder;
    private File m3u8File;
    private List<Ts> tsFileList;

    @Data
    public static class Ts {
        private File file;//ts文件
        private LanzouFile lanzouFile;//蓝奏云上传后的结果
    }

}
