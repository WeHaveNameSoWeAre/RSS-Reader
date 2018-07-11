package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import in.nimbo.model.Channel;
import in.nimbo.model.Item;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class SiteUpdater {
    final Logger logger = LoggerFactory.getLogger(SiteUpdater.class);
    final DatabaseHandler db = DatabaseHandler.getInstance();
    private URL urlAddress;

    public SiteUpdater(URL urlAddress) {
        this.urlAddress = urlAddress;
    }

    public void update() {
        logger.info("Start Updating: {}", urlAddress);
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(urlAddress));

            logger.info("feed reading was successful for site : {}," +
                    "  {} items found!", feed.getTitle(), feed.getEntries().size());

            Channel channel = new Channel(feed.getTitle(), feed.getDescription(), feed.getLink(), feed.getPublishedDate());
            SiteConfig siteConfig = new FileSiteConfig(urlAddress.getHost());
            int channelId = db.insertChannelAndReturnId(channel);

            for (SyndEntry entry : feed.getEntries()) {
                String description = entry.getDescription() != null ? entry.getDescription().getValue() : "";
                Item item = new Item(entry.getTitle(), new URL(entry.getLink()), description, entry.getPublishedDate(), channelId);

                logger.info("Checking item {}", item.getTitle());

                if (db.checkItemExists(item)) continue;

                try {
                    String newsText = extractTextByPattern(item.getLink(), siteConfig.getBodyPattern(), siteConfig.getAdPatterns());
                    item.setFullText(newsText);
                    db.insertItem(item);
                } catch (IOException e) {
                    logger.warn(e.getMessage(), e);
                } catch (Exception e) {
                    logger.info("failed to load fullText for item   {}, for more information enable debug level", item.getTitle());
                    logger.debug("Printing Item : {}", item);
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * this method extract article text by using given patterns.
     * Notice: If there is no pattern or config for this link available, this method is useless.
     * instead look at see also part.
     *
     * @param link        article link
     * @param bodyPattern html selector for body of article e.g "div#article"
     * @param adPatterns  patterns for advertisements to be removed from article text
     * @return extracted article text
     * @throws IOException                                      if url is not valid
     * @throws org.jsoup.select.Selector.SelectorParseException if one of passed patterns is not valid (unchecked exception)
     */
    String extractTextByPattern(URL link, String bodyPattern, String[] adPatterns) throws IOException {
        Document doc = Jsoup.connect(link.toExternalForm()).get();

        for (String adPattern : adPatterns)
            doc.select(adPattern).remove();

        return doc.select(bodyPattern).first().text();
    }
}
