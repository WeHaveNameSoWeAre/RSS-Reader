package in.nimbo.model;

import java.net.URL;
import java.util.Date;

public class Channel {
    private String title;
    private String description;
    private URL link;
    private Date lastBuildDate;


    public Channel(String title, String description, URL link, Date lastBuildDate) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.lastBuildDate = lastBuildDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public URL getLink() {
        return link;
    }

    public Date getLastBuildDate() {
        return lastBuildDate;
    }
}