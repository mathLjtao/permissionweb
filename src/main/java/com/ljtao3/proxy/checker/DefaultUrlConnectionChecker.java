package com.ljtao3.proxy.checker;


import com.ljtao3.proxy.UrlConnectionChecker;

import com.ljtao3.util.HttpUtil;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

/**
 * Created by jimin on 16/5/4.
 */
public class DefaultUrlConnectionChecker implements UrlConnectionChecker {

    private String url;

    private HttpClient httpClient = null;

    public DefaultUrlConnectionChecker(String url) {
        this.url = url;
    }

    public DefaultUrlConnectionChecker(String url, HttpClient httpClient) {
        this.url = url;
        this.httpClient = httpClient;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public HttpPost httpPost() {
        return new HttpPost(url);
    }

    @Override
    public HttpGet httpGet() {
        return new HttpGet(url);
    }

    @Override
    public HttpClient httpClient() {
        if (httpClient == null) {
            return HttpUtil.defaultClient();
        }
        return httpClient;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
