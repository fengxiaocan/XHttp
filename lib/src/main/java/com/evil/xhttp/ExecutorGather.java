package com.evil.xhttp;


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ExecutorGather {
    private static final int MAIN_TASK_KEEP_ALIVE_TIME = 60;//主任务线程保活时间
    private static final int SUBTASK_KEEP_ALIVE_TIME = 1;//子任务线程保活时间

    private static ThreadPoolExecutor taskExecutor;
    private static ThreadPoolExecutor newExecutor;
    private static ThreadPoolExecutor singleExecutor;

    /**
     * 创建子任务线程池队列
     *
     * @return
     */
    public static synchronized ThreadPoolExecutor singleQueue() {
        if (singleExecutor == null) {
            singleExecutor = new ThreadPoolExecutor(1,
                    Integer.MAX_VALUE,
                    SUBTASK_KEEP_ALIVE_TIME,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>());
            singleExecutor.allowCoreThreadTimeOut(true);
        }
        return singleExecutor;
    }

    public static synchronized ThreadPoolExecutor executorDownloadQueue() {
        if (taskExecutor == null) {
            taskExecutor = new ThreadPoolExecutor(XHttp.get().config().getSameTimeDownloadMaxCount(),
                    Integer.MAX_VALUE,
                    MAIN_TASK_KEEP_ALIVE_TIME,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>());
            taskExecutor.allowCoreThreadTimeOut(true);
        }
        return taskExecutor;
    }

    public static synchronized ThreadPoolExecutor executorHttpQueue() {
        if (newExecutor == null) {
            newExecutor = new ThreadPoolExecutor(XHttp.get().config().getSameTimeHttpRequestMaxCount(),
                    Integer.MAX_VALUE,
                    MAIN_TASK_KEEP_ALIVE_TIME,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }
        return newExecutor;
    }
}
