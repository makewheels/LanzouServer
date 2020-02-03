package com.eg.lanzouserver.prepare;

import com.eg.lanzouserver.repository.LanzouFileRepository;
import com.eg.lanzouserver.util.LanzouUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

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
        LanzouUtil lanzouUtil = new LanzouUtil();
        lanzouUtil.simpleUploadAndSave(file);
    }

}
