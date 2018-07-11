package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import in.nimbo.Model.Channel;
import in.nimbo.Model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class SiteUpdater {
    final Logger logger = LoggerFactory.getLogger(SiteUpdater.class);
    final DatabaseHandler db = DatabaseHandler.getInstance();
    private URL urlAddress;
    public void update() throws IOException, FeedException {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(urlAddress));

        Channel channel = new Channel(feed.getTitle(), feed.getDescription(), feed.getLink(), feed.getPublishedDate());
        int channelId = db.insertChannel(channel);

        for (SyndEntry entry : feed.getEntries()) {
            String description = entry.getDescription() != null ? entry.getDescription().getValue() : "";
            Item item = new Item(entry.getTitle(), new URL(entry.getLink()), description, entry.getPublishedDate(),channelId);
            if(db.checkItemExists(item)) continue;



        }

    }

    public SiteUpdater(URL urlAddress) {
        this.urlAddress = urlAddress;
    }
}
