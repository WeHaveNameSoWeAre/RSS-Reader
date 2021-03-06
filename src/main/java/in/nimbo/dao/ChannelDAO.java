package in.nimbo.dao;

import in.nimbo.model.Channel;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public interface ChannelDAO {
    void insertChannel(Channel channel) throws SQLException;

    int getChannelId(URL rssLink) throws SQLException;

    void updateChannelLastDate(Channel channel) throws SQLException;

    List<Channel> getAllChannels() throws SQLException;

    List<Channel> getChannelsUpdatedBefore(int minutes) throws SQLException;
}
