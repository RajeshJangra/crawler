package com.buildit.crawler.service;

import com.buildit.crawler.exception.CrawlerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ThreadPoolExecutor;

import static org.mockito.Mockito.*;

/**
 * Created by rajeshkumar on 08/06/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class CrawlerRejectedExecutionHandlerTest {
    @InjectMocks
    CrawlerRejectedExecutionHandler rejectedExecutionHandler;

    @Test
    public void rejectedExecutionSucessful() throws Exception {
        Runnable crawlerThread = mock(Runnable.class);
        ThreadPoolExecutor executor = mock(ThreadPoolExecutor.class);
        doAnswer(invocation -> null).when(executor).execute(crawlerThread);
        rejectedExecutionHandler.rejectedExecution(crawlerThread, executor);
        verify(executor, timeout(1)).execute(crawlerThread);
        verifyZeroInteractions(crawlerThread);
    }

    @Test(expected = CrawlerException.class)
    public void rejectedExecutionFailedWhilecallingExecute() throws Exception {
        Runnable crawlerThread = mock(Runnable.class);
        ThreadPoolExecutor executor = mock(ThreadPoolExecutor.class);
        doThrow(CrawlerException.class).when(executor).execute(crawlerThread);
        rejectedExecutionHandler.rejectedExecution(crawlerThread, executor);
        verify(executor, timeout(1)).execute(crawlerThread);
        verifyZeroInteractions(crawlerThread);
    }

}