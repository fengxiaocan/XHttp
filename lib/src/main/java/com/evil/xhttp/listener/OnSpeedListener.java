package com.evil.xhttp.listener;

import com.evil.xhttp.base.IDownloadRequest;

public interface OnSpeedListener{
    void onSpeed(IDownloadRequest request, int speed, int time);
}
