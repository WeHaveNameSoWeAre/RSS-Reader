package in.nimbo.Model;

import java.net.URL;
import java.util.Date;

public class Item {
    private String title;
    private URL link;
    private String description;
    private Date pubDate;
    private int channelId;

    public Item(String title, URL link, String description, Date pubDate,int channelId) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        this.channelId = channelId;
    }

    public int getChannelId() {
        return channelId;
    }

    public String getTitle() {

        return title;
    }

    public URL getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public Date getPubDate() {
        return pubDate;
    }
}
