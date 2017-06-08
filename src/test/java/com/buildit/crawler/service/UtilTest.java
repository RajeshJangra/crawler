package com.buildit.crawler.service;

import com.buildit.crawler.exception.CrawlerException;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.net.URLConnection;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created by rajeshkumar on 09/06/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class UtilTest {

    @InjectMocks
    Util util;

    @Test
    public void getDocumentFromUrlSuccessful() throws Exception {
        URLConnection connection = mock(URLConnection.class);
        final String html = getHtml();
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(html.getBytes(UTF_8)));
        when(connection.getContentEncoding()).thenReturn(UTF_8.toString());
        String url = "http://www.wiprodigital.com";
        final Document documentFromUrl = util.getDocumentFromUrl(url, connection);
        assertNotNull(documentFromUrl);
        verify(connection, times(1)).getInputStream();
        verify(connection, times(1)).getContentEncoding();
    }

    @Test(expected = CrawlerException.class)
    public void getDocumentFromUrlEncodingThrowsException() throws Exception {
        URLConnection connection = mock(URLConnection.class);
        final String html = getHtml();
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(html.getBytes(UTF_8)));
        when(connection.getContentEncoding()).thenThrow(new RuntimeException());
        String url = "http://www.wiprodigital.com";
        util.getDocumentFromUrl(url, connection);
    }

    private String getHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h1>My First Heading</h1>\n" +
                "\n" +
                "<p>My first paragraph.</p>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }

    @Test(expected = CrawlerException.class)
    public void getDocumentFromUrlnputStreamThrowsException() throws Exception {
        URLConnection connection = mock(URLConnection.class);
        when(connection.getInputStream()).thenThrow(new RuntimeException());
        String url = "http://www.wiprodigital.com";
        util.getDocumentFromUrl(url, connection);
    }

    @Test
    public void getConnectionSuccessful() throws Exception {
        final URLConnection connection = util.getConnection("http://www.wiprodigital.com");
        assertNotNull(connection);
    }

    @Test(expected = CrawlerException.class)
    public void getConnectionFailsDueToWrongURL() throws Exception {
        final String url = "abc://www.google.com";
        util.getConnection(url);
    }

}