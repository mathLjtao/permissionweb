package com.ljtao3.util;


import com.google.common.collect.Lists;
import com.ljtao3.common.ThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by jimin on 16/5/5.
 */
@Slf4j
public class HttpUtil {

    /**
     * 默认连接超时时间
     */
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

    /**
     * 默认读取超时时间
     */
    private static final int DEFAULT_SOCKET_TIMEOUT = 60000;

    /**
     * 默认读取超时时间
     */
    private static final int DEFAULT_CONN_REQUEST_TIMEOUT = 5000;

    /**
     * 最大连接数
     */
    private static final int DEFAULT_MAX_CONN_TOTAL = 200;

    /**
     * 每个host最大连接数
     */
    private static final int DEFAULT_MAX_CONN_PER_ROUTE = 20;

    /**
     * 使用缺省配置生成httpClient
     *
     * @return
     */
    public static HttpClient defaultClient() {
        return HttpClients.custom().setConnectionManager(getPoolingClientConnectionManager())
                .setDefaultRequestConfig(getRequestConfig(DEFAULT_CONNECT_TIMEOUT, DEFAULT_SOCKET_TIMEOUT, DEFAULT_CONN_REQUEST_TIMEOUT))
                .setMaxConnTotal(DEFAULT_MAX_CONN_TOTAL).setMaxConnPerRoute(DEFAULT_MAX_CONN_PER_ROUTE).build();
    }

