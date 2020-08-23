package com.evil.xhttp.listener;

import com.evil.xhttp.base.IDownloadRequest;

public interface OnDownloadListener{
    void onComplete(IDownloadRequest request);

    void onFailure(IDownloadRequest request);
}
