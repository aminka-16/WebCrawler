package com.griddynamics.crawler.http_crawler.sockets;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public abstract class OIOCrawler {

    static final int PORT = 80;

    static Pattern pattern = Pattern.compile("<html(.*?)>(.*?)</html>", Pattern.CASE_INSENSITIVE);

    // static LoggingConfiguration config = LoggingConfiguration.getInstance();
    // static {
    // config.configure();
    // }

    static final Logger log = Logger.getLogger(OIOCrawler.class);

    static String createRequest(String path, String host) {
        StringBuffer httpRequest = new StringBuffer();
        httpRequest.append("GET ").append(path).append(" HTTP/1.1").append("\r\n").append("User-Agent: curl/7.28.1")
                .append("\r\n").append("Host: ").append(host).append("\r\n").append("\r\n");
        return httpRequest.toString();
    }

    static void sendRequest(OutputStream out, String request) throws IOException {
        out.write(request.getBytes());
        out.write("\r\n".getBytes());
        out.flush();
    }

    static void sendRequest(BufferedWriter out, String request) throws IOException {
        out.write(request);
        out.write("\r\n");
        out.flush();
    }

    static String getResponse(BufferedReader in) throws IOException {
        log.debug("Start get response");
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        log.debug("End get response");
        return response.toString();
    }

    static String getResponse(BufferedInputStream bis, int timeOut) throws IOException {
        log.debug("Start get response");
        int firstChar = bis.read();
        for (int i = 0; firstChar == -1 || i < timeOut; i = i + 50) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                log.debug("InterruptedException in getRespose()");
            }
            firstChar = bis.read();
        }
        if (firstChar == -1) {
            throw new SocketTimeoutException(" Timeout exception ");
        }
        StringBuilder response = new StringBuilder();
        response.append((char) firstChar);
        while (bis.available() > 0) {
            response.append((char) bis.read());
        }
        log.debug("End get response");
        return response.toString();
    }

    static String getResponse(BufferedInputStream bis) throws IOException {
        log.debug("Start get response");
        int firstChar = bis.read();
        StringBuilder response = new StringBuilder();
        response.append((char) firstChar);
        while (bis.available() > 0) {
            response.append((char) bis.read());
        }
        log.debug("End get response");
        return response.toString();
    }

    static String getHtml(String response) throws IOException {
        response = response.replaceAll("\\s+", " ");
        Matcher matcher = pattern.matcher(response);
        String html = "";
        if (matcher.find())
            html = matcher.group(0);
        return html;
    }

}
