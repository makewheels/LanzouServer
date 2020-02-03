package com.eg.lanzouserver;

import com.eg.lanzouserver.bean.MyFile;
import com.eg.lanzouserver.repository.MyFileRepository;
import com.eg.lanzouserver.repository.VideoRepository;
import com.eg.lanzouserver.util.Constants;
import com.eg.lanzouserver.util.LanzouUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.ref.PhantomReference;

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


    private WebDriver driver;

    /**
     * 根据tsId获取文件直链，因为这里已经是在浏览器播放了，需要返回重定向
     *
     * @param tsId
     * @return
     */
    @RequestMapping("getFileByTsId")
    @CrossOrigin
    public String getFileByTsId(@RequestParam String tsId) {
        tsId = tsId.replace(".js", "");
        MyFile myFile = myFileRepository.findMyFileByTsIdEquals(tsId);
        String shareId = myFile.getShareId();

        if (driver == null) {
//            ChromeOptions chromeOptions = new ChromeOptions();
//            chromeOptions.setHeadless(true);
//            driver = new ChromeDriver(chromeOptions);
            driver = new PhantomJSDriver();
        }
        driver.get(Constants.LANZOU_URL + "/" + shareId);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebElement a = driver.switchTo().
                frame(driver.findElement(By.xpath("/html/body/div[3]/div[2]/div[4]/iframe")))
                .findElement(By.xpath("//*[@id=\"go\"]/a"));
        String href = a.getAttribute("href");
        System.out.println(href);

        System.out.println("getFile tsId = " + myFile.getTsId());

//        return "redirect:" + new LanzouUtil().getDirectUrl(shareId);
        return "redirect:" + href;
    }

    /**
     * @param shareId
     * @return
     */
    @RequestMapping("getFileByShareId")
    @CrossOrigin
    public String getFileByShareId(@RequestParam String shareId) {
        if (driver == null) {
            driver = new PhantomJSDriver();
        }
        driver.get(Constants.LANZOU_URL + "/" + shareId);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebElement a = driver.switchTo().
                frame(driver.findElement(By.xpath("/html/body/div[3]/div[2]/div[4]/iframe")))
                .findElement(By.xpath("//*[@id=\"go\"]/a"));
        String href = a.getAttribute("href");
        System.out.println(href);
        return "redirect:" + href;
    }

}
