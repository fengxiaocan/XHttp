package com.app.xhttp.listener;

import com.app.xhttp.base.IDownloadRequest;

public interface OnDownloadListener{
    void onComplete(IDownloadRequest request);

    void onFailure(IDownloadRequest request);
}
