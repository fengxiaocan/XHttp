package com.evil.xhttp.config;

public interface IConfig{
    IConfig cacheDir(String cacheDir);

    IConfig sameTimeHttpRequestMaxCount(int sameTimeHttpRequestMaxCount);

    IConfig sameTimeDownloadMaxCount(int sameTimeDownloadMaxCount);

    IConfig userAgent(String userAgent);

    IConfig bufferedSize(int buffSize);

    IConfig isUseBreakpointResume(boolean isUseBreakpointResume);

    IConfig ignoredProgress(boolean ignoredProgress);

    IConfig ignoredSpeed(boolean ignoredSpeed);

    IConfig isUseAutoRetry(boolean isUseAutoRetry);

    IConfig autoRetryTimes(int autoRetryTimes);

    IConfig autoRetryInterval(int autoRetryInterval);

    IConfig updateProgressTimes(int updateProgressTimes);

    IConfig updateSpeedTimes(int updateSpeedTimes);

    IConfig permitAllSslCertificate(boolean permit);

    IConfig connectTimeOut(int connectTimeOut);

    IConfig iOTimeOut(int iOTimeOut);

    IConfig defaultName(@DefaultName int defaultName);

    @interface DefaultName{
        int MD5=0;//MD5值
        int TIME=1;//时间
        int ORIGINAL=2;//原名称
    }
}
