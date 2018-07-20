package in.nimbo.impl.mysql;

import in.nimbo.dao.ChannelDAO;
import in.nimbo.model.Channel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MysqlChannelDAOImplTest {
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private Connection connection;
    private ChannelDAO channelDAO = new MysqlChannelDAOImpl();

    @Before
    public void setUp() throws Exception {
        connection = MysqlConnectionPool.getConnection();

        PreparedStatement preparedStatement;
        connection.setAutoCommit(false);
        preparedStatement = connection.prepareStatement("SET foreign_key_checks = 0");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        preparedStatement = connection.prepareStatement("TRUNCATE TABLE items");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        preparedStatement = connection.prepareStatement("TRUNCATE TABLE channels");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        preparedStatement = connection.prepareStatement("SET foreign_key_checks = 1");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.commit();
        connection.setAutoCommit(true);
    }

    @After
    public void tearDown() throws Exception {
        if (resultSet != null)
            resultSet.close();

        if (preparedStatement != null)
            preparedStatement.close();

        if (connection != null)
            connection.close();
    }


    @Test
    public void insertChannel() throws SQLException, MalformedURLException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND,0);
        Date lastUpdate = calendar.getTime();
        Channel channel = new Channel(3, "tabnak", new URL("http://www.tabnak.ir/fa/rss/allnews"), lastUpdate, "www.tabnak.ir");
        channelDAO.insertChannel(channel);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM channels");
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            assertEquals(3, resultSet.getInt("id"));
            assertEquals("tabnak", resultSet.getString("name"));
            assertEquals("http://www.tabnak.ir/fa/rss/allnews", resultSet.getString("rssLink"));
            assertEquals(new Timestamp(lastUpdate.getTime()), resultSet.getTimestamp("lastUpdate"));
            assertEquals("www.tabnak.ir", resultSet.getString("link"));
        } else
            fail();

        if (resultSet.next())
            fail();
    }

    @Test
    public void insertChannelWithNullId() throws MalformedURLException, SQLException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND,0);
        Date lastUpdate = calendar.getTime();
        Channel channel = new Channel(null, "mehrnews", new URL("https://www.mehrnews.com/rss?pl=8/fa/rss/allnews"), lastUpdate, "www.mehrnews.com");
        channelDAO.insertChannel(channel);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM channels");
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            assertEquals(1, resultSet.getInt("id"));
            assertEquals("mehrnews", resultSet.getString("name"));
            assertEquals("https://www.mehrnews.com/rss?pl=8/fa/rss/allnews", resultSet.getString("rssLink"));
            assertEquals(new Timestamp(lastUpdate.getTime()), resultSet.getTimestamp("lastUpdate"));
            assertEquals("www.mehrnews.com", resultSet.getString("link"));
        } else
            fail();

        if (resultSet.next())
            fail();
    }


    @Test
    public void getChannelId() throws MalformedURLException, SQLException {
        Channel channel = new Channel(1, "isna", new URL("https://www.isna.ir/rss"), new Date(), "www.isna.ir");
        channelDAO.insertChannel(channel);
        int channelId = channelDAO.getChannelId(channel.getRssLink());
        assertEquals(1, channelId);
    }


    @Test
    public void getAllChannels() throws MalformedURLException, SQLException {
        Channel channel1 = new Channel(null, "isna", new URL("https://www.isna.ir/rss"), new Date(), "www.isna.ir");
        Channel channel2 = new Channel(null, "irna", new URL("http://www.irna.ir/fa/rss.aspx?kind=-1&area=0"), new Date(), "www.irna.ir");
        channelDAO.insertChannel(channel1);
        channelDAO.insertChannel(channel2);
        List<Channel> allChannels = channelDAO.getAllChannels();
        Channel[] expectedChannels = new Channel[]{channel1, channel2};
        for (int i = 0; i < 2; i++) {
            assertEquals(expectedChannels[i].getName(), allChannels.get(i).getName());
            assertEquals(expectedChannels[i].getLink(), allChannels.get(i).getLink());
            assertEquals(expectedChannels[i].getRssLink(), allChannels.get(i).getRssLink());
        }
    }

    @Test
    public void getChannelsUpdatedBefore() throws MalformedURLException, SQLException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -1);
        Date channel0LastUpdate = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);
        Date channel1astUpdate = calendar.getTime();
        Channel channel0 = new Channel(null, "tabnak", new URL("http://www.tabnak.ir"), channel0LastUpdate, "www.tabnak.ir");
        Channel channel1 = new Channel(null, "isna", new URL("https://www.isna.ir"), channel1astUpdate, "www.isna.ir");
        channelDAO.insertChannel(channel0);
        channelDAO.insertChannel(channel1);
        List<Channel> channelsUpdatedBefore = channelDAO.getChannelsUpdatedBefore(3);
        if (channelsUpdatedBefore.size() == 1) {
            assertEquals(channel1.getName(), channelsUpdatedBefore.get(0).getName());
        } else
            fail();
    }

    @AfterClass
    public static void afterClass(){
        MysqlConnectionPool.close();
    }
}