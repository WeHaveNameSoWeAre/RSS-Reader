package in.nimbo.dao;

import in.nimbo.model.Item;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface ItemDAO {
    boolean checkItemExists(Item item) throws SQLException;

    void insertItem(Item item) throws SQLException;

    List<Item> getLastNewsOfChannel(int numOfRows, int channelId) throws SQLException;

    int getNumberOfItemsInChannelPerDay(Date dayDate, int channelId) throws SQLException;

    List<Item> searchByText(String search) throws SQLException;

    List<Item> searchByTitle(String search) throws SQLException;
}
