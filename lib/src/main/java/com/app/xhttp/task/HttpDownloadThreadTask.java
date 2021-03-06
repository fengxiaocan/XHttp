package com.app.xhttp.task;


import com.app.xhttp.XHttp;
import com.app.xhttp.base.IConnectRequest;
import com.app.xhttp.base.IDownloadRequest;
import com.app.xhttp.core.XDownloadRequest;
import com.app.xhttp.impl.DownloadListenerDisposer;
import com.app.xhttp.impl.ProgressDisposer;
import com.app.xhttp.impl.SpeedDisposer;
import com.app.xhttp.made.AutoRetryRecorder;
import com.app.xhttp.tool.MimeType;
import com.app.xhttp.tool.XHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Future;

final class HttpDownloadThreadTask extends HttpDownloadRequest implements IDownloadRequest, IConnectRequest {
    private volatile XDownloadRequest request;
    private final DownloadListenerDisposer listenerDisposer;
    private final ProgressDisposer progressDisposer;
    private final SpeedDisposer speedDisposer;
    private final File cacheFile;

    private volatile long sContentLength;
    private volatile long sSofarLength = 0;
    private volatile Future taskFuture;
    private volatile int speedLength = 0;
    private volatile String suffix;

    public HttpDownloadThreadTask(XDownloadRequest request, DownloadListenerDisposer listener, long contentLength) {
        super(new AutoRetryRecorder(request.isUseAutoRetry(),
                request.getAutoRetryTimes(),
                request.getAutoRetryInterval()), request.getBufferedSize());
        this.request = request;
        this.sContentLength = contentLength;
        this.listenerDisposer = listener;
        this.progressDisposer = new ProgressDisposer(request.isIgnoredProgress(),
                request.getUpdateProgressTimes(),
                listener);
        this.speedDisposer = new SpeedDisposer(request.isIgnoredSpeed(), request.getUpdateSpeedTimes(), listener);
        this.cacheFile = XHttpUtils.getTempFile(request);
        listenerDisposer.onPending(this);
    }

    public final void setTaskFuture(Future taskFuture) {
        this.taskFuture = taskFuture;
    }

    /**
     * 检测是否已经下载完成
     *
     * @return
     */
    public boolean checkComplete() {
        File file = request.getSaveFile();
        if (sContentLength > 0 && file != null && file.exists()) {
            if (file.length() == sContentLength) {
                listenerDisposer.onComplete(this);
                return true;
            } else {
                file.delete();
            }
        }
        return false;
    }

    @Override
    public void run() {
        listenerDisposer.onStart(this);
        super.run();
        XHttp.get().removeDownload(request.getTag());
    }

    @Override
    protected void onConnecting(long length) {
        sContentLength = length;
        listenerDisposer.onConnecting(this);
    }