    /**
     * 缺省connectionManager
     *
     * @return
     */
    public static PoolingHttpClientConnectionManager getPoolingClientConnectionManager() {
        try {
            SSLContext sslContext = SSLContexts.custom().useTLS().build();
            sslContext.init(null, new TrustManager[] { new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            } }, null);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", new SSLConnectionSocketFactory(sslContext)).build();

            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            return connManager;
        } catch (Exception e) {
            log.error("build client connection manager failed", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成requestConfig
     *
     * @param connectTimeout           请求超时
     * @param socketTimeout            接口响应超时
     * @param connectionRequestTimeout 获取连接超时
     * @return
     */
    public static RequestConfig getRequestConfig(int connectTimeout, int socketTimeout, int connectionRequestTimeout) {
        return RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout)
                .build();
    }

    /**
     * post请求
     *
     * @param reqURL
     * @param params
     * @return
     */
    public static String executePOST(HttpClient client, String reqURL, Map<String, String> params, final int retryTimes, HttpClientContext context)
            throws Exception {
        HttpPost httpPost = new HttpPost(reqURL);
        List<NameValuePair> reqParams = buildPostParams(params);
        httpPost.setEntity(new UrlEncodedFormEntity(reqParams, "utf-8"));
        return executePOST(client, httpPost, retryTimes, context);
    }

    /**
     * 执行post请求
     *
     * @param client     指定使用的client
     * @param httpPost   调用者给出post实例，进行header、cookie等设定
     * @param retryTimes 失败重试次数，需调用者给出
     * @return
     * @throws Exception
     */
    public static String executePOST(HttpClient client, HttpPost httpPost, final int retryTimes, HttpClientContext context) throws Exception {
        String responseText = StringUtils.EMPTY;

        for (int i = 0; i < retryTimes; i++) {
            HttpEntity resEntity = null;
            try {
                HttpResponse response = client.execute(httpPost, context);
                resEntity = response.getEntity();
                StatusLine status = response.getStatusLine();
                if (status != null && status.getStatusCode() == 200) {
                    responseText = EntityUtils.toString(resEntity, "utf-8");
                } else {
                    log.error("httpPost请求响应状态异常, url:{}, responseStatus:{}", httpPost.getURI(), status);
                    throw new HttpException(String.format("请求响应状态异常!状态码=%s,url=%s", status, httpPost.getURI()));
                }

            } catch (Exception e) {
                log.error("httpPost请求异常, url:{}, errmsg:{}", httpPost.getURI(), e.getMessage(), e);
                if (i == retryTimes) {
                    throw e;
                }
            } finally {
                httpPost.releaseConnection();
                EntityUtils.consume(resEntity);
            }
        }
        return responseText;
    }

    /**
     * get请求
     *
     * @param client
     * @param reqURL
     * @param retryTimes
     * @return
     * @throws Exception
     */
    public static String executeGet(HttpClient client, String reqURL, final int retryTimes, HttpClientContext context) throws Exception {
        HttpGet httpGet = new HttpGet(reqURL);
        return executeGet(client, httpGet, retryTimes, context);
    }

    /**
     * 执行get请求
     *
     * @param client     指定client
     * @param retryTimes
     * @return
     * @throws Exception
     */
    public static String executeGet(HttpClient client, HttpGet httpGet, final int retryTimes, HttpClientContext context) throws Exception {
        String responseText = StringUtils.EMPTY;

        for (int i = 0; i < retryTimes; i++) {
            HttpEntity resEntity = null;
            try {
                HttpResponse response = client.execute(httpGet, context);
                resEntity = response.getEntity();
                StatusLine status = response.getStatusLine();
                if (status != null && status.getStatusCode() == 200) {
                    responseText = EntityUtils.toString(resEntity, "utf-8");
                } else {
                    log.error("httpGet请求响应状态异常, url:{}, responseStatus:{}", httpGet.getURI().toURL(), status);
                    throw new HttpException(String.format("请求响应状态异常!状态码=%s,url=%s", status, httpGet.getURI().toURL()));
                }
            } catch (Exception e) {
                log.error("httpGet请求异常, url:{}, errmsg:{}", httpGet.getURI().toURL(), e.getMessage(), e);
                if (i == retryTimes) {
                    throw e;
                }
            } finally {
                httpGet.releaseConnection();
                EntityUtils.consume(resEntity);
            }
        }
        return responseText;
    }

    public static List<NameValuePair> buildPostParams(Map<String, String> reqParams) {
        List<NameValuePair> params = Lists.newArrayList();
        for (Entry<String, String> param : reqParams.entrySet()) {
            params.add(new BasicNameValuePair(param.getKey(), param.getValue()));
        }
        return params;
    }

    /**
     * 根据URL 组装 HTTP POST
     *
     * @param url
     * @param json
     * @return
     * @throws UnsupportedEncodingException
     */
    public static HttpPost getPost(String url, String json) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(json, "utf-8");
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);
        httpPost.setHeader(new BasicHeader("Accept", "application/json"));
        return httpPost;

    }

    /**
     * 使用post请求获取url内容（HTTPS）
     *
     * @param httpPost       请求地址
     * @param connectTimeout 连接超时时间
     * @param socketTimeout  读取超时时间
     */
    public static String getPostSSLContent(HttpPost httpPost, int connectTimeout, int socketTimeout) throws Exception {
        return process(httpPost, connectTimeout, socketTimeout, true);
    }

    private static String process(final HttpRequestBase httpUriRequest, int connectTimeout, int socketTimeout, boolean isSSL) throws Exception {
        final DefaultHttpClient client = getClient(connectTimeout, socketTimeout, isSSL);
        FutureTask<String> fu = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String resultString = "";
                HttpResponse response = client.execute(httpUriRequest);
                HttpEntity entity = null;
                try {
                    entity = response.getEntity();
                    StatusLine status = response.getStatusLine();
                    if (status != null && status.getStatusCode() == 200) {
                        resultString = EntityUtils.toString(entity, "UTF-8");
                        return resultString;
                    }
                } catch (Exception e) {
                    throw e;
                } finally {
                    EntityUtils.consume(entity);
                    entity = null;
                }
                return resultString;
            }
        });
        ThreadPool.execute(fu);
        String content = fu.get(connectTimeout, TimeUnit.MILLISECONDS);
        return content;
    }

    private static DefaultHttpClient getClient(int connectTimeout, int socketTimeout, boolean isSSL) throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
        if (isSSL) {
            client.getConnectionManager().getSchemeRegistry().register(getHttpsSupportScheme());
        }
        return client;
    }

    private static Scheme getHttpsSupportScheme() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return new Scheme("https", 443, ssf);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (KeyManagementException e) {
            return null;
        }
    }
}
