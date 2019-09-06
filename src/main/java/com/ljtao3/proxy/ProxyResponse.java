package com.ljtao3.proxy;

/**
 * Created by jimin on 16/4/28.
 */
public class ProxyResponse {

    private final static String DEFAULT_IP = "127.0.0.1"; // 直连时默认的ip

    private final static int DEFAULT_PORT = 80; // 直连时默认的端口

    private final static Proxy DEFAULT_PROXY = new Proxy(DEFAULT_IP, DEFAULT_PORT, true, true);

    private Proxy proxy;
    /**
     * 代理测试使用的url
     */
    private String url;
    /**
     * 代理测试url返回的时间
     */
    private long cost;
    /**
     * 代理测试url是否可访问
     */
    private boolean canVisit;
    /**
     * 代理测试的时间
     */
    private long current = System.currentTimeMillis();

    public ProxyResponse() {
    }

    public ProxyResponse(Proxy proxy, String url, long cost, boolean canVisit) {
        this.proxy = proxy;
        this.url = url;
        this.cost = cost;
        this.canVisit = canVisit;
    }

    public ProxyResponse(String url, long cost, boolean canVisit) {
        this.proxy = DEFAULT_PROXY;
        this.url = url;
        this.cost = cost;
        this.canVisit = canVisit;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public boolean isCanVisit() {
        return canVisit;
    }

    public void setCanVisit(boolean canVisit) {
        this.canVisit = canVisit;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public static ProxyResponse success(Proxy proxy, String url, long cost) {
        return new ProxyResponse(proxy, url, cost, true);
    }

    public static ProxyResponse success(String url, long cost) {
        return new ProxyResponse(url, cost, true);
    }

    public static ProxyResponse failed(Proxy proxy, String url, long cost) {
        return new ProxyResponse(proxy, url, cost, false);
    }

    public static ProxyResponse failed(String url, long cost) {
        return new ProxyResponse(url, cost, false);
    }
}
