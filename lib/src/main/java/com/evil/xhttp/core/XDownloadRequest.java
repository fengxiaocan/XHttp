package com.evil.xhttp.core;

import com.evil.xhttp.XHttp;
import com.evil.xhttp.data.Headers;
import com.evil.xhttp.data.Params;
import com.evil.xhttp.dispatch.Schedulers;
import com.evil.xhttp.listener.OnDownloadConnectListener;
import com.evil.xhttp.listener.OnDownloadListener;
import com.evil.xhttp.listener.OnProgressListener;
import com.evil.xhttp.listener.OnSpeedListener;
import com.evil.xhttp.listener.SSLCertificateFactory;
import com.evil.xhttp.task.ThreadTaskFactory;
import com.evil.xhttp.tool.XHttpUtils;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class XDownloadRequest extends BaseRequest implements HttpDownload, BuilderURLConnection {
    //文件保存位置
    protected String saveFile;//文件保存位置
    //文件缓存目录
    protected String cacheDir = XHttp.get().config().getCacheDir();
    //写文件buff大小，该数值大小不能小于2048，数值变小，下载速度会变慢,默认8kB
    protected int bufferedSize = XHttp.get().config().getBufferedSize();
    //是否使用断点续传
    protected boolean isUseBreakpointResume = XHttp.get().config().isUseBreakpointResume();
    //是否忽略下载的progress回调
    protected boolean ignoredProgress = XHttp.get().config().isIgnoredProgress();
    //是否忽略下载的progress回调
    protected boolean ignoredSpeed = XHttp.get().config().isIgnoredSpeed();
    //更新进度条的间隔
    protected int updateProgressTimes = XHttp.get().config().getUpdateProgressTimes();
    //更新下载速度的间隔
    protected int updateSpeedTimes = XHttp.get().config().getUpdateSpeedTimes();
    //下载完成失败监听
    protected OnDownloadListener onDownloadListener;
    //下载过程连接监听
    protected OnDownloadConnectListener onDownloadConnectListener;
    //下载进度监听
    protected OnProgressListener onProgressListener;
    //下载速度监听
    protected OnSpeedListener onSpeedListener;

    protected XDownloadRequest(String baseUrl) {
        super(baseUrl);
    }

    public static XDownloadRequest with(String url) {
        return new XDownloadRequest(url);
    }

    public String getSaveFile() {
        return saveFile;
    }

    public String getCacheDir() {
        if (XHttpUtils.isStringEmpty(cacheDir)) {
            return XHttp.get().config().getCacheDir();
        }
        return cacheDir;
    }


    @Override
    public HttpDownload setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
        return this;
    }

    @Override
    public HttpDownload setIgnoredProgress(boolean ignoredProgress) {
        this.ignoredProgress = ignoredProgress;
        return this;
    }


    @Override
    public HttpDownload setIgnoredSpeed(boolean ignoredSpeed) {
        this.ignoredSpeed = ignoredSpeed;
        return this;
    }

    @Override
    public HttpDownload setUpdateProgressTimes(int updateProgressTimes) {
        this.updateProgressTimes = updateProgressTimes;
        return this;
    }

    @Override
    public HttpDownload setUpdateSpeedTimes(int updateSpeedTimes) {
        this.updateSpeedTimes = updateSpeedTimes;
        return this;
    }


    @Override
    public HttpDownload setBufferedSize(int bufferedSize) {
        this.bufferedSize = bufferedSize;
        return this;
    }

    @Override
    public HttpDownload setUseBreakpointResume(boolean useBreakpointResume) {
        this.isUseBreakpointResume = useBreakpointResume;
        return this;
    }

    @Override
    public HttpDownload setDownloadListener(OnDownloadListener listener) {
        onDownloadListener = listener;
        return this;
    }

    @Override
    public HttpDownload setConnectListener(OnDownloadConnectListener listener) {
        onDownloadConnectListener = listener;
        return this;
    }

    @Override
    public HttpDownload setOnProgressListener(OnProgressListener listener) {
        onProgressListener = listener;
        return this;
    }

    @Override
    public HttpDownload setOnSpeedListener(OnSpeedListener listener) {
        onSpeedListener = listener;
        return this;
    }

    @Override
    public HttpDownload delect() {
        XHttp.get().cancelDownload(getTag());

        File saveFile = XHttpUtils.getSaveFile(this);
        if (saveFile.exists()) {
            saveFile.delete();
        }
        File tempFile = XHttpUtils.getTempFile(this);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        File tempCacheDir = XHttpUtils.getTempCacheDir(this);
        XHttpUtils.delectDir(tempCacheDir);
        return this;
    }

    @Override
    public HttpDownload setTag(String tag) {
        return (HttpDownload) super.setTag(tag);
    }

    @Override
    public HttpDownload setSSLCertificate(String path) {
        return (HttpDownload) super.setSSLCertificate(path);
    }

    @Override
    public HttpDownload setSSLCertificateFactory(SSLCertificateFactory factory) {
        return (HttpDownload) super.setSSLCertificateFactory(factory);
    }

    @Override
    public HttpDownload setSaveFile(String saveFile) {
        this.saveFile = saveFile;
        return this;
    }

    @Override
    public HttpDownload addParams(String name, String value) {
        return (HttpDownload) super.addParams(name, value);
    }

    @Override
    public HttpDownload addHeader(String name, String value) {
        return (HttpDownload) super.addHeader(name, value);
    }

    @Override
    public HttpDownload setParams(Params params) {
        return (HttpDownload) super.setParams(params);
    }

    @Override
    public HttpDownload setHeader(Headers header) {
        return (HttpDownload) super.setHeader(header);
    }

    @Override
    public HttpDownload setUserAgent(String userAgent) {
        return (HttpDownload) super.setUserAgent(userAgent);
    }

    @Override
    public HttpDownload setConnectTimeOut(int connectTimeOut) {
        return (HttpDownload) super.setConnectTimeOut(connectTimeOut);
    }

    @Override
    public HttpDownload setIOTimeOut(int iOTimeOut) {
        return (HttpDownload) super.setIOTimeOut(iOTimeOut);
    }

    @Override
    public HttpDownload setUseAutoRetry(boolean useAutoRetry) {
        return (HttpDownload) super.setUseAutoRetry(useAutoRetry);
    }

    @Override
    public HttpDownload setAutoRetryTimes(int autoRetryTimes) {
        return (HttpDownload) super.setAutoRetryTimes(autoRetryTimes);
    }

    @Override
    public HttpDownload setAutoRetryInterval(int autoRetryInterval) {
        return (HttpDownload) super.setAutoRetryInterval(autoRetryInterval);
    }

    @Override
    public HttpDownload permitAllSslCertificate(boolean wifiRequired) {
        return (HttpDownload) super.permitAllSslCertificate(wifiRequired);
    }

    @Override
    public HttpDownload scheduleOn(Schedulers schedulers) {
        return (HttpDownload) super.scheduleOn(schedulers);
    }

    public int getBufferedSize() {
        return bufferedSize;
    }

    public boolean isUseBreakpointResume() {
        return isUseBreakpointResume;
    }

    public boolean isIgnoredProgress() {
        return ignoredProgress;
    }

    public boolean isIgnoredSpeed() {
        return ignoredSpeed;
    }

    public int getUpdateProgressTimes() {
        return updateProgressTimes;
    }

    public int getUpdateSpeedTimes() {
        return updateSpeedTimes;
    }

    public OnDownloadListener getOnDownloadListener() {
        return onDownloadListener;
    }

    public OnDownloadConnectListener getOnDownloadConnectListener() {
        return onDownloadConnectListener;
    }

    public OnProgressListener getOnProgressListener() {
        return onProgressListener;
    }

    public OnSpeedListener getOnSpeedListener() {
        return onSpeedListener;
    }

    @Override
    public String start() {
        ThreadTaskFactory.createDownloadTask(this);
        return getTag();
    }

    @Override
    public HttpURLConnection buildConnect(String connectUrl) throws Exception {
        URL url = new URL(connectUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        if (http instanceof HttpsURLConnection) {
            HttpsURLConnection https = (HttpsURLConnection) http;
            //处理https证书
            SSLSocketFactory certificate = null;
            if (sslCertificateFactory != null) {
                certificate = sslCertificateFactory.createCertificate();
            } else if (certificatePath != null) {
                certificate = XHttpUtils.getCertificate(certificatePath);
            } else if (permitAllSslCertificate) {
                //允许所有的https证书
                certificate = XHttpUtils.getUnSafeCertificate();
            }
            if (certificate != null) {
                https.setSSLSocketFactory(certificate);
            }
        }

        http.setRequestMethod("GET");
        //设置http请求头
        http.setRequestProperty("Connection", "Keep-Alive");
        if (headers != null) {
            for (String key : headers.keySet()) {
                http.setRequestProperty(key, headers.getValue(key));
            }
        }
        if (userAgent != null) {
            http.setRequestProperty("User-Agent", userAgent);
        }

        http.setConnectTimeout(Math.max(connectTimeOut, 5 * 1000));
        http.setReadTimeout(Math.max(iOTimeOut, 5 * 1000));

        //本次链接自动处理重定向
        http.setInstanceFollowRedirects(true);

        http.setUseCaches(false);
        http.setDoInput(true);
        return http;
    }

    public HttpURLConnection buildConnect() throws Exception {
        return buildConnect(getConnectUrl());
    }
}
