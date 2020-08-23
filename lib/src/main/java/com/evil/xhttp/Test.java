package com.evil.xhttp;

import com.evil.xhttp.base.IDownloadRequest;
import com.evil.xhttp.listener.OnDownloadListener;

public class Test {
    public static void main(String[] args) {
        XHttp.download("https://hbimg.huabanimg.com/ec599fe41eea36a66795f2e1d3ca219a21b5a3136ce70-xj45aF_fw658/format/webp").setUseAutoRetry(false)
                .setDownloadListener(new OnDownloadListener() {
                    @Override
                    public void onComplete(IDownloadRequest request) {
                        System.out.println("下载完成");
                    }

                    @Override
                    public void onFailure(IDownloadRequest request) {
                        System.out.println("下载失败");
                    }
                }).start();
    }
}
