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
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class DatabaseHandler {
    private final static Logger logger = LoggerFactory.getLogger(DatabaseHandler.class);
    private static DatabaseHandler instance = null;
    private PreparedStatement selectLastNewsStatement;
    private PreparedStatement insertItemStatement;
    private PreparedStatement getItemIdStatement;
    private PreparedStatement getChannelIdStatement;
    private PreparedStatement insertChannelStatement;
    private PreparedStatement getItemCountForDayStatement;
    private PreparedStatement insertConfigStatement;
    private PreparedStatement selectConfigStatement;
    private PreparedStatement updateConfigStatement;
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
        selectLastNewsStatement = connection.prepareStatement(
                "SELECT items.id,title,`desc`,text,date,items.link FROM `items`" +
                        " INNER JOIN channels ON items.channelId = channels.id" +
                        " WHERE channelId = ? ORDER BY date DESC LIMIT ?"
        );
        getItemCountForDayStatement = connection.prepareStatement(
                "SELECT COUNT(*) AS num FROM items WHERE channelId = ? AND date BETWEEN ? AND ?"
        );

        selectConfigStatement = connection.prepareStatement(
                "SELECT * FROM configs WHERE linkHash = SHA1(?)"
        );

        insertConfigStatement = connection.prepareStatement(
                "INSERT INTO configs(link, bodyPattern, adPatterns, linkHash) VALUES (?,?,?,SHA1(?))"
        );
        updateConfigStatement = connection.prepareStatement(
                "UPDATE configs SET bodyPattern = ?,adPatterns = ? WHERE id = ?"
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

        if (item.getPubDate() != null)
            insertItemStatement.setTimestamp(5, new java.sql.Timestamp(item.getPubDate().getTime()));
        else
            insertItemStatement.setTimestamp(5, null);

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

    public Object[] getConfig(String siteLink) throws SQLException {
        selectConfigStatement.setString(1, siteLink);
        ResultSet resultSet = selectConfigStatement.executeQuery();
        if (resultSet.next())
            return new Object[]{resultSet.getInt("id"), resultSet.getString("bodyPattern"), resultSet.getString("adPatterns")};
        else
            throw new IllegalStateException("There were no config for that site");
    }


    public void insertConfig(String siteLink, String bodyPattern, String adPatterns) throws SQLException {
        insertConfigStatement.setString(1, siteLink);
        insertConfigStatement.setString(2, bodyPattern);
        insertConfigStatement.setString(3, adPatterns);
        insertConfigStatement.setString(4, siteLink);

        insertConfigStatement.executeUpdate();
    }

    public void updateConfig(int id,String bodyPattern, String adPatterns) throws SQLException {
        updateConfigStatement.setString(1,bodyPattern);
        updateConfigStatement.setString(2,adPatterns);
        updateConfigStatement.setInt(3,id);
        updateConfigStatement.executeUpdate();
    }


    // Query Methods
    public Item[] getLastNewsOfChannel(int numOfRows, String channelLink) throws SQLException, MalformedURLException {
        int channelId = getChannelId(channelLink);

        selectLastNewsStatement.setInt(1, channelId);
        selectLastNewsStatement.setInt(2, numOfRows);
        ResultSet resultSet = selectLastNewsStatement.executeQuery();
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

    public int getNumOfItems(Date dayDate, String channelLink) throws SQLException {
        Date startOfDay = atStartOfDay(dayDate);
        Date endOfDay = atEndOfDay(dayDate);
        int channelId = getChannelId(channelLink);
        getItemCountForDayStatement.setInt(1, channelId);
        getItemCountForDayStatement.setObject(2, startOfDay);
        getItemCountForDayStatement.setObject(3, endOfDay);
        ResultSet resultSet = getItemCountForDayStatement.executeQuery();
        if (resultSet.next())
            return resultSet.getInt("num");
        else
            return 0;
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
