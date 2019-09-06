package com.ljtao3.common;



import com.ljtao3.config.GlobalConfig;
import com.ljtao3.config.GlobalConfigKey;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jimin on 15/12/1.
 */
public class ThreadPool {

    private static ExecutorService defaultExecutor;  // 线程池

    /****** 初始化线程池 ******/
    static {
        defaultExecutor = new ThreadPoolExecutor(GlobalConfig.getIntValue(GlobalConfigKey.DEFAULT_EXECUTOR_CORESIZE, 40),     // 核心池大小
                GlobalConfig.getIntValue(GlobalConfigKey.DEFAULT_EXECUTOR_MAXSIZE, 100),                                      // 最大线程数
                GlobalConfig.getIntValue(GlobalConfigKey.DEFAULT_EXECUTOR_KEEPALIVE_SECONDS, 120),                            // 空闲等待时间
                TimeUnit.SECONDS,                                                                                             // 时间单位
                new ArrayBlockingQueue<Runnable>(GlobalConfig.getIntValue(GlobalConfigKey.DEFAULT_EXECUTOR_QUEUESIZE, 1000)), // 循环数组 + 指定大小
                new ThreadPoolExecutor.DiscardOldestPolicy()                                                                  // 抛弃最早的请求
        );
    }

    /**
     * 在未来某个时间执行给定的命令
     */
    public static void execute(Runnable runnable) throws RejectedExecutionException {
        defaultExecutor.execute(runnable);
    }

    /**
     * 提交一个 Runnable 任务用于执行,并返回一个表示该任务的 Future
     */
    public static Future<?> submit(Runnable runnable) throws RejectedExecutionException {
        return defaultExecutor.submit(runnable);
    }
}