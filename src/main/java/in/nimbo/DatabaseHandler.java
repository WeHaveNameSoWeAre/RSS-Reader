package in.nimbo;

import in.nimbo.impl.mysql.MysqlConnectionPool;
import in.nimbo.model.Channel;
import in.nimbo.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseHandler {
    private final static Logger logger = LoggerFactory.getLogger(DatabaseHandler.class);
    private static DatabaseHandler instance = null;

    private DatabaseHandler() {
    }

    public static DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }

        return instance;
    }


    public void insertChannel(Channel channel) throws SQLException {

        try (
                Connection connection = MysqlConnectionPool.getConnection();
                PreparedStatement insertChannelStatement = connection.prepareStatement(
                        "INSERT INTO `channels` (name, rssLink, rssLinkHash, link) VALUES (?,?,SHA1(?),?)")
        ) {

            insertChannelStatement.setString(1, channel.getName());
            insertChannelStatement.setString(2, channel.getRssLink().toExternalForm());
            insertChannelStatement.setString(3, channel.getRssLink().toExternalForm());
            insertChannelStatement.setString(4, (channel.getRssLink()).getHost());
            insertChannelStatement.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            switch (e.getErrorCode()) {
                case 1062: //ERR_DUPLICATE Code
                case 1586: //ERR_DUPLICATE_WITH_KEY Code
                    logger.debug("channel {} already exists in database!", channel.getRssLink());
                    break;
                default:
                    logger.warn("insertChannel sql statement not executed", e);
                    throw new SQLException(e);
            }
        }
    }

    public int getChannelId(Channel channel) throws SQLException {

        try (
                Connection connection = MysqlConnectionPool.getConnection();
                PreparedStatement getChannelIdStatement = connection.prepareStatement(
                        "SELECT id FROM channels WHERE rssLinkHash = SHA1(?)"
                )
        ) {
            getChannelIdStatement.setString(1, channel.getRssLink().toExternalForm());
            try (ResultSet resultSet = getChannelIdStatement.executeQuery()) {
                if (resultSet.next())
                    return resultSet.getInt("id");
                else
                    throw new SQLException("Channel doesn't Exist!");
            }
        }
    }

    public List<Object[]> getAllChannels() throws SQLException {
        try (
                Connection connection = MysqlConnectionPool.getConnection();
                PreparedStatement selectAllChannelsStatement = connection.prepareStatement("SELECT * FROM channels");
                ResultSet resultSet = selectAllChannelsStatement.executeQuery()
        ) {
            ArrayList<Object[]> channels = new ArrayList<>();
            while (resultSet.next()) {
                try {
                    channels.add(
                            new Object[]{resultSet.getInt("id"), resultSet.getString("name")}
                    );
                } catch (SQLException e) {
                    logger.warn("error during getting channels", e);
                }
            }
            return channels;
        }
    }

    public Channel[] getChannelsBeforeMinute(int minutes) throws SQLException {
        try (
                Connection connection = MysqlConnectionPool.getConnection();
                PreparedStatement getChannelsBeforeDate = connection.prepareStatement(
                        "SELECT *FROM channels WHERE lastUpdate < DATE_SUB(NOW(),INTERVAL ? MINUTE) ORDER BY lastUpdate ASC"
                )
        ) {
            getChannelsBeforeDate.setInt(1, minutes);
            try (ResultSet resultSet = getChannelsBeforeDate.executeQuery()) {
                ArrayList<Channel> channels = new ArrayList<>();
                while (resultSet.next()) {
                    try {
                        channels.add(
                                new Channel(
                                        resultSet.getString("name"),
                                        new URL(resultSet.getString("rssLink")),
                                        resultSet.getDate("lastUpdate")
                                )
                        );
                    } catch (SQLException | MalformedURLException e) {
                        logger.warn("error during getting channels", e);
                    }
                }
                return channels.toArray(new Channel[0]);
            }
        }
    }

    public boolean checkItemExists(Item item) throws SQLException {
        try (
                Connection connection = MysqlConnectionPool.getConnection();
                PreparedStatement getItemIdStatement =
                        connection.prepareStatement("SELECT id FROM items WHERE linkHash = SHA1(?)")
        ) {

            getItemIdStatement.setString(1, item.getLink().toExternalForm());

            try (ResultSet resultSet = getItemIdStatement.executeQuery()) {
                return resultSet.next();
            }

        }

    }

    public void insertItem(Item item) throws SQLException {
        try (
                Connection connection = MysqlConnectionPool.getConnection();
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

    public Item[] getLastNewsOfChannel(int numOfRows, int channelId) throws SQLException {
        try (
                Connection connection = MysqlConnectionPool.getConnection();
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
                return items.toArray(new Item[0]);
            }
        }
    }

    public int getNumOfItems(Date dayDate, int channelId) throws SQLException {
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


    public Object[] getConfig(String siteLink) throws SQLException {
        try (
                Connection connection = MysqlConnectionPool.getConnection();
                PreparedStatement selectConfigStatement = connection.prepareStatement(
                        "SELECT * FROM configs WHERE linkHash = SHA1(?)"
                )
        ) {
            selectConfigStatement.setString(1, siteLink);
            try (ResultSet resultSet = selectConfigStatement.executeQuery()) {
                if (resultSet.next())
                    return new Object[]{resultSet.getInt("id"), resultSet.getString("bodyPattern"), resultSet.getString("adPatterns")};
                else
                    throw new IllegalStateException("There were no config for that site");
            }
        }
    }


    public void insertConfig(String siteLink, String bodyPattern, String adPatterns) throws SQLException {
        try (
                Connection connection = MysqlConnectionPool.getConnection();
                PreparedStatement insertConfigStatement = connection.prepareStatement(
                        "INSERT INTO configs(link, bodyPattern, adPatterns, linkHash) VALUES (?,?,?,SHA1(?))"
                )
        ) {
            insertConfigStatement.setString(1, siteLink);
            insertConfigStatement.setString(2, bodyPattern);
            insertConfigStatement.setString(3, adPatterns);
            insertConfigStatement.setString(4, siteLink);

            insertConfigStatement.executeUpdate();
        }
    }

    public void updateConfig(int id, String bodyPattern, String adPatterns) throws SQLException {
        try (
                Connection connection = MysqlConnectionPool.getConnection();
                PreparedStatement updateConfigStatement = connection.prepareStatement(
                        "UPDATE configs SET bodyPattern = ?,adPatterns = ? WHERE id = ?"
                )
        ) {
            updateConfigStatement.setString(1, bodyPattern);
            updateConfigStatement.setString(2, adPatterns);
            updateConfigStatement.setInt(3, id);
            updateConfigStatement.executeUpdate();
        }
    }
}
