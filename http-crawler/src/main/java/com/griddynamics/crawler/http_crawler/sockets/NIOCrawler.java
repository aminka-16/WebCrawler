package com.griddynamics.crawler.http_crawler.sockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.griddynamics.crawler.http_util.Http;

public class NIOCrawler extends OIOCrawler {

    public void searchAndSaveLinks(String urlPath, String pathToSave, int loopCount) throws MalformedURLException,
            UnknownHostException, IOException {
        URL url = new URL(urlPath);
        String host = url.getHost();
        String path = url.getPath();

        Charset charset = Charset.forName("UTF-8");

        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        InetSocketAddress addr = new InetSocketAddress(host, PORT);
        log.debug("ip: " + addr.getAddress());
        socketChannel.connect(addr);

        while (!socketChannel.finishConnect()) {

        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        boolean isFinished = false;

        socketChannel.register(selector, SelectionKey.OP_WRITE);

        StringBuffer response = new StringBuffer();
        while (!isFinished) {

            selector.select(200);
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectedKeys.iterator();

            while (it.hasNext()) {

                SelectionKey key = (SelectionKey) it.next();

                if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {

                    log.debug("writing");
                    SocketChannel sc = (SocketChannel) key.channel();

                    String request = createRequest(path, host);
                    // log.debug("request: " + request);

                    buffer.put(request.getBytes());

                    buffer.flip();

                    while (buffer.hasRemaining()) {
                        sc.write(buffer);
                    }

                    buffer.clear();

                    sc.register(selector, SelectionKey.OP_READ);

                    it.remove();

                    log.debug("finish writing");
                }
                if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {

                    log.debug("reading");
                    SocketChannel sc = (SocketChannel) key.channel();

                    int bytesRead = sc.read(buffer);

                    while (bytesRead != -1) {

                        buffer.flip();

                        while (buffer.hasRemaining()) {
                            response.append(charset.decode(buffer));
                            log.debug("response: " + response.toString());
                        }
                        // if (response.toString().contains("</HTML>"))
                        // break;
                        buffer.clear();
                        bytesRead = sc.read(buffer);
                        // log.debug("byte read: " + bytesRead);
                    }
                    isFinished = true;
                    it.remove();

                    log.debug("finish reading");
                }

            }
        }
        String resp = response.toString();
        String html = getHtml(resp);
        if (!Http.saveHtmlIfDoesntExist(html, pathToSave))
            return;

        List<String> links = Http.getLinks(html, urlPath);
        if (!links.isEmpty() & loopCount > 0)
            for (String link : links) {
                searchAndSaveLinks(link, pathToSave, --loopCount);
            }
        log.debug("finish");
    }

    // ip.src==87.245.215.23 or ip.dst==87.245.215.23
    public static void main(String[] args) throws MalformedURLException, UnknownHostException, IOException {
        NIOCrawler crawler = new NIOCrawler();
        crawler.searchAndSaveLinks("http://docs.oracle.com/javase/1.4.2/docs/api/java/io/package-summary.html",
                "d:\\linkSearcher\\", 1);
    }
}
