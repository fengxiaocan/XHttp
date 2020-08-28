package com.app.xhttp.listener;

import com.app.xhttp.base.IDownloadRequest;

public interface OnProgressListener{
    void onProgress(IDownloadRequest request, float progress);
}
