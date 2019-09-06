package com.ljtao3.proxy;

/**
 * Created by jimin on 16/4/28.
 */
public class Proxy {

    /**
     * IP
     */
    private String ip;

    /**
     * port
     */
    private int port;

    /**
     * 是否可用
     */
    private boolean alive;

    /**
     * 是否为本机
     * 主要是为了在ProxyManager中保存最佳的Proxy, concurrentMap中不能存null,使用该字段代表为直连
     */
    private boolean isLocal = false;

    public Proxy() {
    }

    public Proxy(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Proxy(String ip, int port, boolean alive) {
        this.ip = ip;
        this.port = port;
        this.alive = alive;
    }

    public Proxy(String ip, int port, boolean alive, boolean isLocal) {
        this.ip = ip;
        this.port = port;
        this.alive = alive;
        this.isLocal = isLocal;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Proxy proxy = (Proxy) o;

        if (port != proxy.port)
            return false;
        return !(ip != null ? !ip.equals(proxy.ip) : proxy.ip != null);

    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "Proxy{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", alive=" + alive +
                ", isLocal=" + isLocal +
                '}';
    }
}
