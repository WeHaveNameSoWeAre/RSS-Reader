package in.nimbo.model;

import java.util.Date;

public class Channel {
    private String title;
    private String description;
    private String link;
    private Date lastBuildDate;


    public Channel(String title, String description, String link, Date lastBuildDate) {
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

    public String getLink() {
        return link;
    }

    public Date getLastBuildDate() {
        return lastBuildDate;
    }
}
