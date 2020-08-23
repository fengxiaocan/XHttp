package com.evil.xhttp.task;


import com.evil.xhttp.XHttp;
import com.evil.xhttp.base.IConnectRequest;
import com.evil.xhttp.base.IRequest;
import com.evil.xhttp.base.RequestBody;
import com.evil.xhttp.core.HttpConnect;
import com.evil.xhttp.core.XHttpRequest;
import com.evil.xhttp.data.Headers;
import com.evil.xhttp.data.MediaType;
import com.evil.xhttp.data.Response;
import com.evil.xhttp.impl.RequestListenerDisposer;
import com.evil.xhttp.listener.OnConnectListener;
import com.evil.xhttp.listener.OnResponseListener;
import com.evil.xhttp.made.AutoRetryRecorder;
import com.evil.xhttp.tool.XHttpUtils;

import java.net.HttpURLConnection;
import java.util.concurrent.Future;

class HttpRequestTask extends BaseHttpRequest implements IRequest, IConnectRequest{

    protected final XHttpRequest httpRequest;
    protected final RequestListenerDisposer listenerDisposer;
    protected Future taskFuture;

    public HttpRequestTask(
            XHttpRequest request,OnConnectListener connectListeners,OnResponseListener onResponseListeners)
    {
        super(new AutoRetryRecorder(request.isUseAutoRetry(),
                                    request.getAutoRetryTimes(),
                                    request.getAutoRetryInterval()));
        this.listenerDisposer=new RequestListenerDisposer(request.getSchedulers(),connectListeners,onResponseListeners);
        this.httpRequest=request;
        listenerDisposer.onPending(this);
    }

    public final void setTaskFuture(Future taskFuture){
        this.taskFuture=taskFuture;
    }

    @Override
    public void run(){
        listenerDisposer.onStart(this);
        super.run();
        XHttp.get().removeRequest(httpRequest.getTag());
    }

    @Override
    protected void httpRequest() throws Exception{
        HttpURLConnection http=httpRequest.buildConnect();

        listenerDisposer.onConnecting(this);
        //POST请求
        if(httpRequest.isPost()){
            RequestBody body=httpRequest.getRequestBody();
            if(body!=null){
                MediaType mediaType=body.contentType();
                if(mediaType.getType()!=null){
                    http.setRequestProperty("Content-Type",mediaType.getType());
                }
                if(body.contentLength()!=-1){
                    http.setRequestProperty("Content-Length",String.valueOf(body.contentLength()));
                }
                HttpIoSink ioSink=new HttpIoSink(http.getOutputStream());
                body.writeTo(ioSink);
            }
        }

        int responseCode=http.getResponseCode();
        //是否需要重定向
        while (isNeedRedirects(responseCode)){
            http=redirectsConnect(http,httpRequest);
            if(responseCode==307){
                http.setRequestMethod("GET");
            }
            http.connect();
            listenerDisposer.onConnecting(this);

            if(http.getRequestMethod().equals("POST")){
                RequestBody body=httpRequest.getRequestBody();
                if(body!=null){
                    MediaType mediaType=body.contentType();
                    if(mediaType.getType()!=null){
                        http.setRequestProperty("Content-Type",mediaType.getType());
                    }
                    if(body.contentLength()!=-1){
                        http.setRequestProperty("Content-Length",String.valueOf(body.contentLength()));
                    }
                    HttpIoSink ioSink=new HttpIoSink(http.getOutputStream());
                    body.writeTo(ioSink);
                }
            }
            responseCode=http.getResponseCode();
        }
        //请求头
        Headers headers=getHeaders(http);

        if(isSuccess(responseCode)){
            String stream=readStringStream(http.getInputStream(), XHttpUtils.getInputCharset(http));
            XHttpUtils.disconnectHttp(http);
            listenerDisposer.onResponse(this,Response.builderSuccess(stream,responseCode,headers));
        } else{
            String error=readStringStream(http.getErrorStream(), XHttpUtils.getInputCharset(http));
            XHttpUtils.disconnectHttp(http);
            listenerDisposer.onResponse(this,Response.builderFailure(responseCode,headers,error));
            retryToRun();
        }
    }

    @Override
    protected void onRetry(){
        listenerDisposer.onRetry(this);
    }

    @Override
    protected void onError(Exception e){
        listenerDisposer.onError(this,e);
    }

    @Override
    protected void onCancel(){
        listenerDisposer.onCancel(this);
    }

    @Override
    public HttpConnect request(){
        return httpRequest;
    }

    @Override
    public String tag(){
        return httpRequest.getTag();
    }

    @Override
    public String url(){
        return httpRequest.getConnectUrl();
    }

    @Override
    public boolean cancel(){
        isCancel=true;
        if(taskFuture!=null){
            return taskFuture.cancel(true);
        }
        return false;
    }

    @Override
    public int retryCount(){
        return autoRetryRecorder.getRetryCount();
    }
}
