package com.buildit.crawler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.IntStream;

/**
 * Created by rajeshkumar on 09/06/17.
 */
public class ProcessController {
    static private Logger LOGGER = LoggerFactory.getLogger(ProcessController.class);

    private BlockingQueue<String> queue = new ArrayBlockingQueue<>(5000);
    private ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(6);
    private Set<String> crawledUrls = new HashSet<>();
    private Util util = new Util();
    private OutputWriter writer = new OutputWriter();

    public String process(String baseUrl) {
        String outputFile = null;
        if (validateUrl(baseUrl)) {
            outputFile = createThreads(baseUrl);
        } else {
            LOGGER.error("Input URL: " + baseUrl + " is not valid. Application will exit now");
        }
        return outputFile;
    }

    private String createThreads(final String baseUrl) {
        queue.offer(baseUrl);
        executorService.setRejectedExecutionHandler(new CrawlerRejectedExecutionHandler());

        spawnThreads(baseUrl, executorService);

        //Waiting for crawling to finish
        waitForCrawling(queue);
        executorService.shutdown();

        final String outputFile = writer.write(crawledUrls);
        LOGGER.info("\nCrawling successfully completed. Please check " + outputFile + "for results.");
        return outputFile;
    }

    private void spawnThreads(final String baseUrl, final ThreadPoolExecutor executorService) {
        executorService.submit(new Crawler(queue, crawledUrls, baseUrl, util));
        //Lets the queue build initially
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.error("Error while waiting for the queue to build");
        }
        //Start the rest of threads
        IntStream.range(0, 5).forEach(counter -> executorService.submit(new Crawler(queue, crawledUrls, baseUrl, util)));
    }

    protected void waitForCrawling(final BlockingQueue<String> queue) {
        while (!queue.isEmpty()) {
            try {
                Thread.sleep(2000);
                System.out.println("Processing: " + queue.size() + " items in queue");
            } catch (InterruptedException e) {
                LOGGER.error("Error while waiting for the queue to be consumed");
            }
        }
    }

    protected boolean validateUrl(String baseUrl) {
        try {
            new URL(baseUrl);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
