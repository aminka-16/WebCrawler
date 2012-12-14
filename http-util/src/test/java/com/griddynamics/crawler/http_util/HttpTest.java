package com.griddynamics.crawler.http_util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.Test;

public class HttpTest {
	
	private static final String CORRECT_PAGE_URL = "http://en.wikipedia.org/wiki/Mutual_exclusion";
	private static final String WRONG_PAGE_URL = "//bits.wikimedia.org/geoiplookup";
	
	private static final String CORRECT_LINK_TAG = "<a href=\"/wiki/Non-blocking_synchronization\" title=\"Non-blocking synchronization\"</a>";
	private static final String WRONG_LINK_TAG = "<a hrem=\"/wiki/Non-blocking_synchronization\" title=\"Non-blocking synchronization\"</a>";
	private static final String TAG_WITH_ANCHOR = "#cite_note-Taubenfeld:2004-2";
	private static final String LINK_ON_CSS = "../../stylesheet.css";
	
	@Test
	public void testGetHtml() throws MalformedURLException, IOException {
		assertTrue(Http.getHtml(CORRECT_PAGE_URL).contains("Mutual exclusion - Wikipedia, the free encyclopedia"));
	}
	
	@Test(expected=MalformedURLException.class)
	public void testGetWrongHtml() throws IOException{
		Http.getHtml(WRONG_PAGE_URL);
	}
	
	@Test
	public void testGetLinks() throws MalformedURLException{
		List<String> links = Http.getLinks(CORRECT_LINK_TAG, CORRECT_PAGE_URL);
		assertTrue(links.contains("http://en.wikipedia.org/wiki/Non-blocking_synchronization"));
		
		links = Http.getLinks(WRONG_LINK_TAG, CORRECT_PAGE_URL);
		assertFalse(links.contains("http://en.wikipedia.org/wiki/Non-blocking_synchronization"));
		
		links = Http.getLinks(TAG_WITH_ANCHOR, CORRECT_PAGE_URL);
		assertFalse(links.contains("http://en.wikipedia.org/wiki/cite_note-Taubenfeld:2004-2"));
		
		links = Http.getLinks(LINK_ON_CSS, CORRECT_PAGE_URL);
		assertFalse(links.contains("http://en.wikipedia.org/stylesheet.css"));
	}
	
	@Test
	public void testGetPageTitle() throws MalformedURLException, IOException {
		String expectedTitle = Http.getHtmlTitle(Http.getHtml(CORRECT_PAGE_URL));
		assertEquals(expectedTitle, "Mutual exclusion - Wikipedia, the free encyclopedia");
		assertFalse(expectedTitle, "Mutually exclusive events".equals(expectedTitle));
	}

	@Test
	public void testGetAbsoluteURL() throws MalformedURLException{
		String baseURL = "http://docs.oracle.com/javase/1.4.2/docs/api/java/io/package-summary.html";
		String relativeURL = "../../java/io/FilenameFilter.html";
		String absoluteURL = "http://docs.oracle.com/javase/1.4.2/docs/api/java/io/FilenameFilter.html";
		
		assertEquals(absoluteURL, Http.getAbsoluteURL(baseURL, relativeURL));
		
		relativeURL = "http://docs.oracle.com/javase/1.4.2/docs/api/java/io/ObjectStreamConstants.html";
		assertEquals(relativeURL, Http.getAbsoluteURL(baseURL, relativeURL));
	}
}
