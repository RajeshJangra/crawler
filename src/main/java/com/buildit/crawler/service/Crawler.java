package com.buildit.crawler.service;

import com.buildit.crawler.exception.CrawlerException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * Created by rajeshkumar on 05/06/17.
 */
public class Crawler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    private BlockingQueue<String> queue;
    private Set<String> crawledUrls = new HashSet<>();
    private String rootUrl = null;
    private Util util;

    public Crawler(final BlockingQueue<String> queue, final Set<String> crawledUrls, String rootUrl, Util util) {
        this.queue = queue;
        this.crawledUrls = crawledUrls;
        this.rootUrl = rootUrl;
        this.util = util;
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            crawl();
        }
    }

    public void crawl() {
        String url = getUrlFromQueue();
        String normalizedUrl = normalizeURL(url);

        if (!crawledUrls.contains(normalizedUrl)) {
            crawledUrls.add(normalizedUrl);

            final URLConnection connection = util.getConnection(url);
            if (isInvalidContentType(connection)) {
                return;
            }

            Document doc = util.getDocumentFromUrl(url, connection);
            addLinksToQueue(doc);
        }
    }

    protected void addLinksToQueue(final Document doc) {
        final Elements elements = doc.select("a[href]");
        for (Element element : elements) {
            final String childUrl = element.attr("href");
            if (childUrl.startsWith(rootUrl) && !crawledUrls.contains(childUrl) && !childUrl.contains("#")) {
                queue.offer(childUrl);
            }
        }
    }

    private String getUrlFromQueue() {
        String url;
        try {
            url = queue.poll(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            final String msg = "Error while getting url from queue";
            LOGGER.error(msg);
            throw new CrawlerException(msg);
        }
        return url;
    }

    protected boolean isInvalidContentType(URLConnection connection) {
        if (connection == null || connection.getContentType() == null || !connection.getContentType().contains("text/html")) {
            return true;
        }
        return false;
    }

    protected String normalizeURL(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            final String msg = "Input url " + urlString + " is malformed. " + e.getMessage();
            LOGGER.error(msg);
            throw new CrawlerException(msg);
        }
        return url.getProtocol() + "://" + url.getHost() + (url.getPort() > 0 && url.getPort() != url.getDefaultPort() ? ":" + url.getPort() : "") + url.getFile();
    }

    public void setRootUrl(final String rootUrl) {
        this.rootUrl = rootUrl;
    }
}
