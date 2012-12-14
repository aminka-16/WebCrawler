package com.griddynamics.crawler.http_crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

public interface WebCrawler {

    public void searchAndSaveLinks(String url, String pathToSave, int loopCount) throws MalformedURLException,
            UnknownHostException, IOException;

}
