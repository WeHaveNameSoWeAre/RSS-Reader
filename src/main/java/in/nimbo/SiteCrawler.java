package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import in.nimbo.dao.ChannelDAO;
import in.nimbo.dao.ConfigDAO;
import in.nimbo.dao.ItemDAO;
import in.nimbo.impl.mysql.MysqlChannelDAOImpl;
import in.nimbo.impl.mysql.MysqlConfigDAOImpl;
import in.nimbo.impl.mysql.MysqlItemDAOImpl;
import in.nimbo.model.Channel;
import in.nimbo.model.Config;
import in.nimbo.model.Item;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;

public class SiteCrawler implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(SiteCrawler.class);
    private ChannelDAO channelDAO;
    private ItemDAO itemDAO;
    private ConfigDAO configDAO;
    private URL urlAddress;
    private final int TIMEOUT = 3000;

    public SiteCrawler(URL urlAddress) {
        this(urlAddress, new MysqlChannelDAOImpl(), new MysqlItemDAOImpl(), new MysqlConfigDAOImpl());
    }


    public SiteCrawler(URL urlAddress, ChannelDAO channelDAO, ItemDAO itemDAO, ConfigDAO configDAO) {
        this.urlAddress = urlAddress;
        this.channelDAO = channelDAO;
        this.itemDAO = itemDAO;
        this.configDAO = configDAO;
    }

    public void update() {
        logger.info("Start Updating: {}", urlAddress);
        try {

            Config siteConfig = null;

            try {
                siteConfig = configDAO.getConfig(urlAddress.getHost());
            } catch (IllegalStateException e) {
                logger.info("Config for {} not found. Default Algorithm Will Be Used", urlAddress.getHost());
            }

            SyndFeed feed = getSyndFeed();

            logger.info("feed reading was successful for site : {}," +
                    "  {} items found!", feed.getTitle(), feed.getEntries().size());

            logger.trace(feed.toString());
            Channel channel = new Channel(feed.getTitle(), urlAddress, new Date(), urlAddress.getHost());

            int channelId;

            try {
                channelId = channelDAO.getChannelId(channel.getRssLink());
                channel.setId(channelId);
                channelDAO.updateChannelLastDate(channel);
            } catch (SQLException e) {
                channelDAO.insertChannel(channel);
                channelId = channelDAO.getChannelId(channel.getRssLink());
            }

            for (SyndEntry entry : feed.getEntries()) {
                String description = entry.getDescription() != null ? entry.getDescription().getValue() : "";

                Item item = new Item(entry.getTitle(), new URL(entry.getLink()), description, entry.getPublishedDate(), channelId);

                logger.debug("Checking item {}", item.getTitle());
                if (itemDAO.checkItemExists(item)) continue;

                try {

                    String newsText;
                    if (siteConfig != null) {
                        newsText = extractTextByPattern(item.getLink(), siteConfig.getBodyPattern(), siteConfig.getAdPatterns());
                    } else {
                        newsText = extractTextAutomatically(item.getLink());
                    }

                    item.setText(newsText);
                    itemDAO.insertItem(item);
                } catch (IOException e) {
                    logger.warn(e.getMessage(), e);
                } catch (Exception e) {
                    logger.debug("failed to load fullText for item   {}, for more information enable debug level", item.getTitle());
                    logger.debug("error", e);
                    logger.trace("Printing Item : {}", item);
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.err.println("crawling failed for site" + urlAddress + "! for more information see rssReader.log");
        }

    }

    SyndFeed getSyndFeed() throws FeedException, IOException {
        SyndFeedInput input = new SyndFeedInput();
        return input.build(new XmlReader(urlAddress));
    }

    /**
     * this method extract article text using given patterns.
     * Notice: If there is no pattern or config for this link available, this method is useless.
     * instead look at see also part.
     *
     * @param link        article link
     * @param bodyPattern html selector for body of article e.g "div#article"
     * @param adPatterns  patterns for advertisements to be removed from article text
     * @return extracted article text
     * @throws IOException                                      if url is not valid
     * @throws org.jsoup.select.Selector.SelectorParseException if one of passed patterns is not valid (unchecked exception)
     * @throws IllegalStateException                            if element not found or text is null
     */
    String extractTextByPattern(URL link, String bodyPattern, String[] adPatterns) throws IOException {
        Document doc = fetchSite(link).parse();

        for (String adPattern : adPatterns)
            doc.select(adPattern).remove();

        Element firstElement = doc.select(bodyPattern).first();

        if (firstElement == null)
            throw new IllegalStateException("element not found");

        String text = firstElement.text();
        if (text.trim().isEmpty())
            throw new IllegalStateException("text is null");

        return text;
    }

    String extractTextAutomatically(URL link) throws BoilerpipeProcessingException, IOException {
        Connection.Response response = fetchSite(link);
        return ArticleExtractor.INSTANCE.getText(response.body());
    }

    Connection.Response fetchSite(URL link) throws IOException {
        Connection.Response response = Jsoup.connect(link.toExternalForm()).method(Connection.Method.GET).timeout(TIMEOUT).execute();
        return response;
    }

    @Override
    public void run() {
        update();
    }

}
