package com.buildit.crawler.service;

import com.buildit.crawler.exception.CrawlerException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URLConnection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by rajeshkumar on 06/06/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class CrawlerTest {

    @InjectMocks
    Crawler crawler;

    @Mock
    private BlockingQueue<String> queue;
    @Mock
    private Set<String> crawledUrls;
    @Mock
    private Util util;


    @Test
    public void crawlQueueIsEmptyNothingHappens() {
        when(queue.isEmpty()).thenReturn(true);
        crawler.run();
        verify(queue, times(1)).isEmpty();
    }

    @Test(expected = CrawlerException.class)
    public void crawlPollingAUrlFromQueueTimesOut() throws Exception {
        when(queue.isEmpty()).thenReturn(false);
        when(queue.poll(60, TimeUnit.SECONDS)).thenThrow(InterruptedException.class);
        crawler.run();
        verify(queue, times(1)).isEmpty();
        verify(queue, times(1)).poll(60, TimeUnit.SECONDS);
    }

    @Test(expected = CrawlerException.class)
    public void crawlNormalizeUrlMalformedException() throws Exception {
        final String malformedUrl = "abc://www.wiprodigital.com:8080/what-we-do/";
        when(queue.isEmpty()).thenReturn(false);
        when(queue.poll(60, TimeUnit.SECONDS)).thenReturn(malformedUrl);
        crawler.run();
        verify(queue, times(1)).isEmpty();
        verify(queue, times(1)).poll(60, TimeUnit.SECONDS);
    }

    @Test
    public void crawlFetchedUrlAlreadyCrawled() throws Exception {
        final String url = "http://www.wiprodigital.com/what-we-do/";
        when(queue.poll(60, TimeUnit.SECONDS)).thenReturn(url);
        when(crawledUrls.contains(url)).thenReturn(true);
        crawler.crawl();
        verify(queue, times(1)).poll(60, TimeUnit.SECONDS);
        verify(crawledUrls, times(0)).add(url);
    }

    @Test
    public void crawlUrlConnectionIsNull() throws Exception {
        final String url = "http://www.wiprodigital.com/what-we-do/";
        final URLConnection connection = null;
        when(queue.poll(60, TimeUnit.SECONDS)).thenReturn(url);
        when(crawledUrls.contains(url)).thenReturn(false);
        when(util.getConnection(url)).thenReturn(connection);
        crawler.crawl();
        verify(queue, times(1)).poll(60, TimeUnit.SECONDS);
        verify(crawledUrls, times(1)).add(url);
        verify(util, times(1)).getConnection(url);
        verify(util, times(0)).getDocumentFromUrl(url, connection);
    }

    @Test
    public void crawlUrlConnectionContentTypeIsNull() throws Exception {
        final String url = "http://www.wiprodigital.com/what-we-do/";
        final URLConnection connection = mock(URLConnection.class);
        when(queue.poll(60, TimeUnit.SECONDS)).thenReturn(url);
        when(crawledUrls.contains(url)).thenReturn(false);
        when(util.getConnection(url)).thenReturn(connection);
        when(connection.getContentType()).thenReturn(null);
        crawler.crawl();
        verify(queue, times(1)).poll(60, TimeUnit.SECONDS);
        verify(crawledUrls, times(1)).add(url);
        verify(util, times(1)).getConnection(url);
        verify(util, times(0)).getDocumentFromUrl(url, connection);
    }

    @Test
    public void crawlUrlConnectionContentTypeIsNotHtml() throws Exception {
        final String url = "http://www.wiprodigital.com/what-we-do/";
        final URLConnection connection = mock(URLConnection.class);
        when(queue.poll(60, TimeUnit.SECONDS)).thenReturn(url);
        when(crawledUrls.contains(url)).thenReturn(false);
        when(util.getConnection(url)).thenReturn(connection);
        when(connection.getContentType()).thenReturn("multipart/form-data");
        crawler.crawl();
        verify(queue, times(1)).poll(60, TimeUnit.SECONDS);
        verify(crawledUrls, times(1)).add(url);
        verify(util, times(1)).getConnection(url);
        verify(util, times(0)).getDocumentFromUrl(url, connection);
    }

    @Test
    public void crawlChildUrlNotSimilarToRootUrl() throws Exception {
        final String url = "http://www.wiprodigital.com/";
        crawler.setRootUrl(url);
        final String childUrl = "http://www.google.com/";
        final URLConnection connection = mock(URLConnection.class);
        final Document doc = mock(Document.class);

        Collection<Element> elementSet = new HashSet<>();
        final Element element = mock(Element.class);
        elementSet.add(element);
        Elements elements = new Elements(elementSet);

        when(queue.poll(60, TimeUnit.SECONDS)).thenReturn(url);
        when(crawledUrls.contains(url)).thenReturn(false);
        when(util.getConnection(url)).thenReturn(connection);
        when(connection.getContentType()).thenReturn("text/html");
        when(util.getDocumentFromUrl(url, connection)).thenReturn(doc);
        when(doc.select("a[href]")).thenReturn(elements);
        when(element.attr("href")).thenReturn(childUrl);
        crawler.crawl();
        verify(queue, times(1)).poll(60, TimeUnit.SECONDS);
        verify(crawledUrls, times(1)).add(url);
        verify(util, times(1)).getConnection(url);
        verify(util, times(1)).getDocumentFromUrl(url, connection);
        verify(queue, times(0)).offer(childUrl);
    }

    @Test
    public void crawlChildUrlIsAlreadyCrawled() throws Exception {
        final String url = "http://www.wiprodigital.com/";
        crawler.setRootUrl(url);
        final String childUrl = "http://www.wiprodigital.com/what-we-do/";
        final URLConnection connection = mock(URLConnection.class);
        final Document doc = mock(Document.class);

        Collection<Element> elementSet = new HashSet<>();
        final Element element = mock(Element.class);
        elementSet.add(element);
        Elements elements = new Elements(elementSet);

        when(queue.poll(60, TimeUnit.SECONDS)).thenReturn(url);
        when(crawledUrls.contains(url)).thenReturn(false);
        when(crawledUrls.contains(childUrl)).thenReturn(true);
        when(util.getConnection(url)).thenReturn(connection);
        when(connection.getContentType()).thenReturn("text/html");
        when(util.getDocumentFromUrl(url, connection)).thenReturn(doc);
        when(doc.select("a[href]")).thenReturn(elements);
        when(element.attr("href")).thenReturn(childUrl);
        crawler.crawl();
        verify(queue, times(1)).poll(60, TimeUnit.SECONDS);
        verify(crawledUrls, times(1)).add(url);
        verify(util, times(1)).getConnection(url);
        verify(util, times(1)).getDocumentFromUrl(url, connection);
        verify(queue, times(0)).offer(childUrl);
    }

    @Test
    public void crawlChildUrlContainsHashCharacter() throws Exception {
        final String url = "http://www.wiprodigital.com/";
        crawler.setRootUrl(url);
        final String childUrl = "http://www.wiprodigital.com/what-we-do/#";
        final URLConnection connection = mock(URLConnection.class);
        final Document doc = mock(Document.class);

        Collection<Element> elementSet = new HashSet<>();
        final Element element = mock(Element.class);
        elementSet.add(element);
        Elements elements = new Elements(elementSet);

        when(queue.poll(60, TimeUnit.SECONDS)).thenReturn(url);
        when(crawledUrls.contains(url)).thenReturn(false);
        when(crawledUrls.contains(childUrl)).thenReturn(false);
        when(util.getConnection(url)).thenReturn(connection);
        when(connection.getContentType()).thenReturn("text/html");
        when(util.getDocumentFromUrl(url, connection)).thenReturn(doc);
        when(doc.select("a[href]")).thenReturn(elements);
        when(element.attr("href")).thenReturn(childUrl);
        crawler.crawl();
        verify(queue, times(1)).poll(60, TimeUnit.SECONDS);
        verify(crawledUrls, times(1)).add(url);
        verify(util, times(1)).getConnection(url);
        verify(util, times(1)).getDocumentFromUrl(url, connection);
        verify(queue, times(0)).offer(childUrl);
    }

    @Test
    public void crawlSuccessfullyAddedAllTheLinksToQueue() throws Exception {
        final String url = "http://www.wiprodigital.com/";
        crawler.setRootUrl(url);
        final String childUrl = "http://www.wiprodigital.com/what-we-do/";
        final URLConnection connection = mock(URLConnection.class);
        final Document doc = mock(Document.class);

        Collection<Element> elementSet = new HashSet<>();
        final Element element = mock(Element.class);
        elementSet.add(element);
        Elements elements = new Elements(elementSet);

        when(queue.poll(60, TimeUnit.SECONDS)).thenReturn(url);
        when(crawledUrls.contains(url)).thenReturn(false);
        when(crawledUrls.contains(childUrl)).thenReturn(false);
        when(util.getConnection(url)).thenReturn(connection);
        when(connection.getContentType()).thenReturn("text/html");
        when(util.getDocumentFromUrl(url, connection)).thenReturn(doc);
        when(doc.select("a[href]")).thenReturn(elements);
        when(element.attr("href")).thenReturn(childUrl);
        crawler.crawl();
        verify(queue, times(1)).poll(60, TimeUnit.SECONDS);
        verify(crawledUrls, times(1)).add(url);
        verify(util, times(1)).getConnection(url);
        verify(util, times(1)).getDocumentFromUrl(url, connection);
        verify(queue, times(1)).offer(childUrl);
    }

    @Test
    public void normalizeUrlSuccessfulWithDefaultPort() throws Exception {
        final String start = "http://www.wiprodigital.com";
        final String filePath = "/what-we-do/";
        final String url = start + ":80" + filePath;
        final String normalizedURL = crawler.normalizeURL(url);

        assertEquals(start + filePath, normalizedURL);
    }

    @Test
    public void normalizeUrlSuccessfulNonDefaultPort() throws Exception {
        final String start = "http://www.wiprodigital.com";
        final String filePath = "/what-we-do/";
        final String url = start + ":8080" + filePath;
        final String normalizedURL = crawler.normalizeURL(url);

        assertEquals(url, normalizedURL);
    }

    @Test(expected = CrawlerException.class)
    public void normalizeUrlUnsuccessful() throws Exception {
        final String url = "abc://www.wiprodigital.com:8080/what-we-do/";
        crawler.normalizeURL(url);
    }


}