package com.evil.xhttp.listener;

import com.evil.xhttp.base.IRequest;
import com.evil.xhttp.data.Response;

public interface OnResponseListener{
    void onResponse(IRequest request, Response response);

    void onError(IRequest request, Exception exception);
}
