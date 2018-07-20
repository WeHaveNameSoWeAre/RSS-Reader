package in.nimbo.impl.mysql;

import in.nimbo.dao.ChannelDAO;
import in.nimbo.dao.ItemDAO;
import in.nimbo.model.Channel;
import in.nimbo.model.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class MysqlItemDAOImplTest {
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private Connection connection;
    private ItemDAO itemDAO = new MysqlItemDAOImpl();

    @BeforeClass
    public static void beforeClass() throws MalformedURLException, SQLException {
        Connection connection = MysqlConnectionPool.getConnection();

        PreparedStatement preparedStatement;
        connection.setAutoCommit(false);
        preparedStatement = connection.prepareStatement("SET foreign_key_checks = 0");
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

        ChannelDAO channelDAO = new MysqlChannelDAOImpl();
        Channel channel = new Channel(3, "tabnak", new URL("http://www.tabnak.ir/fa/rss/allnews"), new Date(), "www.tabnak.ir");
        channelDAO.insertChannel(channel);
    }

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
    public void checkItemExistsTrue() throws MalformedURLException, SQLException {
        Item item = new Item(1, "تیتر", new URL("http://www.varzesh3.com/rss/all"), "desc", "متن", new Date(), 3);
        itemDAO.insertItem(item);
        assertTrue(itemDAO.checkItemExists(item));
    }

    @Test
    public void checkItemExistsFalse() throws MalformedURLException, SQLException {
        Item item = new Item(1, "تیتر", new URL("http://www.varzesh3.com/rss/all"), "desc", "متن", new Date(), 3);
        assertFalse(itemDAO.checkItemExists(item));
    }

    @Test
    public void insertItemWithNullId() throws MalformedURLException, SQLException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, -50);
        Date lastUpdate = calendar.getTime();

        Item item = new Item(null, "تیتر", new URL("http://www.varzesh3.com/rss/all"), "desc", "متن", lastUpdate, 3);
        itemDAO.insertItem(item);

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM items");
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            assertEquals(1, resultSet.getInt("id"));
            assertEquals("تیتر", resultSet.getString("title"));
            assertEquals("http://www.varzesh3.com/rss/all", resultSet.getString("link"));
            assertEquals("desc", resultSet.getString("desc"));
            assertEquals("متن", resultSet.getString("text"));
            assertEquals(new Timestamp(lastUpdate.getTime()), resultSet.getTimestamp("date"));
            assertEquals(3, resultSet.getInt("channelId"));
        } else
            fail();

        if (resultSet.next())
            fail();
    }

    @Test
    public void insertItem() throws MalformedURLException, SQLException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, -50);
        Date lastUpdate = calendar.getTime();

        Item item = new Item(5, "تیتر", new URL("http://www.varzesh3.com/rss/all"), "desc", "متن", lastUpdate, 3);
        itemDAO.insertItem(item);

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM items");
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            assertEquals(5, resultSet.getInt("id"));
            assertEquals("تیتر", resultSet.getString("title"));
            assertEquals("http://www.varzesh3.com/rss/all", resultSet.getString("link"));
            assertEquals("desc", resultSet.getString("desc"));
            assertEquals("متن", resultSet.getString("text"));
            assertEquals(new Timestamp(lastUpdate.getTime()), resultSet.getTimestamp("date"));
            assertEquals(3, resultSet.getInt("channelId"));
        } else
            fail();

        if (resultSet.next())
            fail();
    }

    @Test
    public void getLastNewsOfChannelWithMoreNumRows() throws SQLException, MalformedURLException {
        Item item = new Item(null, "تیتر", new URL("http://www.varzesh3.com/rss/all"), "desc", "متن", new Date(), 3);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, -50);
        Date lastUpdate = calendar.getTime();
        Item item2 = new Item(null, "Title", new URL("http://www.tabnak.ir/fa/rss/allnews"), "توضیح", "text", lastUpdate, 3);
        itemDAO.insertItem(item);
        itemDAO.insertItem(item2);

        List<Item> items = itemDAO.getLastNewsOfChannel(2, 3);

        if (items.size() != 2) {
            fail();
        }
    }

    @Test
    public void getLastNewsOfChannelWithLessNumRows() throws SQLException, MalformedURLException {
        Item item = new Item(null, "تیتر", new URL("http://www.varzesh3.com/rss/all"), "desc", "متن", new Date(), 3);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, -50);
        Date lastUpdate = calendar.getTime();
        Item item2 = new Item(null, "Title", new URL("http://www.tabnak.ir/fa/rss/allnews"), "توضیح", "text", lastUpdate, 3);
        itemDAO.insertItem(item);

        List<Item> items = itemDAO.getLastNewsOfChannel(1, 3);


        if (items.size() != 1)
            fail();
    }

    @Test
    public void getNumberOfItemsInChannelPerDay() throws MalformedURLException, SQLException {
        Item item = new Item(null, "تیتر", new URL("http://www.varzesh3.com/rss/all"), "desc", "متن", new Date(), 3);
        Item item2 = new Item(null, "تیتر", new URL("https://www.mehrnews.com/rss?pl=8"), "desc", "متن", new Date(), 3);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -50);
        Date lastUpdate = calendar.getTime();
        Item item3 = new Item(null, "Title", new URL("http://www.tabnak.ir/fa/rss/allnews"), "توضیح", "text", lastUpdate, 3);
        itemDAO.insertItem(item);
        itemDAO.insertItem(item2);
        itemDAO.insertItem(item3);

        int number = itemDAO.getNumberOfItemsInChannelPerDay(new Date(), 3);

        if (number != 2)
            fail();
    }

    @Test
    public void searchByTitle() throws SQLException, MalformedURLException {
        Item item0 = new Item(1, "تیترtitle", new URL("http://www.varzesh3.com/rss/all"), "desc", "متن", new Date(), 3);
        Item item2 = new Item(2, "sf", new URL("https://www.mehrnews.com/rss?pl=8"), "desc", "متن", new Date(), 3);
        itemDAO.insertItem(item0);
        itemDAO.insertItem(item2);

        List<Item> items = itemDAO.searchByTitle("تیتر");
        if (items.size() == 1) {
            Item item = items.get(0);
            assertEquals(1, (int) item.getId());
        } else
            fail();
    }

    @Test
    public void searchByText() throws MalformedURLException, SQLException {
        Item item0 = new Item(1, "تیترtitle", new URL("http://www.varzesh3.com/rss/all"), "desc", "textمتن", new Date(), 3);
        Item item2 = new Item(2, "sf", new URL("https://www.mehrnews.com/rss?pl=8"), "desc", "sfgdfg", new Date(), 3);
        itemDAO.insertItem(item0);
        itemDAO.insertItem(item2);

        List<Item> items = itemDAO.searchByText("متن");
        if (items.size() == 1) {
            Item item = items.get(0);
            assertEquals(1, (int) item.getId());
        } else
            fail();
    }
}