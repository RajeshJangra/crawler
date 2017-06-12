package com.buildit.crawler.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by rajeshkumar on 09/06/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProcessControllerTest {
    @InjectMocks
    ProcessController processController;

    @Mock
    private ThreadPoolExecutor executorService;

    @Mock
    OutputWriter urlWriter;

    @Mock
    OutputWriter resWriter;

    @Mock
    private BlockingQueue<String> queue;

    @Test
    public void processInvalidUrl() throws Exception {
        final String url = "abc://www.wiprodigital.com/";
        assertNull(processController.process(url));
    }

    @Test
    public void processValidUrl() throws Exception {
        final String url = "http://www.wiprodigital.com/";
        Future<String> future = mock(Future.class);
        when(executorService.submit(any(OutputWriter.class))).thenReturn(future);
        when(future.get(5, TimeUnit.MINUTES)).thenReturn("urlOutput.txt").thenReturn("resOutput.txt");
        when(queue.isEmpty()).thenReturn(true);
        final List<String> outputFiles = processController.process(url);
        final List<String> expected = getOutputFiles();
        assertEquals(expected.size(), outputFiles.size());
        for (String str : expected) {
            assertTrue(outputFiles.contains(str));
        }
    }

    @Test
    public void waitForCrawling() throws Exception {
    }

    @Test
    public void validateUrl() throws Exception {
    }

    private List<String> getOutputFiles() {
        List<String> outputFiles = new ArrayList<>();
        outputFiles.add("urlOutput.txt");
        outputFiles.add("resOutput.txt");
        return outputFiles;
    }

}