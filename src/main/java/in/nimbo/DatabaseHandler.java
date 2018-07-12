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
    PreparedStatement insertChannelStatement;
    private Properties properties = new Properties();
    private Connection connection;

    private DatabaseHandler() throws SQLException {

        insertChannelStatement = connection.prepareStatement(
                "INSERT INTO channels (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
    }

    public static DatabaseHandler getInstance() {
        if (instance == null) {
            try {
                instance = new DatabaseHandler();
                instance.init();
            } catch (Exception e) {
                logger.error("database Connection Failed!!", e);
                instance = null;
                throw new RuntimeException("database Connection Failed!!", e);
            }
        }

        return instance;
    }

    private void init() throws ClassNotFoundException, SQLException {
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

        String driverString = String.format("jdbc:mysql://%s:%s/%s",
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

        logger.info("Connected to Database");


    }

    public int insertChannelAndReturnId(Channel channel) throws SQLException {
        insertChannelStatement.setString(1, channel.getTitle());
        return insertChannelStatement.executeUpdate();
    }

    public boolean checkItemExists(Item item) {
        // TODO: 7/11/18
        return false;
    }

    public void insertItem(Item item) {
        // TODO: 7/11/18
    }

}
