package in.nimbo.model;

import java.net.URL;
import java.util.Date;

public class Item {
    private String title;
    private URL link;
    private String description;
    private Date pubDate;
    private int channelId;
    private String fullText = null;

    public Item(String title, URL link, String description, Date pubDate, int channelId) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        this.channelId = channelId;
    }

    public int getChannelId() {
        return channelId;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
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

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", link=" + link +
                ", description='" + description + '\'' +
                ", pubDate=" + pubDate +
                ", channelId=" + channelId +
                ", fullText='" + fullText + '\'' +
                '}';
    }
}
