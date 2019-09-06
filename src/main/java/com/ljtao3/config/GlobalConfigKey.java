package com.ljtao3.config;

public class GlobalConfigKey {
    /**
     * 通过数据库中的值生成
     * select CONCAT('public final static String ', REPLACE(UPPER(k), '.', '_'), ' = "', k, '";') from configuration
     */

    /**
     * 这一部分为实际值
     */
    public final static String MAIL_SEND_PASSWORD = "mail.send.password";
    public final static String MAIL_SEND_NICKNAME = "mail.send.nickname";
    public final static String MAIL_SEND_FROM = "mail.send.from";
    public final static String MAIL_SEND_PORT = "mail.send.port";
    public final static String MAIL_SEND_SMTP = "mail.send.smtp";

    public final static String FILE_UPLOAD_PATH = "file.upload.path";
    public final static String FILE_MAPPING_PATH = "file.mapping.path";

    public final static String LOGBACK_FILTER_MSG = "logback.filter.msg";
    public final static String LOGBACK_EMAIL_OPEN = "logback.email.open";

    public final static String SQL_LIST_COUNT = "sql.list.count";
    public final static String SLOW_QUERY_MILLSECONDS = "slow.query.millseconds";

    public final static String ROOT_USER_NAME = "root.user.name";
    public final static String ACCESS_WHITELIST = "access.whiteList";
    public final static String NO_AUTH_PAGE = "no.auth.page";
    public final static String COOKIE_EXPIRE_SECONDS = "cookie.expire.seconds";
    public final static String COOKIE_USER_FLAG = "cookie.user.flag";

    public final static String CAPTCHA_CODE_INVALID_MINUTES = "captcha_code.invalid.minutes";
    public final static String CAPTCHA_CODE_ONE_MINUTE_MAX = "captcha_code.one_minute.max";
    public final static String CAPTCHA_CODE_VALIDATE_URL = "captcha_code.validate.url";

    public final static String HTTP_MAX_THREAD = "http.max.thread";
    public final static String HTTP_DEFAULT_CONNECTION_TIMEOUT = "http.default.connection.timeout";
    public final static String HTTP_DEFAULT_SOCKET_TIMEOUT = "http.default.socket.timeout";

    public final static String RABBITMQ_DEFAULT_QUEUE_NAME = "rabbitmq.default.queue.name";

    public final static String NOT_ALLOWED_URLS = "not.allowed.urls";
    public final static String PERCENT_ALLOWED_URLS = "percent.allowed.urls";
    public final static String SERVICE_DEGARDING_PAGE = "service.degarding.page";

    public final static String DEFAULT_EXECUTOR_CORESIZE = "default.executor.coreSize";
    public final static String DEFAULT_EXECUTOR_MAXSIZE = "default.executor.maxSize";
    public final static String DEFAULT_EXECUTOR_KEEPALIVE_SECONDS = "default.executor.keepAlive.seconds";
    public final static String DEFAULT_EXECUTOR_QUEUESIZE = "default.executor.queueSize";

    public final static String DEFAULT_PROXY_IPS = "default.proxy.ips";
    public final static String PROXY_IPS_SUFFIX = ".proxy.ips";
    public final static String PROXY_IPS_KEY = "proxy.keys";
    public final static String PROXY_VISIT_BASE_MILLSECONDS = "proxy.visit.base.millseconds";
    public final static String PROXY_FLAG = "proxy.flag";

    public final static String MACHINE_LIST = "machine.list";
    public final static String GLOBAL_QPS_LIMIT_SWITCH = "qps.limit.switch";

    /**
     * 这一部分为拼接用值
     */
    public final static String QPS_LIMIT_PREFFIX = "qps.";
    public final static String QPS_LIMIT_SUFFIX = ".rate";
}
