package com.app.xhttp.impl;

import com.app.xhttp.base.IDownloadRequest;

public final class SpeedDisposer{
    private volatile long lastTime;
    private final boolean ignoredSpeed;
    private final long updateSpeedTimes;
    private final DownloadListenerDisposer disposer;

    public SpeedDisposer(
            boolean ignoredSpeed,long updateSpeedTimes,DownloadListenerDisposer listener)
    {
        this.ignoredSpeed=ignoredSpeed;
        this.updateSpeedTimes=updateSpeedTimes;
        this.disposer=listener;
    }

    public boolean isIgnoredSpeed(){
        return ignoredSpeed;
    }

    public boolean isCallSpeed(){
        if(ignoredSpeed||updateSpeedTimes<=0){
            return false;
        }
        synchronized(Object.class){
            if(lastTime == 0){
                lastTime=System.currentTimeMillis();
                return false;
            }
            final long l=System.currentTimeMillis()-lastTime;
            return l >= updateSpeedTimes;
        }
    }

    public void onSpeed(IDownloadRequest request,final int speed){
        disposer.onSpeed(request,speed,(int)(System.currentTimeMillis()-lastTime));
        lastTime=System.currentTimeMillis();
    }
}
