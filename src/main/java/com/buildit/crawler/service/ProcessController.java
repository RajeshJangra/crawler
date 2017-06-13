package com.buildit.crawler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Created by rajeshkumar on 09/06/17.
 */
public class ProcessController {
    static private Logger LOGGER = LoggerFactory.getLogger(ProcessController.class);

    private BlockingQueue<String> queue = new ArrayBlockingQueue<>(5000);
    private ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(6);
    private Set<String> crawledUrls = new HashSet<>();
    Set<String> staticResources = new HashSet<>();
    private Util util = new Util();
    private OutputWriter urlWriter = new OutputWriter("urlOutput.txt", crawledUrls);
    private OutputWriter resWriter = new OutputWriter("resOutput.txt", staticResources);

    public List<String> process(String baseUrl) throws Exception {
        List<String> outputFileList = null;
        if (validateUrl(baseUrl)) {
            outputFileList = createThreads(baseUrl);
        } else {
            LOGGER.error("Input URL: " + baseUrl + " is not valid. Application will exit now");
        }
        return outputFileList;
    }

    private List<String> createThreads(final String baseUrl) throws Exception {
        queue.offer(baseUrl);
        executorService.setRejectedExecutionHandler(new CrawlerRejectedExecutionHandler());

        spawnThreads(baseUrl, executorService);

        //Waiting for crawling to finish
        waitForCrawling(queue);

        List<String> outputFileList = writeOutputToFiles();
        LOGGER.info("\nCrawling successfully completed. Please check output files for results.");

        executorService.shutdown();
        return outputFileList;
    }

    private List<String> writeOutputToFiles() throws Exception {
        Future<String> urFut = executorService.submit(urlWriter);
        Future<String> resFut = executorService.submit(resWriter);
        return Arrays.asList(getWriterOutput(urFut), getWriterOutput(resFut));
    }

    private String getWriterOutput(final Future<String> resFut) throws TimeoutException, InterruptedException, ExecutionException {
        String resOutputFile;
        try {
            resOutputFile = resFut.get(5, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            LOGGER.error("Timeout while writing to output file");
            throw e;
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error while writing to output file");
            throw e;
        }
        return resOutputFile;
    }

    private void spawnThreads(final String baseUrl, final ThreadPoolExecutor executorService) {
        executorService.submit(new Crawler(queue, crawledUrls, staticResources, baseUrl, util));
        //Lets the queue build initially
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.error("Error while waiting for the queue to build");
        }
        //Start the rest of threads
        IntStream.range(0, 5).forEach(counter -> executorService.submit(new Crawler(queue, crawledUrls, staticResources, baseUrl, util)));
    }

    protected void waitForCrawling(final BlockingQueue<String> queue) {
        while (!queue.isEmpty()) {
            try {
                Thread.sleep(2000);
                LOGGER.info("Processing: " + queue.size() + " items in queue");
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
