package com.ljtao3.proxy;

import com.ljtao3.config.GlobalConfig;
import com.ljtao3.config.GlobalConfigKey;
import com.ljtao3.util.JsonMapper;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY;

/**
 * Created by jimin on 16/5/5.
 */

@Slf4j
public class ProxyManager {

    /**
     * 测试与代理连接的超时
     */
    private final static int CONNECT_TIME_OUT = 1000;

    private final static Splitter proxyIpPortSplitter = Splitter.on(",").trimResults().omitEmptyStrings();
    private final static Splitter proxyKeySplitter = Splitter.on(",").trimResults().omitEmptyStrings();

    private final static int USE_PROXY_BY_CALC = 0;
    private final static int FORCE_USE_PROXY = 1;
    private final static int FORCE_NOT_USE_PROXY = 2;

    /**
     * 当前支持自动切换代理的处理类
     */
    private static ImmutableMap<String, UrlConnectionChecker> urlConnectionCheckerMap = ImmutableMap.<String, UrlConnectionChecker>builder()
            //.put(key, DefaultUrlConnectionChecker)
            .build();

    /**
     * 代理列表
     */
    private final static Map<String, Set<Proxy>> proxies = Maps.newConcurrentMap();
    /**
     * 存储本机最佳效果的代理
     */
    private final static Map<String, ProxyResponse> bestProxyConnectResponseMap = Maps.newConcurrentMap();
    /**
     * 心跳检测线程
     */
    private static final ScheduledExecutorService proxyHealthCheckScheduler = Executors.newScheduledThreadPool(1);
    /**
     * 定时检测代理和直连的效果
     */
    private static final ScheduledExecutorService urlConnectCheckScheduler = Executors.newScheduledThreadPool(1);
    /**
     * 单例实例
     */
    private static ProxyManager proxyManager = new ProxyManager();

    private ProxyManager() {
        // 心跳检测代理是否有效
        /*
        proxyHealthCheckScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 10, 10, TimeUnit.SECONDS);
        */

        // 检测当前直连和代理中最佳的, 每次计算完延迟60s开始下一次
        /*
        urlConnectCheckScheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                calcBestUrlConnect();
            }
        }, 15, 60, TimeUnit.SECONDS);
        */
    }

    public static void reload(Map<String, String> conf) {

        //清空所有代理，重新加载
        proxies.clear();
        // 清空之前计算的结果
        bestProxyConnectResponseMap.clear();

        log.info("reload size:{}, key:{}, value:{}", conf.size(), conf.keySet(), conf.values());
        String proxyIpsKey = conf.get(GlobalConfigKey.PROXY_IPS_KEY);

        if (StringUtils.isEmpty(proxyIpsKey)) {
            return;
        }

        List<String> proxyIpsKeyList = proxyKeySplitter.splitToList(proxyIpsKey);

        for (String proxyKey : proxyIpsKeyList) {
            String proxyIps = conf.get(proxyKey + GlobalConfigKey.PROXY_IPS_SUFFIX);
            if (StringUtils.isBlank(proxyIps)) {
                proxyIps = conf.get(GlobalConfigKey.DEFAULT_PROXY_IPS);
                log.info("没有配置对应的代理,使用默认配置, key:{}, setting:{}", proxyKey, proxyIps);
            }
            if (StringUtils.isBlank(proxyIps)) {
                log.warn("没有配置对应的代理,也未配置默认的代理, key:{}", proxyKey);
                continue;
            }
            Set<Proxy> proxySet = proxies.get(proxyKey);
            if (proxySet == null) {
                proxySet = Sets.newConcurrentHashSet();
                proxies.put(proxyKey, proxySet);
            }

            for (String outEntry : proxyIpPortSplitter.split(proxyIps)) {
                String[] inEntry = StringUtils.split(outEntry, ":");
                if (ArrayUtils.isNotEmpty(inEntry) && inEntry.length == 2) {
                    Proxy proxy = new Proxy(inEntry[0], NumberUtils.toInt(inEntry[1], 7001));
                    proxy.setAlive(isConnect(proxy.getIp(), proxy.getPort()));
                    proxySet.add(proxy);
                } else {
                    log.warn("代理配置的有问题, 过滤, key:{}, str:{}", proxyKey, outEntry);
                }
            }
        }

        log.info("proxies:{}", proxies);
    }

