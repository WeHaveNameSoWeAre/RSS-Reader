package in.nimbo.model;

import java.net.URL;
import java.util.Date;
import java.util.Objects;

public class Item {
    private Integer id;
    private String title;
    private URL link;
    private String desc;
    private String text = null;
    private Date date;
    private int channelId;

    public Item(String title, URL link, String desc, Date date, int channelId) {
        this(title, link, desc, null, date, channelId);
    }

    public Item(String title, URL link, String desc, String text, Date date, int channelId) {
        this(null, title, link, desc, text, date, channelId);
    }

    public Item(Integer id, String title, URL link, String desc, String text, Date date, int channelId) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.desc = desc;
        this.text = text;
        this.date = date;
        this.channelId = channelId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(link, item.link);
    }

    @Override
    public int hashCode() {

        return Objects.hash(link);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", link=" + link +
                ", text='" + text + '\'' +
                ", desc='" + desc + '\'' +
                ", date=" + date +
                ", channelId=" + channelId +
                '}';
    }
}
