package com.app.xhttp.impl;

import com.app.xhttp.base.IDownloadRequest;
import com.app.xhttp.dispatch.Schedulers;
import com.app.xhttp.listener.OnDownloadConnectListener;
import com.app.xhttp.listener.OnDownloadListener;
import com.app.xhttp.listener.OnProgressListener;
import com.app.xhttp.listener.OnSpeedListener;

public class DownloadListenerDisposer
        implements OnDownloadConnectListener, OnDownloadListener, OnProgressListener, OnSpeedListener{
    private final Schedulers schedulers;
    private final OnDownloadListener onDownloadListener;
    private final OnProgressListener onProgressListener;
    private final OnSpeedListener onSpeedListener;
    private final OnDownloadConnectListener onConnectListener;

    public DownloadListenerDisposer(
            Schedulers schedulers,
            OnDownloadConnectListener onConnectListener,
            OnDownloadListener onDownloadListener,
            OnProgressListener onProgressListener,
            OnSpeedListener onSpeedListener)
    {
        this.schedulers=schedulers;
        this.onConnectListener=onConnectListener;
        this.onDownloadListener=onDownloadListener;
        this.onProgressListener=onProgressListener;
        this.onSpeedListener=onSpeedListener;
    }

    @Override
    public void onPending(final IDownloadRequest request){
        if(onConnectListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onConnectListener.onPending(request);
                }
            });
        } else{
            onConnectListener.onPending(request);
        }
    }

    @Override
    public void onStart(final IDownloadRequest request){
        if(onConnectListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onConnectListener.onStart(request);
                }
            });
        } else{
            onConnectListener.onStart(request);
        }
    }

    @Override
    public void onConnecting(final IDownloadRequest request){
        if(onConnectListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onConnectListener.onConnecting(request);
                }
            });
        } else{
            onConnectListener.onConnecting(request);
        }
    }

    @Override
    public void onRequestError(final IDownloadRequest request,final int code,final String error){
        if(onConnectListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onConnectListener.onRequestError(request,code,error);
                }
            });
        } else{
            onConnectListener.onRequestError(request,code,error);
        }
    }

    @Override
    public void onCancel(final IDownloadRequest request){
        if(onConnectListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onConnectListener.onCancel(request);
                }
            });
        } else{
            onConnectListener.onCancel(request);
        }
    }

    @Override
    public void onRetry(final IDownloadRequest request){
        if(onConnectListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onConnectListener.onRetry(request);
                }
            });
        } else{
            onConnectListener.onRetry(request);
        }
    }

    @Override
    public void onComplete(final IDownloadRequest request){
        if(onDownloadListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onDownloadListener.onComplete(request);
                }
            });
        } else{
            onDownloadListener.onComplete(request);
        }
    }

    @Override
    public void onFailure(final IDownloadRequest request){
        if(onDownloadListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onDownloadListener.onFailure(request);
                }
            });
        } else{
            onDownloadListener.onFailure(request);
        }
    }

    @Override
    public void onProgress(final IDownloadRequest request,final float progress){
        if(onProgressListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onProgressListener.onProgress(request,progress);
                }
            });
        } else{
            onProgressListener.onProgress(request,progress);
        }
    }

    @Override
    public void onSpeed(final IDownloadRequest request,final int speed,final int time){
        if(onSpeedListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onSpeedListener.onSpeed(request,speed,time);
                }
            });
        } else{
            onSpeedListener.onSpeed(request,speed,time);
        }
    }
}
