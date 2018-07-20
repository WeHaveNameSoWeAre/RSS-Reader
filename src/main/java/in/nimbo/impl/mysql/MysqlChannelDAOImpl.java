package in.nimbo.impl.mysql;

import in.nimbo.dao.ChannelDAO;
import in.nimbo.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
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
                            "INSERT INTO `channels` (id,name, rssLink, rssLinkHash, link,lastUpdate) VALUES (?,?,?,SHA1(?),?,?)")
            ) {

                insertChannelStatement.setObject(1, channel.getId());
                insertChannelStatement.setString(2, channel.getName());
                insertChannelStatement.setString(3, channel.getRssLink().toExternalForm());
                insertChannelStatement.setString(4, channel.getRssLink().toExternalForm());
                insertChannelStatement.setString(5,
                        channel.getLink() != null ? channel.getLink() : channel.getRssLink().getHost()
                );
                insertChannelStatement.setTimestamp(6,
                        channel.getLastUpdate() != null ? new Timestamp(channel.getLastUpdate().getTime()) : null
                );
                insertChannelStatement.executeUpdate();

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
    public void updateChannelLastDate(Channel channel) throws SQLException {
        if (channel.getId() == null)
            throw new IllegalArgumentException("id is null");

        try (
                Connection connection = getConnection();
                PreparedStatement updateChannelDate = connection.prepareStatement(
                        "UPDATE channels SET lastUpdate = ? WHERE id = ?"
                )
        ) {
            updateChannelDate.setTimestamp(1, new Timestamp(channel.getLastUpdate().getTime()));
            updateChannelDate.setInt(2, channel.getId());
            updateChannelDate.executeUpdate();
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

    protected Connection getConnection() throws SQLException {
        return MysqlConnectionPool.getConnection();
    }
}
