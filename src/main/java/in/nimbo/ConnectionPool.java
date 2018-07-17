package in.nimbo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionPool {
    private final static Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    private static final Object mutex = new Object();
    private static ConnectionPool instance;
    private volatile HikariDataSource ds;

    private ConnectionPool() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/databaseConfig.properties"));
        } catch (IOException e) {
            logger.warn("database config file opening error! Default values will be used!", e);
        }

        Object DbUsername = properties.getOrDefault("username", "root");
        Object DbPassword = properties.getOrDefault("password", "");
        Object DbHostname = properties.getOrDefault("hostname", "localhost");
        Object DbPort = properties.getOrDefault("port", "3306");
        Object DbName = properties.getOrDefault("databaseName", "rss_reader");

        logger.debug("database username: {}", DbUsername);
        logger.debug("database password: {}", DbPassword);
        logger.debug("database hostname: {}", DbHostname);
        logger.debug("database port: {}", DbPort);
        logger.debug("database DbName: {}", DbName);

        String driverString = String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8",
                DbHostname,
                DbPort,
                DbName
        );

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(driverString);
        config.setUsername((String) DbUsername);
        config.setPassword((String) DbPassword);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        if (instance == null) {
            synchronized (mutex) {
                if (instance == null)
                    instance = new ConnectionPool();
            }
        }
        return instance.ds.getConnection();
    }

    public synchronized static void close() {
        if (instance != null && instance.ds != null)
            instance.ds.close();
    }
}
