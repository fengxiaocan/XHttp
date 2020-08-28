package com.app.xhttp.core;

import com.app.xhttp.XHttp;
import com.app.xhttp.data.Headers;
import com.app.xhttp.data.Params;
import com.app.xhttp.dispatch.Schedulers;
import com.app.xhttp.listener.SSLCertificateFactory;
import com.app.xhttp.tool.XHttpUtils;

abstract class BaseRequest implements IConnect{
    protected String tag;//标记

    protected String certificatePath;//https 证书地址
    protected SSLCertificateFactory sslCertificateFactory;//https 证书创建器
    protected String baseUrl;//下载地址
    protected Headers headers;//头部信息
    protected Params params;//参数
    protected Schedulers schedulers;//调度器
    protected String userAgent= XHttp.get().config().getUserAgent();//默认UA

    protected boolean permitAllSslCertificate= XHttp.get().config().isPermitAllSslCertificate();//是否仅在WiFi情况下下载
    protected boolean isUseAutoRetry= XHttp.get().config().isUseAutoRetry();//是否使用出错自动重试
    protected int autoRetryTimes= XHttp.get().config().getAutoRetryTimes();//自动重试次数
    protected int autoRetryInterval= XHttp.get().config().getAutoRetryInterval();//自动重试间隔
    protected int connectTimeOut= XHttp.get().config().getConnectTimeOut();//连接超时
    protected int iOTimeOut= XHttp.get().config().getiOTimeOut();//连接超时

    protected volatile String connectUrl;//下载地址
    protected volatile String identifier;//下载标志

    protected BaseRequest(String baseUrl){
        this.baseUrl=baseUrl;
    }

    @Override
    public IConnect setTag(String tag){
        this.tag=tag;
        return this;
    }

    @Override
    public IConnect setSSLCertificate(String path){
        this.certificatePath=path;
        return this;
    }

    @Override
    public IConnect setSSLCertificateFactory(SSLCertificateFactory factory){
        this.sslCertificateFactory=factory;
        return this;
    }

    @Override
    public IConnect addParams(String name,String value){
        if(params==null){
            params=new Params();
        }
        params.addParams(name,value);
        this.connectUrl=null;
        this.identifier=null;
        return this;
    }

    @Override
    public IConnect addHeader(String name,String value){
        if(headers==null){
            headers=new Headers();
        }
        headers.addHeader(name,value);
        return this;
    }

    @Override
    public IConnect setParams(Params params){
        this.params=params;
        return this;
    }

    @Override
    public IConnect setHeader(Headers header){
        this.headers=header;
        return this;
    }

    @Override
    public IConnect setUserAgent(String userAgent){
        this.userAgent=userAgent;
        return this;
    }

    @Override
    public IConnect setConnectTimeOut(int connectTimeOut){
        this.connectTimeOut=connectTimeOut;
        return this;
    }

    @Override
    public IConnect setIOTimeOut(int iOTimeOut){
        this.iOTimeOut=iOTimeOut;
        return this;
    }

    @Override
    public IConnect setUseAutoRetry(boolean useAutoRetry){
        this.isUseAutoRetry=useAutoRetry;
        return this;
    }

    @Override
    public IConnect setAutoRetryTimes(int autoRetryTimes){
        this.autoRetryTimes=autoRetryTimes;
        return this;
    }

    @Override
    public IConnect setAutoRetryInterval(int autoRetryInterval){
        this.autoRetryInterval=autoRetryInterval;
        return this;
    }

    @Override
    public IConnect permitAllSslCertificate(boolean permitAllSslCertificate){
        this.permitAllSslCertificate=permitAllSslCertificate;
        return this;
    }

    @Override
    public IConnect scheduleOn(Schedulers schedulers){
        this.schedulers=schedulers;
        return this;
    }

    public Schedulers getSchedulers(){
        return schedulers;
    }

    public String getConnectUrl(){
        if(connectUrl!=null){
            return connectUrl;
        }
        identifier=null;
        if(params==null||params.size()==0){
            return baseUrl;
        }
        StringBuilder builder=new StringBuilder(baseUrl);
        if(baseUrl.indexOf("?")>0){
            builder.append("&");
            params.toString(builder);
        } else{
            builder.append("?");
            params.toString(builder);
        }
        return connectUrl=builder.toString();
    }

    public String getIdentifier(){
        if(identifier!=null){
            return identifier;
        }
        final String rUrl=getConnectUrl();
        return identifier= XHttpUtils.getMd5(rUrl);
    }


    public String getTag(){
        if(tag==null){
            tag=getIdentifier();
        }
        return tag;
    }

    public String getUserAgent(){
        return userAgent;
    }

    public boolean isPermitAllSslCertificate(){
        return permitAllSslCertificate;
    }

    public boolean isUseAutoRetry(){
        return isUseAutoRetry;
    }

    public int getAutoRetryTimes(){
        return autoRetryTimes;
    }

    public int getAutoRetryInterval(){
        return autoRetryInterval;
    }

    public int getConnectTimeOut(){
        return connectTimeOut;
    }

    public int getIOTimeOut(){
        return iOTimeOut;
    }

    public String getCertificatePath(){
        return certificatePath;
    }

    public String getBaseUrl(){
        return baseUrl;
    }

    public Headers getHeaders(){
        return headers;
    }

    public Params getParams(){
        return params;
    }
}
