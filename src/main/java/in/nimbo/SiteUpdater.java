package in.nimbo;

import in.nimbo.dao.ChannelDAO;
import in.nimbo.impl.mysql.MysqlChannelDAOImpl;
import in.nimbo.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SiteUpdater {
    private static volatile SiteUpdater instance;
    final Logger logger = LoggerFactory.getLogger(SiteUpdater.class);
    private ChannelDAO channelDAO;
    private boolean started = false;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10, r -> {
        Thread thread = Executors.defaultThreadFactory().newThread(r);
        thread.setDaemon(true);
        return thread;
    });

    private SiteUpdater() {
    }

    public synchronized static SiteUpdater getInstance() {
        if (instance == null) {
            instance = new SiteUpdater();
        }
        return instance;
    }

    public void setChannelDAO(ChannelDAO channelDAO) {
        this.channelDAO = channelDAO;
    }

    public void crawl(URL rssLink) {
        SiteCrawler siteCrawler = new SiteCrawler(rssLink);
        executorService.schedule(siteCrawler, 0, TimeUnit.NANOSECONDS);
        logger.debug("{} scheduled in executor service!",rssLink);
    }

    public synchronized void start() {
        if (started)
            throw new IllegalStateException("Already Started");

        if (channelDAO == null)
            channelDAO = new MysqlChannelDAOImpl();

        started = true;

        executorService.scheduleAtFixedRate(new UpdaterThread(), 0, 30, TimeUnit.SECONDS);
    }

    public void close() {
        executorService.shutdown();
    }

    private class UpdaterThread implements Runnable {
        final Logger logger = LoggerFactory.getLogger(UpdaterThread.class);

        @Override
        public void run() {
            try {
                List<Channel> channels = channelDAO.getChannelsUpdatedBefore(1);
                for (Channel channel : channels) {
                    logger.info("Scheduled Channel Crawling Started for {}", channel.getName());
                    channel.setLastUpdate(new Date());
                    channelDAO.updateChannelLastDate(channel);
                    crawl(channel.getRssLink());
                }

            } catch (Exception e) {
                logger.error("error in Channels Crawler Starter Thread", e);
            }
        }

    }

}
