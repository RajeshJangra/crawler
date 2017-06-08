package com.buildit.crawler.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.when;

/**
 * Created by rajeshkumar on 09/06/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProcessControllerTest {
    @InjectMocks
    ProcessController processController;

    @Mock
    OutputWriter writer;

    @Test
    public void processInvalidUrl() throws Exception {
        final String url = "abc://www.wiprodigital.com/";
        assertNull(processController.process(url));
    }

    @Test
    public void processValidUrl() throws Exception {
        final String url = "http://www.wiprodigital.com/";
        when(writer.write(anySet())).thenReturn("output.txt");
        assertEquals("output.txt", processController.process(url));
    }

    @Test
    public void waitForCrawling() throws Exception {
    }

    @Test
    public void validateUrl() throws Exception {
    }

}