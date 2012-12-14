package com.griddynamics.crawler.http_crawler.url;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;

import com.griddynamics.crawler.http_crawler.WebCrawler;
import com.griddynamics.crawler.http_util.Http;

public class SingleThreadCrawler implements WebCrawler {

    // 31 seconds
    public void searchAndSaveLinks(String url, String pathToSave, int loopCount) throws MalformedURLException,
            UnknownHostException, IOException {

        String html = Http.getHtml(url);

        if (!Http.saveHtmlIfDoesntExist(html, pathToSave))
            return;

        List<String> links = Http.getLinks(html, url);
        if (links != null && !links.isEmpty() & loopCount > 0)
            for (String link : links) {
                searchAndSaveLinks(link, pathToSave, --loopCount);
            }
    }

    public static void main(String[] args) throws MalformedURLException, IOException {
        SingleThreadCrawler extractor = new SingleThreadCrawler();
        extractor.searchAndSaveLinks("http://docs.oracle.com/javase/1.4.2/docs/api/java/io/package-summary.html",
                "d:\\linkSearcher\\", 1);
    }
}
