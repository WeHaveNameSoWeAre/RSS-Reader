package in.nimbo;

import in.nimbo.model.Channel;
import in.nimbo.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseHandler {
    final static Logger logger = LoggerFactory.getLogger(DatabaseHandler.class);
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

        String driverString = String.format("jdbc:mysql://%s:%s/%s??useUnicode=true&amp;characterEncoding=utf8",
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
                "INSERT INTO `channels` (name,link,linkHash) VALUES (?,?,SHA1(?))");
        getChannelIdStatement = connection.prepareStatement("SELECT id FROM channels WHERE linkHash = SHA1(?)");
        getItemIdStatement = connection.prepareStatement("SELECT id FROM items WHERE linkHash = SHA1(?)");
        insertItemStatement = connection.prepareStatement(
                "INSERT INTO items(title, link, `desc`, text, date, channelId, linkHash)" +
                        " VALUES (?,?,?,?,?,?,SHA1(?))"
        );
    }

    public void insertChannel(Channel channel) throws SQLException {
        try {

            insertChannelStatement.setString(1, channel.getTitle());
            insertChannelStatement.setString(2, channel.getLink());
            insertChannelStatement.setString(3, channel.getLink());
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
        insertItemStatement.setDate(5, new java.sql.Date(item.getPubDate().getTime()));
        insertItemStatement.setInt(6, item.getChannelId());
        insertItemStatement.setString(7, item.getLink().toExternalForm());

        insertItemStatement.executeUpdate();
    }

    public int getChannelId(Channel channel) throws SQLException {
        getChannelIdStatement.setString(1, channel.getLink());
        ResultSet resultSet = getChannelIdStatement.executeQuery();
        if (resultSet.next())
            return resultSet.getInt("id");
        else
            throw new SQLException("Channel doesn't Exist!");
    }
}
