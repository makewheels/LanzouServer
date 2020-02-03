package com.eg.lanzouserver.prepare;

import com.eg.lanzouserver.bean.lanzou.uploadresponse.Text;
import com.eg.lanzouserver.bean.lanzou.uploadresponse.UploadResponse;
import com.eg.lanzouserver.util.LanzouUtil;

import java.io.File;

/**
 * @time 2020-02-03 10:17
 */
public class TestUpload {
    public static void main(String[] args) {
        File file = new File("C:\\Users\\Administrator\\Downloads\\lanzou-gui-master.zip");
        LanzouUtil lanzouUtil = new LanzouUtil();
        UploadResponse uploadResponse = lanzouUtil.simpleUploadFile(file);
        Text text = uploadResponse.getText().get(0);
        String fileId = text.getId();
        String shareId = text.getF_id();

        System.out.println(fileId);
        System.out.println(shareId);

    }
}
