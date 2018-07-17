package in.nimbo.impl.mysql;

import in.nimbo.dao.ItemDAO;
import in.nimbo.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class MysqlItemDAOImpl implements ItemDAO {
    private final static Logger logger = LoggerFactory.getLogger(MysqlItemDAOImpl.class);

    @Override
    public boolean checkItemExists(Item item) throws SQLException {
        try (
                Connection connection = getConnection();
                PreparedStatement getItemIdStatement =
                        connection.prepareStatement("SELECT id FROM items WHERE linkHash = SHA1(?)")
        ) {

            getItemIdStatement.setString(1, item.getLink().toExternalForm());

            try (ResultSet resultSet = getItemIdStatement.executeQuery()) {
                return resultSet.next();
            }

        }
    }

    @Override
    public void insertItem(Item item) throws SQLException {
        try (
                Connection connection = getConnection();
                PreparedStatement insertItemStatement = connection.prepareStatement(
                        "INSERT INTO items(title, link, `desc`, text, date, channelId, linkHash)" +
                                " VALUES (?,?,?,?,?,?,SHA1(?))"
                )
        ) {
            insertItemStatement.setString(1, item.getTitle());
            insertItemStatement.setString(2, item.getLink().toExternalForm());
            insertItemStatement.setString(3, item.getDesc());
            insertItemStatement.setString(4, item.getText());

            if (item.getDate() != null)
                insertItemStatement.setTimestamp(5, new Timestamp(item.getDate().getTime()));
            else
                insertItemStatement.setTimestamp(5, null);

            insertItemStatement.setInt(6, item.getChannelId());
            insertItemStatement.setString(7, item.getLink().toExternalForm());

            insertItemStatement.executeUpdate();
        }
    }

    @Override
    public List<Item> getLastNewsOfChannel(int numOfRows, int channelId) throws SQLException {
        {
            try (
                    Connection connection = getConnection();
                    PreparedStatement selectLastNewsStatement = connection.prepareStatement(
                            "SELECT items.id,title,`desc`,text,date,items.link FROM `items`" +
                                    " INNER JOIN channels ON items.channelId = channels.id" +
                                    " WHERE channelId = ? ORDER BY date DESC LIMIT ?"
                    )
            ) {
                selectLastNewsStatement.setInt(1, channelId);
                selectLastNewsStatement.setInt(2, numOfRows);
                try (ResultSet resultSet = selectLastNewsStatement.executeQuery()) {
                    ArrayList<Item> items = new ArrayList<>();
                    while (resultSet.next()) {
                        try {
                            Item item = new Item(
                                    resultSet.getString("title"),
                                    new URL(resultSet.getString("link")),
                                    resultSet.getString("link"),
                                    resultSet.getString("text"),
                                    resultSet.getDate("date"),
                                    channelId

                            );
                            items.add(item);
                        } catch (MalformedURLException e) {
                            logger.warn("item link is not valid", e);
                        }
                    }
                    return items;
                }
            }
        }
    }

    @Override
    public int getNumberOfItemsInChannelPerDay(Date dayDate, int channelId) throws SQLException {
        {
            try (
                    Connection connection = MysqlConnectionPool.getConnection();
                    PreparedStatement getItemCountForDayStatement = connection.prepareStatement(
                            "SELECT COUNT(*) AS num FROM items WHERE channelId = ? AND date BETWEEN ? AND ?"
                    )
            ) {
                Date startOfDay = atStartOfDay(dayDate);
                Date endOfDay = atEndOfDay(dayDate);
                getItemCountForDayStatement.setInt(1, channelId);
                getItemCountForDayStatement.setTimestamp(2, new Timestamp(startOfDay.getTime()));
                getItemCountForDayStatement.setTimestamp(3, new Timestamp(endOfDay.getTime()));

                try (ResultSet resultSet = getItemCountForDayStatement.executeQuery()) {
                    if (resultSet.next())
                        return resultSet.getInt("num");
                    else
                        return 0;
                }
            }
        }
    }

    protected Connection getConnection() throws SQLException {
        return MysqlConnectionPool.getConnection();
    }

    private Date atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private Date atStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
