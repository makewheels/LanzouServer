package com.eg.lanzouserver.prepare;

import com.eg.lanzouserver.bean.lanzou.LanzouFile;
import com.eg.lanzouserver.bean.lanzou.uploadresponse.SimpleUploadResponse;
import com.eg.lanzouserver.repository.LanzouFileRepository;
import com.eg.lanzouserver.util.Constants;
import com.eg.lanzouserver.util.LanzouUtil;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Date;

/**
 * @time 2020-02-03 10:17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUpload {
    @Autowired
    LanzouFileRepository lanzouFileRepository;

    @Test
    public void go() {
        File file = new File("C:\\Users\\Administrator\\Downloads\\out.m3u8");

        LanzouFile lanzouFile = new LanzouFile();
        lanzouFile.setCreateTime(new Date());
        lanzouFile.setRealName(file.getName());
        lanzouFile.setRealExtension(FilenameUtils.getExtension(file.getName()));
        lanzouFile.setFakeName(file.getName() + ".zip");
        lanzouFile.setFakeExtension("zip");
        lanzouFile.setSize(file.length());
        lanzouFile.setFolderId(Constants.UPLOAD_FOLDER_ID);

        LanzouUtil lanzouUtil = new LanzouUtil();
        SimpleUploadResponse simpleUploadResponse = lanzouUtil.simpleUploadFile(file);
        lanzouFile.setFileId(simpleUploadResponse.getFileId());
        lanzouFile.setShareId(simpleUploadResponse.getShareId());

        lanzouFileRepository.save(lanzouFile);
    }

}
