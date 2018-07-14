package in.nimbo;

import com.rometools.rome.io.FeedException;
import in.nimbo.model.Item;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;


public class App {
    public static void main(String[] args) throws IOException, FeedException {
        SiteUpdater siteUpdater = new SiteUpdater(new URL("https://www.farsnews.com/RSS"));
        siteUpdater.update();
        try {
            Item[] items = DatabaseHandler.getInstance().getLastNewsOfChannel(10, "www.tabnak.ir");
            for (Item item : items) {
                System.out.println(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
