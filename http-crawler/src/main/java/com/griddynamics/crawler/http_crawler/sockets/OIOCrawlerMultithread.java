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

public class OIOCrawlerMultithread extends OIOCrawler implements WebCrawler {

    public void searchAndSaveLinks(String urlPath, String pathToSave, int loopCount) throws MalformedURLException,
            UnknownHostException, IOException {
        Thread t = new Thread(new Executor(urlPath, pathToSave, loopCount));
        t.start();
    }

    private class Executor implements Runnable {
        private String urlPath;
        private String pathToSave;
        private int loopCount;

        Executor(String urlPath, String pathToSave, int loopCount) {
            this.urlPath = urlPath;
            this.pathToSave = pathToSave;
            this.loopCount = loopCount;
        }

        public void run() {
            Socket socket = null;

            BufferedOutputStream out = null;

            BufferedInputStream in = null;

            // BufferedWriter out = null;
            // BufferedReader in = null;
            try {
                URL url = new URL(urlPath);
                String host = url.getHost();
                String path = url.getPath();

                socket = new Socket(host, PORT);
                // out = new BufferedWriter(new
                // OutputStreamWriter(socket.getOutputStream(), "UTF8"));
                // in = new BufferedReader(new
                // InputStreamReader(socket.getInputStream()));

                out = new BufferedOutputStream(socket.getOutputStream());

                in = new BufferedInputStream(socket.getInputStream());

                String requst = createRequest(path, host);
                sendRequest(out, requst);
                String response = getResponse(in, 1000);
                String html = getHtml(response);
                if (!Http.saveHtmlIfDoesntExist(html, pathToSave))
                    return;
                List<String> links = Http.getLinks(html, urlPath);
                if (!links.isEmpty() & loopCount > 0)
                    for (String link : links) {
                        Thread thread = new Thread(new Executor(link, pathToSave, --loopCount));
                        thread.start();

                    }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public static void main(String[] args) throws MalformedURLException, UnknownHostException, IOException {
        OIOCrawlerMultithread crawler = new OIOCrawlerMultithread();
        crawler.searchAndSaveLinks("http://docs.oracle.com/javase/1.4.2/docs/api/java/io/package-summary.html",
                "d:\\linkSearcher\\", 1);
    }

}
