package com.griddynamics.crawler.http_crawler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.apache.commons.validator.routines.UrlValidator;

import com.griddynamics.crawler.http_crawler.sockets.OIOCrawlerMultithread;

import static com.griddynamics.crawler.http_crawler.Constants.*;

public class Runner {

    private static String URLPath;
    private static String pathToSave;
    private static int loopCount;

    private static String initParams(String[] args) {

        if (args.length == 0)
            return EMPTHY_PARAMS;

        if (args[0].equalsIgnoreCase("help"))
            return HELP;

        URLPath = args[0];
        if (!checkURL(URLPath))
            return NOT_CORRECT_URL;

        pathToSave = args[1];
        if (!checkDoesDirecotyExist(pathToSave))
            return NON_EXISTEN_FOLDER;

        try {
            loopCount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return NOT_A_NUMBER;
        }

        return null;
    }

    private static boolean checkURL(String URL) {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(URL);
    }

    private static boolean checkDoesDirecotyExist(String path) {
        File file = new File(pathToSave);
        return file.exists();
    }

    public static void main(String[] args) throws MalformedURLException, UnknownHostException, IOException {
        String error = initParams(args);
        if (!(error == null || error.equals("null")))
            System.out.println("error: " + error);
        else {
            WebCrawler crawler = new OIOCrawlerMultithread();
            crawler.searchAndSaveLinks(URLPath, pathToSave, loopCount);
        }
    }
}

class Demo {
    public static void main(String[] args) throws Exception {
        Runner.main(new String[] { "http://docs.oracle.com/javase/1.4.2/docs/api/java/io/package-summary.html",
                "d:\\linkSearcher\\", "v" });
    }
}