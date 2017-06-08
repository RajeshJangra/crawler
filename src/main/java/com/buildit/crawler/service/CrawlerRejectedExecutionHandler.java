package com.buildit.crawler.service;

import com.buildit.crawler.exception.CrawlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by rajeshkumar on 08/06/17.
 */
public class CrawlerRejectedExecutionHandler implements RejectedExecutionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerRejectedExecutionHandler.class);

    @Override
    public void rejectedExecution(Runnable crawlerThread, ThreadPoolExecutor executor) {
        try {
            executor.execute(crawlerThread);
        } catch (Exception e) {
            final String msg = "Error while re-executing a crawler thread" + e.getMessage();
            LOGGER.error(msg);
            throw new CrawlerException(msg);
        }
    }

}
