package com.seezoon.boot.configuration;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步配置
 */
@Configuration
public class CustomAsyncConfiguration extends AsyncConfigurerSupport {

    private static final int corePoolSize = 1;            // 核心线程数（默认线程数）
    private static final int maxPoolSize = 100;                // 最大线程数
    private static final int keepAliveTime = 60;            // 允许线程空闲时间（单位：默认为秒）
    private static final int queueCapacity = 1000;            // 缓冲队列数
    private static final String threadNamePrefix = "Async-Service-"; // 线程池名前缀
    private static Logger logger = LoggerFactory.getLogger(CustomAsyncConfiguration.class);

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        //lambda表达式用作异常处理程序
        return (throwable, method, obj) -> {
            logger.error("Async exception method name :{},parmas:{}", method.getName(), JSON.toJSONString(obj), throwable);
        };
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.setTaskDecorator((runnable) -> {
            Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (null != copyOfContextMap) {
                        MDC.setContextMap(copyOfContextMap);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        });
        // 线程池对拒绝任务的处理策略
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                logger.error("Async exception:task rejected");
            }
        });
        // 初始化
        executor.initialize();
        return executor;
    }
}

