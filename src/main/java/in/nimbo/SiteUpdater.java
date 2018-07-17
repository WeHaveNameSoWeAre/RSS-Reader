package in.nimbo;

import in.nimbo.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SiteUpdater {
    private static volatile SiteUpdater instance;
    final Logger logger = LoggerFactory.getLogger(SiteUpdater.class);
    private final DatabaseHandler db = DatabaseHandler.getInstance();
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
            instance.start();
        }
        return instance;
    }

    public void crawl(URL rssLink) {
        SiteCrawler siteCrawler = new SiteCrawler(rssLink);
        executorService.schedule(siteCrawler, 0, TimeUnit.NANOSECONDS);
    }

    private void start() {
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
                Channel[] channels = db.getChannelsBeforeMinute(10);
                for (Channel channel : channels) {
                    logger.info("Scheduled Channel Crawling Started for {}", channel.getTitle());
                    crawl(channel.getLink());
                }

            } catch (SQLException e) {
                logger.error("error in Channels Crawler Starter Thread", e);
            }
        }

    }

}
