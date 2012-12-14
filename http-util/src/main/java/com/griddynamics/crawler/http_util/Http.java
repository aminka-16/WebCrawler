package com.griddynamics.crawler.http_util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Http {

    private static final Pattern LINK_PATTERN = Pattern.compile("href=\"([^#].*?)\"", Pattern.CASE_INSENSITIVE);

    private static final Pattern TITLE_PATTERN = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE);

    public static String getHtml(String pageUrl) throws MalformedURLException, IOException {
        URL url = new URL(pageUrl);
        StringBuffer response = new StringBuffer();
        InputStream is = url.openStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        while (bis.available() > 0) {
            response.append((char) bis.read());
        }
        String html = response.toString();
        html = html.replaceAll("\\s+", " ");
        return html;
    }

    public static String getHtmlTitle(String html) {
        Matcher matcher = TITLE_PATTERN.matcher(html);
        String title = null;
        if (matcher.find()) {
            title = matcher.group(1);
            title = title.trim();
        }
        if (title == null || title.equals(""))
            title = "default";
        return title;
    }

    public static boolean saveHtmlIfDoesntExist(String html, String path) throws IOException {
        String title = Http.getHtmlTitle(html);
        File file = new File(path + title + ".html");
        if (!file.exists()) {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(html);
            bw.close();
            return true;
        }
        return false;
    }

    public static String getAbsoluteURL(String baseURL, String link) throws MalformedURLException {
        URL base = new URL(baseURL);
        URL url = new URL(base, link);
        return url.toString();
    }

    // TODO: add check for links on images, css and other
    public static List<String> getLinks(String html, String baseURL) throws MalformedURLException {
        ArrayList<String> links = new ArrayList<String>();
        Matcher matcher = LINK_PATTERN.matcher(html);
        String link = null;
        while (matcher.find()) {
            link = matcher.group(1);
            link = link.trim();
            if (!link.endsWith(".css")) {
                link = Http.getAbsoluteURL(baseURL, link);
                links.add(link);
            }
        }
        return links;
    }
}
