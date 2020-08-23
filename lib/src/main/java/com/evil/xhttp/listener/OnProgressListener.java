package com.evil.xhttp.listener;

import com.evil.xhttp.base.IDownloadRequest;

public interface OnProgressListener{
    void onProgress(IDownloadRequest request, float progress);
}
