package com.griddynamics.crawler.http_crawler.sockets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import com.griddynamics.crawler.http_crawler.WebCrawler;
import com.griddynamics.crawler.http_util.Http;

public class OIOCrawlerSingleThread extends OIOCrawler implements WebCrawler {

    public void searchAndSaveLinks(String urlPath, String pathToSave, int loopCount) throws MalformedURLException,
            UnknownHostException, IOException {

        URL url = new URL(urlPath);
        String host = url.getHost();
        String path = url.getPath();

        Socket socket = new Socket(host, PORT);
        socket.setTcpNoDelay(true);

        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

        // BufferedWriter out = new BufferedWriter(new
        // OutputStreamWriter(socket.getOutputStream(), "UTF8"));
        // BufferedReader in = new BufferedReader(new
        // InputStreamReader(socket.getInputStream()));

        try {
            String request = createRequest(path, host);
            sendRequest(out, request);
            String response = getResponse(in, 1000);
            String html = getHtml(response);
            if (!Http.saveHtmlIfDoesntExist(html, pathToSave))
                return;
            List<String> links = Http.getLinks(html, urlPath);
            if (!links.isEmpty() & loopCount > 0)
                for (String link : links) {
                    searchAndSaveLinks(link, pathToSave, --loopCount);
                }

        } finally {
            out.close();
            in.close();
            socket.close();
        }
    }

    public static void main(String[] args) throws MalformedURLException, UnknownHostException, IOException {
        OIOCrawlerSingleThread crawler = new OIOCrawlerSingleThread();
        crawler.searchAndSaveLinks("http://docs.oracle.com/javase/1.4.2/docs/api/java/io/package-summary.html",
                "d:\\linkSearcher\\", 1);
    }

}
