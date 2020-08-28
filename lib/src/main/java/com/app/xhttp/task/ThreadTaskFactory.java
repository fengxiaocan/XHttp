package com.app.xhttp.task;

import com.app.xhttp.ExecutorGather;
import com.app.xhttp.XHttp;
import com.app.xhttp.core.XDownloadRequest;
import com.app.xhttp.core.XHttpRequest;
import com.app.xhttp.core.XHttpRequestQueue;
import com.app.xhttp.impl.DownloadListenerDisposer;

import java.util.List;
import java.util.concurrent.Future;

public final class ThreadTaskFactory{
    public static void createDownloadTask(XDownloadRequest request){
        final DownloadListenerDisposer disposer=new DownloadListenerDisposer(request.getSchedulers(),
                                                                             request.getOnDownloadConnectListener(),
                                                                             request.getOnDownloadListener(),
                                                                             request.getOnProgressListener(),
                                                                             request.getOnSpeedListener());
        HttpDownloadThreadTask requestTask=new HttpDownloadThreadTask(request,disposer,0);
        Future future=XHttp.executorDownloadQueue().submit(requestTask);
        XHttp.get().addDownload(request.getTag(),requestTask);
        requestTask.setTaskFuture(future);
    }

    public static void createHttpRequestTask(XHttpRequest request){
        HttpRequestTask requestTask=new HttpRequestTask(request,
                                                        request.getOnConnectListeners(),
                                                        request.getOnResponseListeners());
        Future future=XHttp.executorHttpQueue().submit(requestTask);
        XHttp.get().addRequest(request.getTag(),requestTask);
        requestTask.setTaskFuture(future);
    }

    public static void createHttpRequestTaskQueue(XHttpRequestQueue request){
        List<XHttpRequest> requests=request.cloneToRequest();
        for(XHttpRequest httpRequest: requests){
            createHttpRequestTask(httpRequest);
        }
    }
}
