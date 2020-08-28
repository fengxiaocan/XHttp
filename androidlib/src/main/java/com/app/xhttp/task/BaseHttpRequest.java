package com.app.xhttp.task;

import com.app.xhttp.core.BuilderURLConnection;
import com.app.xhttp.data.Headers;
import com.app.xhttp.made.AutoRetryRecorder;
import com.app.xhttp.tool.XHttpUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

abstract class BaseHttpRequest implements Runnable{
    protected final AutoRetryRecorder autoRetryRecorder;
    protected volatile boolean isCancel=false;

    public BaseHttpRequest(AutoRetryRecorder autoRetryRecorder){
        this.autoRetryRecorder=autoRetryRecorder;
    }

    protected final void runTask(){
        try{
            if(isCancel){
                onCancel();
                return;
            }
            httpRequest();
        } catch(Exception e){
            e.printStackTrace();
            if(isCancel){
                onCancel();
            } else{
                if(autoRetryRecorder.isCanRetry()){
                    //回调重试
                    onRetry();
                    //决定是否延迟执行重试
                    autoRetryRecorder.sleep();
                    //自动重试下载
                    runTask();
                } else{
                    onError(e);
                }
            }
        }
    }

    /**
     * 重定向地址
     *
     * @param connection
     * @param request
     * @return
     * @throws Exception
     */
    protected HttpURLConnection redirectsConnect(HttpURLConnection connection,BuilderURLConnection request)
            throws Exception
    {
        if(connection!=null){
            //获取指向
            String location=connection.getHeaderField("Location");
            //获取cookie
            String redirectsCookie=connection.getHeaderField("Set-Cookie");
            //获取重定向地址
            String redirectsUrl=getRedirectsUrl(connection.getURL(),location);
            //断开原来的连接
            XHttpUtils.disconnectHttp(connection);
            HttpURLConnection http=request.buildConnect(redirectsUrl);
            if(redirectsCookie!=null){
                http.setRequestProperty("Cookie",redirectsCookie);
            }
            return http;
        }
        return connection;
    }

    /**
     * 获取重定向的真实地址
     *
     * @param url
     * @param location
     * @return
     */
    protected final String getRedirectsUrl(URL url,String location){
        String redirectsUrl;
        if(location.startsWith("http")){
            redirectsUrl=location;
        } else{
            StringBuilder builder=new StringBuilder(url.getProtocol());
            builder.append("://");
            builder.append(url.getHost());

            if(location.startsWith("/")){
                builder.append(location);

                String query=url.getQuery();
                if(query!=null){
                    if(location.indexOf("?")>0){
                        builder.append("&");
                    } else{
                        builder.append("?");
                    }
                    builder.append(query);
                }
            } else{
                String urlPath=url.getPath();

                if(urlPath!=null){
                    int index=urlPath.lastIndexOf("/");
                    if(index>0){
                        String substring=urlPath.substring(0,index+1);
                        builder.append(substring);
                    } else{
                        builder.append(urlPath);
                    }
                    builder.append(location);
                }

                String query=url.getQuery();
                if(query!=null){
                    if(location.indexOf("?")>0){
                        builder.append("&");
                    } else{
                        builder.append("?");
                    }
                    builder.append(query);
                }
            }
            redirectsUrl=builder.toString();
        }
        return redirectsUrl;
    }

    protected boolean isNeedRedirects(int code){
        switch(code){
            case 301:
                //客户请求的文档在其他地方，新的URL在Location头中给出，浏览器应该自动地访问新的URL。
            case 302:
                //类似于301，但新的URL应该被视为临时性的替代，而不是永久性的。注意，在HTTP1.0中对应的状态信息是“Moved Temporatily”。
                //出现该状态代码时，浏览器能够自动访问新的URL，因此它是一个很有用的状态代码。
            case 303:
                //类似于301/302，不同之处在于，如果原来的请求是POST，Location头指定的重定向目标文档应该通过GET提取（HTTP 1.1新）。
            case 305:
                //客户请求的文档应该通过Location头所指明的代理服务器提取（HTTP 1.1新）。
            case 307:
                //和 302（Found）相同。许多浏览器会错误地响应302应答进行重定向，即使原来的请求是POST，即使它实际上只能在POST请求的应答是303时才 能重定向。由于这个原因，HTTP 1.1新增了307，以便更加清除地区分几个状态代码：当出现303应答时，浏览器可以跟随重定向的GET和POST请求；如果是307应答，则浏览器只 能跟随对GET请求的重定向。（HTTP 1.1新）
                return true;
            default:
                return false;
        }
    }

    protected final void retryToRun(){
        if(isCancel){
            onCancel();
        } else{
            if(autoRetryRecorder.isCanRetry()){
                //回调重试
                onRetry();
                //决定是否延迟执行重试
                autoRetryRecorder.sleep();
                //自动重试下载
                runTask();
            }
        }
    }

    protected abstract void httpRequest() throws Exception;

    protected abstract void onRetry();

    protected abstract void onError(Exception e);

    protected abstract void onCancel();

    @Override
    public void run(){
        runTask();
    }

    protected boolean isSuccess(int responseCode){
        return responseCode >= 200&&responseCode<400;
    }


    protected Headers getHeaders(HttpURLConnection http){
        Headers headers=new Headers();
        Map<String,List<String>> map=http.getHeaderFields();
        for(String key: map.keySet()){
            headers.addHeaders(key,map.get(key));
        }
        return headers;
    }

    protected String readStringStream(InputStream is,String charset) throws IOException{
        BufferedReader reader=null;
        try{
            reader=new BufferedReader(new InputStreamReader(is,charset));

            StringBuilder builder=new StringBuilder();
            char[] temp=new char[1024*8];
            int length;
            while((length=reader.read(temp))>0){
                builder.append(temp,0,length);
            }
            return builder.toString();
        } finally{
            XHttpUtils.closeIo(is);
            XHttpUtils.closeIo(reader);
        }
    }
}
