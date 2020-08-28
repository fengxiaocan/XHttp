package com.app.xhttp.listener;

import com.app.xhttp.base.IDownloadRequest;

public interface OnSpeedListener{
    void onSpeed(IDownloadRequest request, int speed, int time);
}
