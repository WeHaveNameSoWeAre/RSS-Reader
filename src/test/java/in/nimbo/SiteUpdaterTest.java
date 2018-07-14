package in.nimbo;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class SiteUpdaterTest {
    public static SiteUpdater siteUpdater;

    @BeforeClass
    public static void init() throws MalformedURLException {
        siteUpdater  = new SiteUpdater(new URL("http://rss.cnn.com/rss/edition.rss"));
    }

    @Test
    public void extractTextByPattern() throws IOException {
        String text  = siteUpdater.extractTextByPattern(new URL("https://edition.cnn.com/2018/07/13/politics/russia-investigation-indictments/index.html"),
                 "#body-text",
                new String[0]
        );
    }
}