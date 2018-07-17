package in.nimbo.impl.mysql;

import in.nimbo.dao.ItemDAO;
import in.nimbo.model.Item;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MysqlItemDAOImpl implements ItemDAO {
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
    public int getNumerOfItemsInChannelPerDay(Date dayDate, int channelId) throws SQLException {
        return 0;
    }
}
