package com.eg.lanzouserver.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

/**
 * Http工具类
 *
 * @author Administrator
 */
public class HttpUtil {
    private static String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36";
    private static String contentType = "application/x-www-form-urlencoded";
    private static String acceptLanguage = "zh-CN,zh;q=0.9";

    /**
     * 简单get请求
     *
     * @param url
     * @return
     */
    public static String get(String url) {
        System.out.println("HttpClient GET: " + url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet();
        httpGet.addHeader("User-Agent", userAgent);
        httpGet.setHeader("Content-type", contentType);
        httpGet.setHeader("Accept-Language", acceptLanguage);
        httpGet.setURI(URI.create(url));
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        try {
            return EntityUtils.toString(entity, "utf-8");
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 简单post请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        System.out.println("HttpClient POST: " + url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<>();
        if (params != null) {
            for (Entry<String, String> entry : params.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        httpPost.setHeader("User-Agent", userAgent);
        httpPost.setHeader("Content-type", contentType);
        httpPost.setHeader("Accept-Language", acceptLanguage);
        String body = null;
        try {
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return body;
    }

    /**
     * 带header参数的post请求
     *
     * @param url
     * @param headerMap
     * @param params
     * @return
     */
    public static String post(String url, Map<String, String> headerMap, String params) {
        System.out.println("HttpClient POST (header): " + url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", userAgent);
        httpPost.setHeader("Content-type", contentType);
        httpPost.setHeader("Accept-Language", acceptLanguage);
        Set<String> keySet = headerMap.keySet();
        for (String key : keySet) {
            httpPost.setHeader(key, headerMap.get(key));
        }
        try {
            httpPost.setEntity(new StringEntity(params));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String body = null;
        try {
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return body;
    }

    /**
     * 上传文件
     *
     * @param url
     * @param header
     * @param param
     * @param uploadFileKey
     * @param file
     * @return
     * @throws IOException
     */
    public static String uploadFile(
            String url, Map<String, String> header, Map<String, String> param,
            String uploadFileKey, File file) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", userAgent);
        httpPost.setHeader("Accept-Language", acceptLanguage);
        Set<String> keySet = header.keySet();
        for (String key : keySet) {
            httpPost.setHeader(key, header.get(key));
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody(uploadFileKey, file);
        Set<String> paramKeySet = param.keySet();
        for (String key : paramKeySet) {
            builder.addTextBody(key, param.get(key));
        }
        httpPost.setEntity(builder.build());
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String res;
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            res = EntityUtils.toString(entity, "UTF-8");
            response.close();
        } else {
            res = EntityUtils.toString(entity, "UTF-8");
            response.close();
            throw new IllegalArgumentException(res);
        }
        return res;
    }

}
