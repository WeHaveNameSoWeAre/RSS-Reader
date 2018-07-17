package in.nimbo.impl.mysql;

import in.nimbo.dao.ChannelDAO;
import in.nimbo.model.Channel;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class MysqlChannelDAOImpl implements ChannelDAO {
    @Override
    public void insertChannel(Channel channel) throws SQLException {

    }

    @Override
    public int getChannelId(URL rssLink) throws SQLException {
        return 0;
    }

    @Override
    public List<Channel> getAllChannels() throws SQLException {
        return null;
    }

    @Override
    public Channel[] getChannelsUpdatedBefore(int minutes) throws SQLException {
        return new Channel[0];
    }
}
