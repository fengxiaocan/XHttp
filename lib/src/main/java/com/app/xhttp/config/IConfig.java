package com.app.xhttp.config;

public interface IConfig{
    IConfig cacheDir(String cacheDir);

    IConfig saveDir(String dir);

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

    IConfig defaultNameIsTime(boolean isTime);

}
