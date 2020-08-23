package com.evil.xhttp.core;

import java.net.HttpURLConnection;

public interface BuilderURLConnection{
    HttpURLConnection buildConnect(String connectUrl) throws Exception;

    HttpURLConnection buildConnect() throws Exception;
}
