package com.buildit.crawler.service;

import com.buildit.crawler.exception.CrawlerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by rajeshkumar on 09/06/17.
 */
public class Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    public Document getDocumentFromUrl(final String url, final URLConnection connection) {
        Document doc;
        try {
            doc = Jsoup.parse(connection.getInputStream(), connection.getContentEncoding(), url);
        } catch (Exception e) {
            final String msg = "Error while parsing page at url: " + url + ". " + e.getMessage();
            LOGGER.error(msg);
            throw new CrawlerException(msg);
        }
        return doc;
    }

    public URLConnection getConnection(String url) {
        URLConnection connection;
        try {
            connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Buildit Crawler");
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(10000);
        } catch (IOException e) {
            final String msg = "Error while opening connection to " + url + ". " + e.getMessage();
            LOGGER.error(msg);
            throw new CrawlerException(msg);
        }
        return connection;
    }
}
