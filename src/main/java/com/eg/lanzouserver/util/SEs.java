package com.eg.lanzouserver.util;

import com.alibaba.fastjson.JSON;
import com.eg.lanzouserver.bean.lanzou.DirectUrl;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @time 2020-02-01 23:04
 */
public class SEs {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpEntity entity = httpClient.execute(null).getEntity();
        Document document = Jsoup.parse(EntityUtils.toString(entity));
        Element iframe = document.getElementsByTag("iframe").get(0);
        String src = iframe.attr("src");
        String html = HttpUtil.get(Constants.LANZOU_URL + src);
        String sign = StringUtils.substringBetween(html, "var sg = '", "';");
        System.out.println(sign);
        Map<String, String> params = new HashMap<>();
        params.put("action", "downprocess");
        params.put("sign", sign);
        params.put("ves", "1");
        String json = HttpUtil.post(Constants.LANZOU_URL + "/ajaxm.php", params);
        DirectUrl directUrl = JSON.parseObject(json, DirectUrl.class);
        String sss = directUrl.getDom() + "/file/" + directUrl.getUrl();


        HttpGet HttpGet = new HttpGet("http://202.99.102.23/TJTAX_NET/NetLevy/NetQuery/TicketUse/index.jsp");
        HttpResponse response = httpClient.execute(HttpGet);
        String result = EntityUtils.toString(response.getEntity());
        System.out.println(result);
        System.out.println("--------------------------------------------------------------------------------------");
        HttpGet = new HttpGet("http://202.99.102.23/servlet/com.appinf.bus.TaskBus?_TaskID=tjtax.declevy.NetQuery.TicketQuery.si.C_UseTicketQuerySI_siTicketQuery&_SIVO=tjtax.declevy.NetQuery.TicketQuery.vo.C_UseTicketQueryVO&_SessionName=%2FTJTAX_NET%2FNetLevy%2FNetQuery%2FTicketUse%2Findex.jsp&cxfs=1&S_INVOICECODE=&S_TICKETCODE=&S_TICKETCHECKCODE=212001304013099160045971&S_KPJE=3454.76");
        response = httpClient.execute(HttpGet);
        result = EntityUtils.toString(response.getEntity());
        System.out.println(result);
        System.out.println("--------------------------------------------------------------------------------------");
        HttpGet = new HttpGet("http://202.99.102.23/TJTAX_NET/NetLevy/NetQuery/TicketUse/edit.jsp");
        response = httpClient.execute(HttpGet);
        result = EntityUtils.toString(response.getEntity());
        System.out.println(result);
    }
}
