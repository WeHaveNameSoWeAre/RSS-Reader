package in.nimbo.mock;

import in.nimbo.dao.ItemDAO;
import in.nimbo.model.Item;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ItemDAOFake implements ItemDAO {
    @Override
    public boolean checkItemExists(Item item) throws SQLException {
        return false;
    }

    @Override
    public void insertItem(Item item) throws SQLException {

    }

    @Override
    public List<Item> getLastNewsOfChannel(int numOfRows, int channelId) throws SQLException {
        return null;
    }

    @Override
    public int getNumberOfItemsInChannelPerDay(Date dayDate, int channelId) throws SQLException {
        return 0;
    }

    @Override
    public List<Item> searchByText(String search) throws SQLException {
        return null;
    }

    @Override
    public List<Item> searchByTitle(String search) throws SQLException {
        return null;
    }
}
