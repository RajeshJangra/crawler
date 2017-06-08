package com.buildit.crawler.service;

import com.buildit.crawler.exception.CrawlerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Created by rajeshkumar on 08/06/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class OutputWriterTest {

    @Spy
    OutputWriter outputWriter;

    @Test
    public void writeNullCrawledUrls() throws Exception {
        final Set<String> crawledUrls = null;
        assertNull(outputWriter.write(crawledUrls));
    }

    @Test
    public void writeEmptyCrawledUrls() throws Exception {
        final Set<String> crawledUrls = new HashSet<>();
        assertNull(outputWriter.write(crawledUrls));
    }

    @Test
    public void writeCrawledUrlsSuccessfully() throws Exception {
        final BufferedWriter bufferedWriter = mock(BufferedWriter.class);
        doReturn(bufferedWriter).when(outputWriter).getWriter();
        final Set<String> crawledUrls = getCrawledUrls();
        assertEquals("output.txt", outputWriter.write(crawledUrls));
        verify(bufferedWriter, times(2)).write(anyString());
    }

    @Test(expected = CrawlerException.class)
    public void writeCrawledUrlsErrorWhileWritingToFile() throws Exception {
        final BufferedWriter bufferedWriter = mock(BufferedWriter.class);
        doReturn(bufferedWriter).when(outputWriter).getWriter();
        doThrow(new IOException()).when(bufferedWriter).write(anyString());
        final Set<String> crawledUrls = getCrawledUrls();
        outputWriter.write(crawledUrls);
    }

    @Test(expected = CrawlerException.class)
    public void writeCrawledUrlsErrorWhileWritingFlushing() throws Exception {
        final BufferedWriter bufferedWriter = mock(BufferedWriter.class);
        doReturn(bufferedWriter).when(outputWriter).getWriter();
        doThrow(new IOException()).when(bufferedWriter).flush();
        final Set<String> crawledUrls = getCrawledUrls();
        outputWriter.write(crawledUrls);
    }

    @Test(expected = CrawlerException.class)
    public void writeCrawledUrlsErrorWhileWritingClosing() throws Exception {
        final BufferedWriter bufferedWriter = mock(BufferedWriter.class);
        doReturn(bufferedWriter).when(outputWriter).getWriter();
        doThrow(new IOException()).when(bufferedWriter).close();
        final Set<String> crawledUrls = getCrawledUrls();
        outputWriter.write(crawledUrls);
    }

    private Set<String> getCrawledUrls() {
        final Set<String> crawledUrls = new HashSet<>();
        crawledUrls.add("http://www.wiprodigital.com");
        crawledUrls.add("http://www.wiprodigital.com/what-we-are");
        return crawledUrls;
    }
}