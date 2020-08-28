package com.app.xhttp.tool;

import com.app.xhttp.core.XDownloadRequest;
import com.app.xhttp.impl.UnSafeTrustManager;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class XHttpUtils {
    private static String getMd5(byte[] btInput) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            //获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //使用指定的字节更新摘要
            mdInst.update(btInput);
            //获得密文
            byte[] md = mdInst.digest();
            //把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMd5(String msg) {
        byte[] bytes = msg.getBytes();
        return getMd5(bytes);
    }

    public static void closeIo(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sleep(long ms) {
        long start = System.currentTimeMillis();
        long duration = ms;
        boolean interrupted = false;
        do {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                interrupted = true;
            }
            duration = start + ms - System.currentTimeMillis();
        } while (duration > 0);

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    public static String getInputCharset(HttpURLConnection connection) {
        if (connection != null) {
            String type = connection.getHeaderField("Content-Type");
            if (type != null) {
                if (type.contains("charset=")) {
                    String substring = type.substring(type.indexOf("charset=") + "charset=".length());
                    int i = substring.indexOf(";");
                    if (i > 0) {
                        substring = substring.substring(0, i);
                    }
                    return substring;
                }
            }
        }
        return "utf-8";
    }

    public static void disconnectHttp(HttpURLConnection connection) {
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static boolean isStringEmpty(String content) {
        return content == null || content.equals("");
    }


    /**
     * 获得临时文件的文件名
     *
     * @return
     */
    public static File getTempFile(XDownloadRequest request) {
        //保存路径
        //没有设置保存文件名
        return new File(request.getCacheDir(), request.getIdentifier() + "_temp");
    }

    public static void deleteDir(File dir) {
        if (dir == null) {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (file1.isDirectory()) {
                    deleteDir(file1);
                }
                file1.delete();
            }
        }
        dir.delete();
    }

    public static long getContentLength(HttpURLConnection http) {
        try {
            return Long.parseLong(http.getHeaderField("Content-Length"));
        } catch (Exception e) {
        }
        return 0;
    }


    /**
     * 获取正则匹配的第一个
     *
     * @param regex
     * @param input
     * @return
     */
    private static String getFirstMatches(String regex, String input) {
        if (input == null) {
            return null;
        }
        String matches = null;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            matches = matcher.group();
        }
        return matches;
    }

    /**
     * 获取安全证书
     *
     * @param cerPath
     * @return
     */
    public static SSLSocketFactory getCertificate(String cerPath) {
        InputStream is = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            is = new FileInputStream(cerPath);
            Certificate ca = cf.generateCertificate(is);

            // Create a KeyStore containing our trusted CAs
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(defaultAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);//信任证书密钥
//            sslContext.init(null, new TrustManager[]{new UnSafeTrustManager()}, null);//信任所有
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeIo(is);
        }
        return null;
    }

    /**
     * 不安全的信任管理
     *
     * @return
     */
    public static SSLSocketFactory getUnSafeCertificate() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new UnSafeTrustManager()}, null);//信任所有
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String jsonString(Object... array) {
        StringBuilder builder = new StringBuilder();
        for (Object o : array) {
            builder.append(o);
        }
        return builder.toString();
    }
}
