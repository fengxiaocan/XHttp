package com.app.xhttp.listener;

import com.app.xhttp.base.IRequest;
import com.app.xhttp.data.Response;

public interface OnResponseListener{
    void onResponse(IRequest request, Response response);

    void onError(IRequest request, Exception exception);
}
