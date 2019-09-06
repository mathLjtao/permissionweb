package com.ljtao3.proxy;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

/**
 * Created by jimin on 16/5/3.
 */
public interface UrlConnectionChecker {

    /**
     * 检查使用的url
     * 注意: 不需要保证url的参数的正确性, 处理的是http状态码
     */
    String url();

    /**
     * 如果是Post请求, 需要给出该类的实例, 否则为空
     * 注意: 如果需要设置header及往body里放置data等, 都在返回的实例上设置好
     */
    HttpPost httpPost();

    /**
     * 如果是Get请求, 需要给出该类的实例, 否则为空
     */
    HttpGet httpGet();

    /**
     * 检查使用的httpClient
     */
    HttpClient httpClient();
}
