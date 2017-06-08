package com.buildit.crawler.service;

/**
 * Created by rajeshkumar on 08/06/17.
 */

import com.buildit.crawler.exception.CrawlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class OutputWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(OutputWriter.class);

    private String fileName = "output.txt";

    public String write(Set<String> crawledUrls) {
        if (validateInput(crawledUrls)) return null;
        final BufferedWriter writer = getWriter();
        writeToFile(crawledUrls, writer);
        closeWriter(writer);
        return fileName;
    }

    private boolean validateInput(final Set<String> crawledUrls) {
        if (crawledUrls == null || crawledUrls.isEmpty()) {
            final String msg = "Crawled Urls are null";
            LOGGER.error(msg);
            return true;
        }
        return false;
    }

    private void writeToFile(final Set<String> crawledUrls, final BufferedWriter bw) {
        crawledUrls.stream().forEach(url -> {
            try {
                bw.write(url + "\n");
            } catch (IOException e) {
                final String msg = "Error while writing output to file: " + fileName + " " + e.getMessage();
                LOGGER.error(msg);
                throw new CrawlerException(msg);
            }
        });
    }

    protected void closeWriter(BufferedWriter writer) {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            final String msg = "Error while closing file: " + fileName + " " + e.getMessage();
            LOGGER.error(msg);
            throw new CrawlerException(msg);
        }
    }

    protected BufferedWriter getWriter() {
        try {
            return new BufferedWriter(new FileWriter(fileName));
        } catch (IOException e) {
            final String msg = "Error while opening file: " + fileName + " " + e.getMessage();
            LOGGER.error(msg);
            throw new CrawlerException(msg);
        }
    }
}
