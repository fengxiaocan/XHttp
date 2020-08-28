package com.app.xhttp.task;

import com.app.xhttp.core.XDownloadRequest;
import com.app.xhttp.made.AutoRetryRecorder;
import com.app.xhttp.tool.XHttpUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

abstract class HttpDownloadRequest extends BaseHttpRequest {

    protected final int byteArraySize;

    public HttpDownloadRequest(AutoRetryRecorder autoRetryRecorder, int byteSize) {
        super(autoRetryRecorder);
        byteArraySize = Math.max(2048, byteSize);
    }

    //进度回调
    protected void onProgress(int length) {
    }

    //连接中
    protected void onConnecting(long length) {
    }

    //获取文件长度
    protected HttpURLConnection getDownloaderLong(XDownloadRequest request) throws Exception {
        HttpURLConnection http = request.buildConnect();
        int responseCode = http.getResponseCode();

        while (isNeedRedirects(responseCode)) {
            http = redirectsConnect(http, request);
            responseCode = http.getResponseCode();
        }
        //优先获取文件长度再回调
        long contentLength = XHttpUtils.getContentLength(http);
        //连接中
        onConnecting(contentLength);

        if (contentLength <= 0) {
            //长度获取不到的时候重新连接 获取不到长度则要求http请求不要gzip压缩
            XHttpUtils.disconnectHttp(http);
            http = request.buildConnect();
            http.setRequestProperty("Accept-Encoding", "identity");
            http.connect();

            contentLength = XHttpUtils.getContentLength(http);
            //连接中
            onConnecting(contentLength);
        }
        return http;
    }

    protected final boolean readInputStream(InputStream is, OutputStream os) throws IOException {
        try {
            byte[] bytes = new byte[byteArraySize];
            int length;
            while ((length = is.read(bytes)) > 0) {
                if (isCancel) {
                    onCancel();
                    return false;
                }
                os.write(bytes, 0, length);
                os.flush();
                onProgress(length);
            }
            return true;
        } finally {
            XHttpUtils.closeIo(is);
            XHttpUtils.closeIo(os);
        }
    }


    protected final boolean copyFile(File inputFile, File outputFile, boolean isDelect) throws IOException {
        if (inputFile == null || outputFile == null) {
            return false;
        }
        //输入文件不存在
        if (!inputFile.exists()) {
            return false;
        }
        //输入文件跟输出文件一致
        if (inputFile == outputFile) {
            return true;
        }
        //输入文件跟输出文件一致
        if (inputFile.getAbsolutePath().equals(outputFile.getAbsolutePath())) {
            return true;
        }
        //输出文件存在并跟输入文件一致
        if (outputFile.exists() && outputFile.length() == inputFile.length()) {
            if (isDelect) {
                inputFile.delete();
            }
            return true;
        } else {
            outputFile.getParentFile().mkdirs();

            FileOutputStream output = null;
            FileInputStream input = null;
            try {
                input = new FileInputStream(inputFile);
                output = new FileOutputStream(outputFile);

                byte[] bytes = new byte[1024 * 8];
                int length;
                while ((length = input.read(bytes)) > 0) {
                    output.write(bytes, 0, length);
                    output.flush();
                }
                if (isDelect) {
                    inputFile.delete();
                }
                return true;
            } finally {
                XHttpUtils.closeIo(input);
                XHttpUtils.closeIo(output);
            }
        }
    }

}
