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
import java.util.concurrent.Callable;

public class OutputWriter implements Callable<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OutputWriter.class);

    private String fileName;
    private Set<String> stringSet;

    public OutputWriter(final String fileName, Set<String> stringSet) {
        this.fileName = fileName;
        this.stringSet = stringSet;
    }

    @Override
    public String call() throws Exception {
        return write();
    }

    protected String write() {
        if (validateInput()) return null;
        final BufferedWriter writer = getWriter();
        writeToFile(writer);
        closeWriter(writer);
        return fileName;
    }

    private boolean validateInput() {
        if (stringSet == null || stringSet.isEmpty()) {
            final String msg = "Input is null or empty";
            LOGGER.error(msg);
            return true;
        }
        return false;
    }

    private void writeToFile(final BufferedWriter bw) {
        stringSet.stream().forEach(url -> {
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