    /**
     * 刷新代理状态
     */
    private static void refresh() {
        if (MapUtils.isEmpty(proxies)) {
            log.info("no proxy to refresh");
        }
        for (Iterator<String> iterator = proxies.keySet().iterator(); iterator.hasNext(); ) {
            String proxyKey = iterator.next();
            Set<Proxy> proxySet = proxies.get(proxyKey);

            for (Iterator<Proxy> it = proxySet.iterator(); it.hasNext(); ) {
                Proxy proxy = it.next();
                proxy.setAlive(isConnect(proxy.getIp(), proxy.getPort()));
            }
        }
    }

    /**
     * 验证代理是否能连接
     */
    private static boolean isConnect(String ip, int port) {
        try {
            TelnetClient client = new TelnetClient();
            client.setDefaultTimeout(CONNECT_TIME_OUT);
            client.connect(ip, port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 处理代理
     *
     * @param client
     */
    public static void handleProxy(String proxyKey, HttpClient client, String url) {
        try {
            if (StringUtils.isEmpty(proxyKey)) {
                return;
            }
            Proxy proxy = getProxy(proxyKey);
            if (proxy != null && proxy.isAlive()) {
                HttpHost httpHost = new HttpHost(proxy.getIp(), proxy.getPort());
                client.getParams().setParameter(DEFAULT_PROXY, httpHost);
                log.info("使用代理, proxyKey={}, url={}, proxy={}", proxyKey, url, proxy);
            } else {
                client.getParams().removeParameter(DEFAULT_PROXY);
            }
        } catch (Exception e) {
            log.warn("proxyKey:{}, url:{} , exception:{}", proxyKey, url, e.toString(), e);
        }
    }

    /**
     * 获取代理ip端口
     *
     * @return 可以使用的代理, null代表不走代理
     */
    private static Proxy getProxy(String proxyKey) {
        int proxyFlag = GlobalConfig.getIntValue(GlobalConfigKey.PROXY_FLAG, USE_PROXY_BY_CALC);
        if (proxyFlag == FORCE_NOT_USE_PROXY) {
            // 强制不走代理
            // LOG.info("强制不走代理, key:{}", proxyKey);
            return null;
        }

        // 1 强制走代理
        // 2 没有配置自动检测url的,但是却在要切代理的列表里, 这种case主要用于处理那些通过代理商id切换代理
        if (proxyFlag == FORCE_USE_PROXY || (!urlConnectionCheckerMap.containsKey(proxyKey) && proxies.containsKey(proxyKey))) {
            // 强制走代理
            Set<Proxy> proxySet = getProxiesByKey(proxyKey);
            if (CollectionUtils.isNotEmpty(proxySet)) {
                for (int i = 0; i < proxySet.size(); i++) {
                    Proxy proxy = (Proxy) proxySet.toArray()[i];
                    if (proxy.isAlive()) {
                        // LOG.info("强制走代理, key:{}", proxyKey);
                        return proxy;
                    }
                }
            }
        }
        if (!urlConnectionCheckerMap.containsKey(proxyKey)) {
            return null;
        }
        ProxyResponse proxyResponse = bestProxyConnectResponseMap.get(proxyKey);
        if (proxyResponse == null || !proxyResponse.isCanVisit()) {
            // 都连不通, 走直连
            return null;
        }
        if (proxyResponse.getProxy().isLocal()) {
            // 代表是直连的
            return null;
        }
        return proxyResponse.getProxy();
    }

    /**
     * 获取代理管理实例
     */
    public static ProxyManager getProxyManager() {
        return proxyManager;
    }

    /**
     * 允许外部查询当前指定key配置的代理集合
     */
    public static Set<Proxy> getProxiesByKey(String proxyKey) {
        if (proxies.containsKey(proxyKey)) {
            return proxies.get(proxyKey);
        }
        return null;
    }

    /**
     * 计算每个interfaceCode最佳的代理, 存储到bestProxyConnectResponseMap中
     * 如果选择直连, 则bestProxyConnectResponseMap中存储的信息为null
     */
    private static void calcBestUrlConnect() {
        String proxyKeys = GlobalConfig.getStringValue(GlobalConfigKey.PROXY_IPS_KEY, "");
        if (StringUtils.isEmpty(proxyKeys)) {
            log.info("没有需要走自动代理的配置");
            return;
        }
        List<String> proxyKeyList = proxyKeySplitter.splitToList(proxyKeys);
        for (String proxyKey : proxyKeyList) {
            UrlConnectionChecker checker = urlConnectionCheckerMap.get(proxyKey);
            if (checker == null) {
                // 只处理当前系统已经支持的和qconfig配置的交集部分
                log.warn("没有实现检测类, key:{}", proxyKey);
                continue;
            }

            // 先计算直连的效果
            ProxyResponse directResponse = checkUrlConnection(proxyKey, checker, null);
            log.info("直连检测, key:{}, {}", proxyKey, JsonMapper.obj2String(directResponse));

            // 直连效果已经很好,就不需要关注代理了(代理尽量少用)
            if (directResponse != null && directResponse.isCanVisit() && directResponse.getCost() < GlobalConfig
                    .getIntValue(GlobalConfigKey.PROXY_VISIT_BASE_MILLSECONDS, 5000)) {
                bestProxyConnectResponseMap.put(proxyKey, toLocalResponse(directResponse));
                log.info("直连效果可以,不需要继续尝试代理了, key:{}", proxyKey);
                continue;
            }

            // 计算代理的效果
            ProxyResponse bestProxyResponse = directResponse;
            boolean isDirectBest = true; // 记录直连是否是最佳的
            Set<Proxy> proxySet = getProxiesByKey(proxyKey);
            for (Proxy proxy : proxySet) {
                if (proxy.isAlive()) { // 如果这个代理是活着的,去测试对应的test url
                    ProxyResponse proxyResponse = checkUrlConnection(proxyKey, checker, proxy);
                    log.info("代理检测, key:{}, {}", proxyKey, JsonMapper.obj2String(proxyResponse));
                    // 选出最佳效果的代理
                    if (proxyResponse != null && proxyResponse.isCanVisit()) {
                        if (proxyResponse.getCost() < bestProxyResponse.getCost()) {
                            isDirectBest = false;
                            bestProxyResponse = proxyResponse;
                        }
                    }
                }
            }
            if (!isDirectBest) {
                log.info("本次检测到最佳的代理, key:{}, {}", proxyKey, JsonMapper.obj2String(bestProxyResponse));
                bestProxyConnectResponseMap.put(proxyKey, bestProxyResponse);
            } else {
                bestProxyConnectResponseMap.put(proxyKey, toLocalResponse(directResponse));
            }
        }
    }

    private static ProxyResponse toLocalResponse(ProxyResponse proxyResponse) {
        return new ProxyResponse(proxyResponse.getUrl(), proxyResponse.getCost(), proxyResponse.isCanVisit());
    }

    private static ProxyResponse checkUrlConnection(String key, UrlConnectionChecker checker, Proxy proxy) {
        Preconditions.checkNotNull(checker, "请求信息不可以为空");
        final HttpClient httpClient = checker.httpClient();
        final String url = checker.url();
        if (proxy != null) {
            httpClient.getParams().setParameter(DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
        } else {
            httpClient.getParams().removeParameter(DEFAULT_PROXY);
        }

        long start = System.currentTimeMillis();
        HttpResponse response = null;
        try {
            if (checker.httpGet() != null) {
                response = httpClient.execute(checker.httpGet());
            } else {
                response = httpClient.execute(checker.httpPost());
            }
            StatusLine status = response.getStatusLine();
            if (status != null && status.getStatusCode() == HttpStatus.SC_OK) {
                return ProxyResponse.success(proxy, url, System.currentTimeMillis() - start);
            } else {
                return ProxyResponse.failed(proxy, url, System.currentTimeMillis() - start);
            }
        } catch (SocketTimeoutException e1) {
            log.info("key:{}, exception: SocketTimeoutException, proxy:{}", key, JsonMapper.obj2String(proxy));
            return ProxyResponse.failed(proxy, url, System.currentTimeMillis() - start);
        } catch (ConnectTimeoutException e2) {
            log.info("key:{}, exception: ConnectTimeoutException, proxy:{}", key, JsonMapper.obj2String(proxy));
            return ProxyResponse.failed(proxy, url, System.currentTimeMillis() - start);
        } catch (IOException e3) {
            log.info("key:{}, exception: IOException, proxy:{}", key, JsonMapper.obj2String(proxy));
            return ProxyResponse.failed(proxy, url, System.currentTimeMillis() - start);
        } catch (Throwable t) {
            log.warn(String.format("测试出现未知异常, key:%s, url:%s, %s", key, url, JsonMapper.obj2String(proxy)), t);
            return ProxyResponse.success(proxy, url, System.currentTimeMillis() - start);
        } finally {
            if (checker.httpGet() != null) {
                checker.httpGet().releaseConnection();
            }
            if (checker.httpPost() != null) {
                checker.httpPost().releaseConnection();
            }
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (Throwable t) {
                    log.warn(String.format("EntityUtils.consume(entity)出现异常, url:%s, %s", url, JsonMapper.obj2String(proxy)), t);
                }
            }
        }
    }
}
