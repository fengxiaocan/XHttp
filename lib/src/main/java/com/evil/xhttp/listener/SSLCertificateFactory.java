package com.evil.xhttp.listener;

import javax.net.ssl.SSLSocketFactory;

public interface SSLCertificateFactory{
    SSLSocketFactory createCertificate();
}
