package in.nimbo;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.URL;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException, FeedException {
        URL feedUrl = new URL("http://www.tabnak.ir/fa/rss/allnews");

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));

        System.out.println(feed);

    }
}
