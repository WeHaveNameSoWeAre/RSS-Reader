package in.nimbo.mock;

import in.nimbo.dao.ChannelDAO;
import in.nimbo.model.Channel;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class ChannelsDAOFake implements ChannelDAO {
    private static boolean insertChannelCalled = false;
    private static boolean getChannelCalled = false;
    @Override
    public void insertChannel(Channel channel) throws SQLException {
        insertChannelCalled = true;
    }

    @Override
    public int getChannelId(URL rssLink) throws SQLException {
        getChannelCalled = true;
        return 0;
    }

    @Override
    public void updateChannelLastDate(Channel channel) throws SQLException {

    }

    @Override
    public List<Channel> getAllChannels() throws SQLException {
        return null;
    }

    @Override
    public List<Channel> getChannelsUpdatedBefore(int minutes) throws SQLException {
        return null;
    }
}
