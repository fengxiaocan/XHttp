package com.app.xhttp.core;

import com.app.xhttp.base.RequestBody;
import com.app.xhttp.data.Headers;
import com.app.xhttp.data.Params;
import com.app.xhttp.dispatch.Schedulers;
import com.app.xhttp.listener.OnConnectListener;
import com.app.xhttp.listener.OnResponseListener;
import com.app.xhttp.listener.SSLCertificateFactory;

public interface HttpConnect extends IConnect{

    @Override
    HttpConnect setTag(String tag);

    @Override
    HttpConnect setSSLCertificate(String path);

    @Override
    HttpConnect setSSLCertificateFactory(SSLCertificateFactory factory);

    @Override
    HttpConnect addParams(String name, String value);

    @Override
    HttpConnect addHeader(String name, String value);

    @Override
    HttpConnect setParams(Params params);

    @Override
    HttpConnect setHeader(Headers header);

    @Override
    HttpConnect setUserAgent(String userAgent);

    @Override
    HttpConnect setConnectTimeOut(int connectTimeOut);

    @Override
    HttpConnect setIOTimeOut(int iOTimeOut);

    @Override
    HttpConnect setUseAutoRetry(boolean useAutoRetry);

    @Override
    HttpConnect setAutoRetryTimes(int autoRetryTimes);

    @Override
    HttpConnect setAutoRetryInterval(int autoRetryInterval);

    @Override
    HttpConnect permitAllSslCertificate(boolean wifiRequired);

    @Override
    HttpConnect scheduleOn(Schedulers schedulers);

    HttpConnect setUseCaches(boolean useCaches);

    HttpConnect setRequestMothod(Mothod mothod);

    HttpConnect setOnResponseListener(OnResponseListener listener);

    HttpConnect setOnConnectListener(OnConnectListener listener);

    HttpConnect post();

    HttpConnect requestBody(RequestBody body);

    enum Mothod{
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        HEAD("HEAD"),
        DELETE("DELETE");

        private String mothod;

        Mothod(String mothod){
            this.mothod=mothod;
        }

        public String getMothod(){
            return mothod;
        }
    }

    String start();
}