    @Override
    protected void httpRequest() throws Exception {
        if (checkComplete()) {
            return;
        }
        HttpURLConnection http = null;

        if (sContentLength <= 0 || suffix == null) {
            //重新获取下载长度
            http = getDownloaderLong(request);
            //获取文件类型后缀
            suffix = MimeType.getType(http.getHeaderField("Content-Type"));

            final int code = http.getResponseCode();
            if (!isSuccess(code)) {
                //获取错误信息
                String stream = readStringStream(http.getErrorStream(), XHttpUtils.getInputCharset(http));
                listenerDisposer.onRequestError(this, code, stream);
                //断开请求
                XHttpUtils.disconnectHttp(http);
                //重试
                retryToRun();
                return;
            }
        }

        final boolean isBreakPointResume;//是否断点续传

        //判断之前下载的文件是否存在或完成
        if (cacheFile.exists()) {
            if (sContentLength > 0) {
                if (cacheFile.length() == sContentLength) {
                    //长度一致
                    sSofarLength = sContentLength;
                    //复制临时文件到保存文件中
                    copyFile(cacheFile, getFile(), true);
                    //下载完成
                    speedLength = 0;
                    listenerDisposer.onComplete(this);
                    return;
                } else if (cacheFile.length() > sContentLength) {
                    //长度大了
                    cacheFile.delete();
                    sSofarLength = 0;
                    isBreakPointResume = false;
                } else {
                    sSofarLength = cacheFile.length();
                    isBreakPointResume = request.isUseBreakpointResume();
                }
            } else {
                //获取不到文件长度
                cacheFile.delete();
                sSofarLength = 0;
                isBreakPointResume = false;
            }
        } else {
            cacheFile.getParentFile().mkdirs();
            sSofarLength = 0;
            isBreakPointResume = false;
        }

        if (isBreakPointResume) {
            //断点下载
            if (sSofarLength > 0) {
                XHttpUtils.disconnectHttp(http);
                http = null;
            }
            if (http == null) {
                http = request.buildConnect();
                final long start = sSofarLength;
                http.setRequestProperty("Range", XHttpUtils.jsonString("bytes=",
                        start, "-", sContentLength));
                //重新连接
                http.connect();
            }
        } else {
            if (http == null) {
                http = request.buildConnect();
                http.connect();
            }
        }

        int responseCode = http.getResponseCode();

        //判断是否需要重定向
        while (isNeedRedirects(responseCode)) {
            http = redirectsConnect(http, request);
            responseCode = http.getResponseCode();
        }

        //重新判断
        if (!isSuccess(responseCode)) {
            onResponseError(http, responseCode);
            return;
        }

        //重新下载
        if (!downReadInput(http, isBreakPointResume)) {
            return;
        }
        //复制下载完成的文件
        copyFile(cacheFile, getFile(), true);

        //处理最后的进度
        if (!progressDisposer.isIgnoredProgress()) {
            listenerDisposer.onProgress(this, 1);
        }
        //处理最后的速度
        if (!speedDisposer.isIgnoredSpeed()) {
            speedDisposer.onSpeed(this, speedLength);
        }
        speedLength = 0;
        //完成回调
        listenerDisposer.onComplete(this);
    }

    private boolean downReadInput(HttpURLConnection http, boolean append) throws IOException {
        try {
            FileOutputStream os = new FileOutputStream(cacheFile, append);
            return readInputStream(http.getInputStream(), os);
        } finally {
            XHttpUtils.disconnectHttp(http);
        }
    }

    /**
     * 处理失败的回调
     *
     * @param http
     * @param responseCode
     * @throws IOException
     */
    private void onResponseError(HttpURLConnection http, int responseCode) throws IOException {
        String stream = readStringStream(http.getErrorStream(), XHttpUtils.getInputCharset(http));
        listenerDisposer.onRequestError(this, responseCode, stream);

        XHttpUtils.disconnectHttp(http);
        retryToRun();
    }

    @Override
    protected void onRetry() {
        listenerDisposer.onRetry(this);
    }

    @Override
    protected void onError(Exception e) {
        listenerDisposer.onFailure(this);
    }

    @Override
    protected void onCancel() {
        listenerDisposer.onCancel(this);
    }

    @Override
    protected void onProgress(int length) {
        sSofarLength += length;
        speedLength += length;
        if (progressDisposer.isCallProgress()) {
            progressDisposer.onProgress(this, getTotalLength(), getSofarLength());
        }
        if (speedDisposer.isCallSpeed()) {
            speedDisposer.onSpeed(this, speedLength);
            speedLength = 0;
        }
    }

    private File getFile() {
        File saveFile = request.getSaveFile();
        if (saveFile != null) {
            return saveFile;
        }
        if (suffix != null) {
            return new File(request.getSaveDir(), request.getSaveName() + suffix);
        }
        return new File(request.getSaveDir(), request.getSaveName());
    }

    @Override
    public String getFilePath() {
        return getFile().getAbsolutePath();
    }

    @Override
    public long getTotalLength() {
        return sContentLength;
    }

    @Override
    public long getSofarLength() {
        return sSofarLength;
    }

    @Override
    public String tag() {
        return request.getIdentifier();
    }

    @Override
    public String url() {
        return request.getConnectUrl();
    }

    @Override
    public boolean cancel() {
        isCancel = true;
        if (taskFuture != null) {
            return taskFuture.cancel(true);
        }
        return false;
    }

    @Override
    public int retryCount() {
        return autoRetryRecorder.getRetryCount();
    }

    @Override
    public XDownloadRequest request() {
        return request;
    }
}
