package in.nimbo;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import in.nimbo.mock.ChannelsDAOFake;
import in.nimbo.mock.ConfigDAOFake;
import in.nimbo.mock.ItemDAOFake;
import org.jsoup.Connection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SiteCrawlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void update() {
        //this test is not Complete.
//        URL url = SiteCrawlerTest.class.getResource("/test.xml");
//        ItemDAOFake itemDAO = new ItemDAOFake();
//        SiteCrawler crawler = new FakeSiteCrawler(url,new ChannelsDAOFake(), itemDAO, new ConfigDAOFake());
//        crawler.update();
    }

    @Test
    public void extractTextByPattern() throws IOException, BoilerpipeProcessingException {
        URL url = SiteCrawlerTest.class.getResource("/text.html");
        SiteCrawler crawler = new FakeSiteCrawler(url);
        assertNotNull(crawler.extractTextByPattern(url,".gutter_news>div.body",new String[0]));
    }

    @Test
    public void extractTextAutomatically() throws IOException, BoilerpipeProcessingException {
        URL url = SiteCrawlerTest.class.getResource("/text.html");
        SiteCrawler crawler = new FakeSiteCrawler(url);
        assertNotNull(crawler.extractTextAutomatically(url));
    }

    @Test
    public void fetchSite() throws IOException {
        URL url = new URL("http://www.tabnak.ir/fa/news/818329/%D8%B4%D8%A7%DB%8C%D8%B9%E2%80%8C%D8%AA%D8%B1%DB%8C%D9%86-%D8%B9%D9%84%D8%A7%D8%A6%D9%85-%D8%AC%D8%B3%D9%85%DB%8C%D9%90-%D8%A7%D9%81%D8%B3%D8%B1%D8%AF%DA%AF%DB%8C");
        SiteCrawler crawler = new SiteCrawler(url);
        Connection.Response response = crawler.fetchSite(url);
        assertEquals(200, response.statusCode());
    }
}