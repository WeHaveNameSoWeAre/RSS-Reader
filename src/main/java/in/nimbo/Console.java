package in.nimbo;

import asg.cliche.Command;
import asg.cliche.Param;
import in.nimbo.dao.ChannelDAO;
import in.nimbo.dao.ConfigDAO;
import in.nimbo.dao.ItemDAO;
import in.nimbo.impl.mysql.MysqlChannelDAOImpl;
import in.nimbo.impl.mysql.MysqlConfigDAOImpl;
import in.nimbo.impl.mysql.MysqlItemDAOImpl;
import in.nimbo.model.Channel;
import in.nimbo.model.Config;
import in.nimbo.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Console {
    private final static Logger logger = LoggerFactory.getLogger(Console.class);
    private ChannelDAO channelDAO;
    private ItemDAO itemDAO;
    private ConfigDAO configDAO;


    public Console() {
        this(new MysqlChannelDAOImpl(), new MysqlItemDAOImpl(), new MysqlConfigDAOImpl());
    }

    public Console(ChannelDAO channelDAO, ItemDAO itemDAO, ConfigDAO configDAO) {
        this.channelDAO = channelDAO;
        this.itemDAO = itemDAO;
        this.configDAO = configDAO;
    }

    @Command
    public void crawl(@Param(name = "RSS Link", description = "rss link for site to crawl.") String rssLink) {
        try {
            URL link = new URL(rssLink);
            SiteCrawler siteCrawler = new SiteCrawler(link);
            if(siteCrawler.fetchSite(link).statusCode() != 200)
                throw new RuntimeException("Document is Not Valid");
            SiteUpdater.getInstance().crawl(link);
        } catch (MalformedURLException e) {
            System.out.println("Please Enter a valid RSS URL");
        } catch (Exception e) {
            logger.warn("Crawling was unsuccessful for site " + rssLink, e);
            System.out.println("Crawling failed.\n Myabe Network is not Connected Or document is not Reachable.\n for more information see the logs!");
        }
    }

    @Command(description = "add or update site configs without ad patterns")
    public void addConfig(@Param(name = "site link") String siteLink,
                          @Param(name = "body pattern") String bodyPattern) {
        Config siteConfig = new Config(siteLink, bodyPattern);
        try {
            configDAO.insertOrUpdateConfig(siteConfig);
            System.out.println("Adding config was successful");
        } catch (Exception e) {
            logger.warn("saving config was unsuccessful for site " + siteLink, e);
            System.out.println("Adding Config Failed. for more information see the logs!");
        }
    }

    @Command(description = "add or update site configs including ad patterns")
    public void addConfig(@Param(name = "site link") String siteLink,
                          @Param(name = "body pattern") String bodyPattern,
                          @Param(name = "ad patterns") String adPatterns) {
        Config siteConfig = new Config(siteLink, bodyPattern, adPatterns.split(";"));
        try {
            configDAO.insertOrUpdateConfig(siteConfig);
            System.out.println("Adding config was successful");
        } catch (Exception e) {
            logger.warn("saving config was unsuccessful for site " + siteLink, e);
            System.out.println("Adding config failed. for more information see the logs!");
        }
    }

    @Command(description = "See last news of a site")
    public void getLastNews(
            @Param(name = "number of news") int numOfRows,
            @Param(name = "rss ID") int rssId) {
        if (numOfRows <= 0)
            System.out.println("please enter a positive number!");
        else {
            try {
                List<Item> lastNewsOfChannel = itemDAO.getLastNewsOfChannel(numOfRows, rssId);
                for (Item item : lastNewsOfChannel) {
                    System.out.println(item);
                }
            } catch (Exception e) {
                logger.warn("exception happend", e);
                System.out.println("Operation failed. for more information see the logs!");
            }
        }
    }

    @Command(description = "Show all channels")
    public void showChannels() {
        try {
            List<Channel> channels = channelDAO.getAllChannels();
            for (Channel channel : channels) {
                System.out.println("id =" + channel.getId());
                System.out.println(channel.getName());
                System.out.println();
            }
        } catch (Exception e) {
            logger.warn("exception happend", e);
            System.out.println("Operation failed. for more information see the logs!");
        }
    }

    @Command(abbrev = "gnn", description = "get items of today for a site")
    public void getNumberOfNews(@Param(name = "rss id for site") int rssId) {
        try {
            Date date = new Date();
            int items = itemDAO.getNumberOfItemsInChannelPerDay(date, rssId);
            System.out.println("This site has " + items + " item's in " + date);
        } catch (SQLException e) {
            logger.warn("sql exception happend", e);
            System.out.println("Adding config failed. for more information see the logs!");
        }
    }

    @Command(description = "search in item texts")
    public void searchInText(@Param(name = "search text") String text) {
        try {
            List<Item> items = itemDAO.searchByText(text);
            for (Item item : items) {
                System.out.println(item);
            }

        } catch (SQLException e) {
            logger.warn("sql exception happend", e);
            System.out.println("search failed. for more information see the logs!");
        }
    }


    @Command(description = "search in item titles")
    public void searchInTitle(@Param(name = "search text") String text) {
        try {
            List<Item> items = itemDAO.searchByTitle(text);
            for (Item item : items) {
                System.out.println(item);
            }

        } catch (SQLException e) {
            logger.warn("sql exception happend", e);
            System.out.println("search failed. for more information see the logs!");
        }
    }

    @Command(abbrev = "gnn", description = "get items of a day for a site")
    public void getNumberOfNews(@Param(name = "rss id for site") int rssId,
                                @Param(name = "year") int year,
                                @Param(name = "month") int month,
                                @Param(name = "day") int day
    ) {
        if (month > 12 || month < 1)
            System.out.println("unvalid date");
        else {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day);
                Date date = calendar.getTime();
                int items = itemDAO.getNumberOfItemsInChannelPerDay(date, rssId);
                System.out.println("This site has " + items + " item's in " + date);
            } catch (SQLException e) {
                logger.warn("sql exception happend", e);
                System.out.println("Adding config failed. for more information see the logs!");
            }
        }
    }
}
