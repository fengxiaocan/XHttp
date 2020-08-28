package com.app.xhttp.listener;

import com.app.xhttp.base.IRequest;

public interface OnConnectListener{
    void onPending(IRequest request);

    void onStart(IRequest request);

    void onConnecting(IRequest request);

    void onCancel(IRequest request);

    void onRetry(IRequest request);
}
