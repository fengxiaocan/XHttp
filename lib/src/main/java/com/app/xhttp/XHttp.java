package com.app.xhttp;

import com.app.xhttp.base.IConnectRequest;
import com.app.xhttp.config.XConfig;
import com.app.xhttp.core.HttpConnect;
import com.app.xhttp.core.HttpDownload;
import com.app.xhttp.core.XDownloadRequest;
import com.app.xhttp.core.XHttpRequest;
import com.app.xhttp.core.XHttpRequestQueue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class XHttp {

    private static XHttp xDownload;
    private XConfig setting;
    private Map<String, IConnectRequest> connectMap = new HashMap<>();
    private Map<String, List<IConnectRequest>> downloadMap = new HashMap<>();

    private XHttp() {
    }

    public static synchronized XHttp get() {
        if (xDownload == null) {
            xDownload = new XHttp();
        }
        return xDownload;
    }

    /**
     * 创建一个http请求
     *
     * @param baseUrl
     * @return
     */
    public static HttpConnect request(String baseUrl) {
        return XHttpRequest.with(baseUrl);
    }

    /**
     * 创建http请求队列
     *
     * @param queue
     * @return
     */
    public static HttpConnect requests(List<String> queue) {
        return XHttpRequestQueue.with(queue);
    }

    /**
     * 创建http请求队列
     *
     * @return
     */
    public static HttpConnect requests() {
        return XHttpRequestQueue.create();
    }

    /**
     * 创建一个下载任务
     *
     * @param baseUrl
     * @return
     */
    public static HttpDownload download(String baseUrl) {
        return XDownloadRequest.with(baseUrl);
    }

    public XHttp config(XConfig setting) {
        this.setting = setting;
        return this;
    }

    public synchronized XConfig config() {
        if (setting == null) {
            String cachePath = new File(System.getProperty("user.dir"), "xDownload").getAbsolutePath();
            setting = new XConfig(cachePath);
        }
        return setting;
    }

    public void addRequest(String tag, IConnectRequest connect) {
        connectMap.put(tag, connect);
    }

    public IConnectRequest removeRequest(String tag) {
        return connectMap.remove(tag);
    }

    public void addDownload(String tag, IConnectRequest download) {
        List<IConnectRequest> requestList = downloadMap.get(tag);
        if (requestList != null) {
            requestList.add(download);
        } else {
            requestList = new ArrayList<>();
            requestList.add(download);
            downloadMap.put(tag, requestList);
        }
    }

    public List<IConnectRequest> removeDownload(String tag) {
        return downloadMap.remove(tag);
    }

    /**
     * 取消请求
     *
     * @param tag
     * @return
     */
    public boolean cancelRequest(String tag) {
        IConnectRequest request = connectMap.remove(tag);
        if (request != null) {
            return request.cancel();
        }
        return false;
    }

    /**
     * 取消下载
     *
     * @param tag
     * @return
     */
    public boolean cancelDownload(String tag) {
        List<IConnectRequest> list = downloadMap.remove(tag);
        if (list != null) {
            boolean isCancel = false;
            for (IConnectRequest request : list) {
                boolean cancel = request.cancel();
                if (!isCancel) {
                    isCancel = cancel;
                }
            }
            return isCancel;
        }
        return false;
    }

    public static ThreadPoolExecutor singleQueue() {
        return ExecutorGather.singleQueue();
    }

    public static ThreadPoolExecutor executorDownloadQueue() {
        return ExecutorGather.executorDownloadQueue();
    }

    public static ThreadPoolExecutor executorHttpQueue() {
        return ExecutorGather.executorHttpQueue();
    }
}
