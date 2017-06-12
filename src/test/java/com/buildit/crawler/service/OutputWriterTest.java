package com.buildit.crawler.service;

import com.buildit.crawler.exception.CrawlerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    OutputWriter outputWriter;

    @Before
    public void setUp() throws Exception {
        outputWriter = spy(new OutputWriter("output.txt", getCrawledUrls()));
    }

    @Test
    public void writeNullCrawledUrls() throws Exception {
        final Set<String> crawledUrls = null;
        outputWriter = spy(new OutputWriter("output.txt", crawledUrls));
        assertNull(outputWriter.write());
    }

    @Test
    public void writeEmptyCrawledUrls() throws Exception {
        final Set<String> crawledUrls = new HashSet<>();
        outputWriter = spy(new OutputWriter("output.txt", crawledUrls));
        assertNull(outputWriter.write());
    }

    @Test
    public void writeCrawledUrlsSuccessfully() throws Exception {
        final BufferedWriter bufferedWriter = mock(BufferedWriter.class);
        doReturn(bufferedWriter).when(outputWriter).getWriter();
        assertEquals("output.txt", outputWriter.write());
        verify(bufferedWriter, times(2)).write(anyString());
    }

    @Test(expected = CrawlerException.class)
    public void writeCrawledUrlsErrorWhileWritingToFile() throws Exception {
        final BufferedWriter bufferedWriter = mock(BufferedWriter.class);
        doReturn(bufferedWriter).when(outputWriter).getWriter();
        doThrow(new IOException()).when(bufferedWriter).write(anyString());
        outputWriter.write();
    }

    @Test(expected = CrawlerException.class)
    public void writeCrawledUrlsErrorWhileWritingFlushing() throws Exception {
        final BufferedWriter bufferedWriter = mock(BufferedWriter.class);
        doReturn(bufferedWriter).when(outputWriter).getWriter();
        doThrow(new IOException()).when(bufferedWriter).flush();
        outputWriter.write();
    }

    @Test(expected = CrawlerException.class)
    public void writeCrawledUrlsErrorWhileWritingClosing() throws Exception {
        final BufferedWriter bufferedWriter = mock(BufferedWriter.class);
        doReturn(bufferedWriter).when(outputWriter).getWriter();
        doThrow(new IOException()).when(bufferedWriter).close();
        outputWriter.write();
    }

    private Set<String> getCrawledUrls() {
        final Set<String> crawledUrls = new HashSet<>();
        crawledUrls.add("http://www.wiprodigital.com");
        crawledUrls.add("http://www.wiprodigital.com/what-we-are");
        return crawledUrls;
    }
}