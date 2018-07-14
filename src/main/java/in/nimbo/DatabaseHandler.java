package in.nimbo;

import in.nimbo.model.Channel;
import in.nimbo.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseHandler {
    private final static Logger logger = LoggerFactory.getLogger(DatabaseHandler.class);
    private static DatabaseHandler instance = null;
    private PreparedStatement insertItemStatement, getItemIdStatement, getChannelIdStatement, insertChannelStatement;
    private Properties properties = new Properties();
    private Connection connection;

    private DatabaseHandler() {
    }

    public static DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
            try {
                instance.initDatabaseConnection();
                instance.initPreparedStatements();
            } catch (Exception e) {
                logger.error("database Connection Failed!!", e);
                instance = null;
                throw new RuntimeException("database Connection Failed!!", e);
            }
        }

        return instance;
    }

    private void initDatabaseConnection() throws ClassNotFoundException, SQLException {
        try {
            properties.load(getClass().getResourceAsStream("/databaseConfig.properties"));
        } catch (IOException e) {
            logger.warn("database config file opening error! Default values will be used!", e);
        }

        Object DBusername = properties.getOrDefault("username", "root");
        Object DBpassword = properties.getOrDefault("password", "");
        Object DBhostname = properties.getOrDefault("hostname", "localhost");
        Object DBport = properties.getOrDefault("port", "3306");
        Object DBname = properties.getOrDefault("databaseName", "rss_reader");

        logger.debug("database username: {}", DBusername);
        logger.debug("database password: {}", DBpassword);
        logger.debug("database hostname: {}", DBhostname);
        logger.debug("database port: {}", DBport);
        logger.debug("database DBName: {}", DBname);

        String driverString = String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8",
                DBhostname,
                DBport,
                DBname
        );

        Class.forName("com.mysql.jdbc.Driver");

        connection = DriverManager.getConnection(
                driverString,
                (String) DBusername,
                (String) DBpassword
        );

        Statement stmt = connection.createStatement();
        stmt.executeQuery("SET NAMES 'UTF8'");
        stmt.executeQuery("SET CHARACTER SET 'UTF8'");

        logger.info("Connected to Database");


    }

    private void initPreparedStatements() throws SQLException {
        insertChannelStatement = connection.prepareStatement(
                "INSERT INTO `channels` (name, rssLink, rssLinkHash, link) VALUES (?,?,SHA1(?),?)");
        getChannelIdStatement = connection.prepareStatement("SELECT id FROM channels WHERE rssLinkHash = SHA1(?)");
        getItemIdStatement = connection.prepareStatement("SELECT id FROM items WHERE linkHash = SHA1(?)");
        insertItemStatement = connection.prepareStatement(
                "INSERT INTO items(title, link, `desc`, text, date, channelId, linkHash)" +
                        " VALUES (?,?,?,?,?,?,SHA1(?))"
        );
    }

    public void insertChannel(Channel channel) throws SQLException {
        try {

            insertChannelStatement.setString(1, channel.getTitle());
            insertChannelStatement.setString(2, channel.getLink().toExternalForm());
            insertChannelStatement.setString(3, channel.getLink().toExternalForm());
            insertChannelStatement.setString(4, (channel.getLink()).getHost());
            insertChannelStatement.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            switch (e.getErrorCode()) {
                case 1062: //ERR_DUPLICATE Code
                case 1586: //ERR_DUPLICATE_WITH_KEY Code
                    logger.debug("channel {} already exists in database!", channel.getLink());
                    break;
                default:
                    logger.warn("insertChannel sql statement not executed", e);
                    throw new SQLException(e);
            }
        }
    }

    public boolean checkItemExists(Item item) throws SQLException {
        getItemIdStatement.setString(1, item.getLink().toExternalForm());
        ResultSet resultSet = getItemIdStatement.executeQuery();
        if (resultSet.next())
            return true;
        else
            return false;
    }

    public void insertItem(Item item) throws SQLException {
        insertItemStatement.setString(1, item.getTitle());
        insertItemStatement.setString(2, item.getLink().toExternalForm());
        insertItemStatement.setString(3, item.getDescription());
        insertItemStatement.setString(4, item.getFullText());
        insertItemStatement.setTimestamp(5, new java.sql.Timestamp(item.getPubDate().getTime()));
        insertItemStatement.setInt(6, item.getChannelId());
        insertItemStatement.setString(7, item.getLink().toExternalForm());

        insertItemStatement.executeUpdate();
    }

    public int getChannelId(Channel channel) throws SQLException {
        getChannelIdStatement.setString(1, channel.getLink().toExternalForm());
        ResultSet resultSet = getChannelIdStatement.executeQuery();
        if (resultSet.next())
            return resultSet.getInt("id");
        else
            throw new SQLException("Channel doesn't Exist!");
    }

    public int getChannelId(String channelLink) throws SQLException {
        PreparedStatement query = connection.prepareStatement("SELECT id FROM channels WHERE link = ?");
        query.setString(1, channelLink.trim());
        ResultSet resultSet = query.executeQuery();
        if (resultSet.next())
            return resultSet.getInt("id");
        else
            throw new SQLException("Channel doesn't Exist!");
    }


    // Query Methods

    public Item[] getLastNewsOfChannel(int numOfRows, String channelLink) throws SQLException, MalformedURLException {
        int channelId = getChannelId(channelLink);

        PreparedStatement query = connection.prepareStatement(
                "SELECT items.id,title,text,date,items.link FROM `items` INNER JOIN channels ON items.channelId = channels.id WHERE channelId = ? ORDER BY date DESC LIMIT ?"
        );
        query.setInt(1, channelId);
        query.setInt(2, numOfRows);
        ResultSet resultSet = query.executeQuery();
        ArrayList<Item> items = new ArrayList<>();
        while (resultSet.next()) {
            Item item = new Item(
                    resultSet.getString("title"),
                    new URL(resultSet.getString("link")),
                    null,
                    resultSet.getDate("date"),
                    channelId

            );
            items.add(item);
        }
        return items.toArray(new Item[0]);
    }
}
