package com.eg.lanzouserver.util;

import com.alibaba.fastjson.JSON;
import com.eg.lanzouserver.bean.lanzou.DirectUrl;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * @time 2020-02-01 23:09
 */
public class TEs {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://www.lanzous.com/i6aa3hg");
        HttpResponse response = httpClient.execute(httpGet);
        String result = EntityUtils.toString(response.getEntity());
        System.out.println(result);
        Document document = Jsoup.parse(result);
        Element iframe = document.getElementsByTag("iframe").get(0);
        String src = iframe.attr("src");
        HttpGet httpGet1 = new HttpGet(Constants.LANZOU_URL + src);
        CloseableHttpResponse response1 = httpClient.execute(httpGet1);
        String result1 = EntityUtils.toString(response1.getEntity());
        String sign = StringUtils.substringBetween(result1, "var sg = '", "';");
        HttpPost httpPost = new HttpPost(Constants.LANZOU_URL + "/ajaxm.php");
        httpPost.setEntity(new StringEntity("action=downprocess&sign=" + sign + "&ves=1"));
        CloseableHttpResponse response2 = httpClient.execute(httpPost);
        String result2 = EntityUtils.toString(response2.getEntity());
        DirectUrl directUrl = JSON.parseObject(result2, DirectUrl.class);
        String ssfa = directUrl.getDom() + "/file/" + directUrl.getUrl();
        System.out.println(ssfa);

    }
}
