package in.nimbo.impl.mysql;

import in.nimbo.dao.ChannelDAO;
import in.nimbo.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class MysqlChannelDAOImpl implements ChannelDAO {
    private final static Logger logger = LoggerFactory.getLogger(MysqlChannelDAOImpl.class);

    @Override
    public void insertChannel(Channel channel) throws SQLException {
        {

            try (
                    Connection connection = getConnection();
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

    }

    @Override
    public int getChannelId(URL rssLink) throws SQLException {
        {
            try (
                    Connection connection = getConnection();
                    PreparedStatement getChannelIdStatement = connection.prepareStatement(
                            "SELECT id FROM channels WHERE rssLinkHash = SHA1(?)"
                    )
            ) {
                getChannelIdStatement.setString(1, rssLink.toExternalForm());
                try (ResultSet resultSet = getChannelIdStatement.executeQuery()) {
                    if (resultSet.next())
                        return resultSet.getInt("id");
                    else
                        throw new SQLException("Channel doesn't Exist!");
                }
            }
        }
    }

    @Override
    public List<Channel> getAllChannels() throws SQLException {
        {
            try (
                    Connection connection = getConnection();
                    PreparedStatement selectAllChannelsStatement = connection.prepareStatement("SELECT * FROM channels");
                    ResultSet resultSet = selectAllChannelsStatement.executeQuery()
            ) {
                ArrayList<Channel> channels = new ArrayList<>();
                while (resultSet.next()) {
                    try {
                        channels.add(
                                new Channel(
                                        resultSet.getInt("id"),
                                        resultSet.getString("name"),
                                        new URL(resultSet.getString("rssLink")),
                                        resultSet.getDate("lastUpdate"),
                                        resultSet.getString("link")
                                )
                        );
                    } catch (SQLException | MalformedURLException e) {
                        logger.warn("error during getting channels", e);
                    }
                }
                return channels;
            }
        }
    }

    @Override
    public List<Channel> getChannelsUpdatedBefore(int minutes) throws SQLException {
        try (
                Connection connection = getConnection();
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
                return channels;
            }
        }
    }

    protected Connection getConnection() throws SQLException {
        return MysqlConnectionPool.getConnection();
    }
}
