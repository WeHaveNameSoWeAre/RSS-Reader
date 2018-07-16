package in.nimbo;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SiteCrawlerTest {
    public static SiteCrawler siteCrawler;

    @BeforeClass
    public static void init() throws MalformedURLException {
        siteCrawler = new SiteCrawler(new URL("http://rss.cnn.com/rss/edition.rss"));
    }

    @Test
    public void extractTextByPattern() throws IOException {
        String text  = siteCrawler.extractTextByPattern(new URL("https://edition.cnn.com/2018/07/13/politics/russia-investigation-indictments/index.html"),
                 "#body-text",
                new String[0]
        );
    }
}