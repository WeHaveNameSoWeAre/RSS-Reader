package in.nimbo.impl.mysql;

import in.nimbo.dao.ConfigDAO;
import in.nimbo.model.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class MysqlConfigDAOImpl implements ConfigDAO {
    private final static Logger logger = LoggerFactory.getLogger(MysqlConfigDAOImpl.class);


    @Override
    public Config getConfig(String link) throws SQLException {
        {
            try (
                    Connection connection = getConnection();
                    PreparedStatement selectConfigStatement = connection.prepareStatement(
                            "SELECT * FROM configs WHERE linkHash = SHA1(?)"
                    )
            ) {
                selectConfigStatement.setString(1, link);
                try (ResultSet resultSet = selectConfigStatement.executeQuery()) {
                    if (resultSet.next())
                        return new Config(
                                resultSet.getInt("id"),
                                resultSet.getString("link"),
                                resultSet.getString("bodyPattern"),
                                resultSet.getString("adPatterns")
                        );
                    else
                        throw new IllegalStateException("There were no config for that site");
                }
            }
        }
    }

    @Override
    public void insertConfig(Config config) throws SQLException {
        {
            try (
                    Connection connection = getConnection();
                    PreparedStatement insertConfigStatement = connection.prepareStatement(
                            "INSERT INTO configs(link, bodyPattern, adPatterns, linkHash) VALUES (?,?,?,SHA1(?))"
                    )
            ) {
                insertConfigStatement.setString(1, config.getLink());
                insertConfigStatement.setString(2, config.getBodyPattern());
                insertConfigStatement.setString(3, config.getBodyPattern());
                insertConfigStatement.setString(4, config.getLink());

                insertConfigStatement.executeUpdate();
            }
        }

    }

    @Override
    public void updateConfig(Config config) throws SQLException {
        {
            if (config.getId() == null)
                throw new IllegalArgumentException("id is null");
            try (
                    Connection connection = getConnection();
                    PreparedStatement updateConfigStatement = connection.prepareStatement(
                            "UPDATE configs SET bodyPattern = ?,adPatterns = ? WHERE id = ?"
                    )
            ) {
                updateConfigStatement.setString(1, config.getBodyPattern());
                updateConfigStatement.setString(2, config.getAdPatternsString());
                updateConfigStatement.setInt(3, config.getId());
                updateConfigStatement.executeUpdate();
            }
        }

    }

    @Override
    public void insertOrUpdateConfig(Config config) throws SQLException {
        Objects.requireNonNull(config);
        Objects.requireNonNull(config.getLink());
        Integer id;
        try {
            Config siteConfig = getConfig(config.getLink());
            id = siteConfig.getId();
        } catch (IllegalStateException e) {
            id = null;
        }

        if (id != null){
            config.setId(id);
            updateConfig(config);
        }else{
            insertConfig(config);
        }
    }

    protected Connection getConnection() throws SQLException {
        return MysqlConnectionPool.getConnection();
    }
}
