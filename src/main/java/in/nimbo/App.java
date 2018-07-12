package in.nimbo;

import com.rometools.rome.io.FeedException;

import java.io.IOException;
import java.net.URL;


public class App {
    public static void main(String[] args) throws IOException, FeedException {
        SiteUpdater siteUpdater = new SiteUpdater(new URL("http://www.tabnak.ir/fa/rss/allnews"));
        SiteUpdater s = new SiteUpdater(new URL("http://www.tabnak.ir/fa/rss/allnews"));
        siteUpdater.update();
        s.update();
    }
}
