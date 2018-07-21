package in.nimbo;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import in.nimbo.dao.ChannelDAO;
import in.nimbo.dao.ConfigDAO;
import in.nimbo.dao.ItemDAO;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class FakeSiteCrawler extends SiteCrawler {
    URL urlAddress;

    public FakeSiteCrawler(URL urlAddress) {
        super(urlAddress);
        this.urlAddress = urlAddress;
    }

    public FakeSiteCrawler(URL urlAddress, ChannelDAO channelDAO, ItemDAO itemDAO, ConfigDAO configDAO) {
        super(urlAddress, channelDAO, itemDAO, configDAO);
        this.urlAddress = urlAddress;
    }

    @Override
    Connection.Response fetchSite(URL link) throws IOException {
        return new FakeResponse(this.urlAddress);
    }

    @Override
    SyndFeed getSyndFeed() throws FeedException, IOException {
        SyndFeedInput input = new SyndFeedInput();
        try {
            return input.build(Paths.get(urlAddress.toURI()).toFile());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

class FakeResponse implements Connection.Response {
    URL url;

    public FakeResponse(URL url) {
        this.url = url;
    }

    @Override
    public int statusCode() {
        return 0;
    }

    @Override
    public String statusMessage() {
        return null;
    }

    @Override
    public String charset() {
        return null;
    }

    @Override
    public Connection.Response charset(String charset) {
        return null;
    }

    @Override
    public String contentType() {
        return null;
    }

    @Override
    public Document parse() throws IOException {
        return Jsoup.parse(body());
    }

    @Override
    public String body() {
        try {
            return new String(Files.readAllBytes(Paths.get(url.toURI())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] bodyAsBytes() {
        return new byte[0];
    }

    @Override
    public Connection.Response bufferUp() {
        return null;
    }

    @Override
    public BufferedInputStream bodyStream() {
        return null;
    }

    @Override
    public URL url() {
        return null;
    }

    @Override
    public Connection.Response url(URL url) {
        return null;
    }

    @Override
    public Connection.Method method() {
        return null;
    }

    @Override
    public Connection.Response method(Connection.Method method) {
        return null;
    }

    @Override
    public String header(String name) {
        return null;
    }

    @Override
    public List<String> headers(String name) {
        return null;
    }

    @Override
    public Connection.Response header(String name, String value) {
        return null;
    }

    @Override
    public Connection.Response addHeader(String name, String value) {
        return null;
    }

    @Override
    public boolean hasHeader(String name) {
        return false;
    }

    @Override
    public boolean hasHeaderWithValue(String name, String value) {
        return false;
    }

    @Override
    public Connection.Response removeHeader(String name) {
        return null;
    }

    @Override
    public Map<String, String> headers() {
        return null;
    }

    @Override
    public Map<String, List<String>> multiHeaders() {
        return null;
    }

    @Override
    public String cookie(String name) {
        return null;
    }

    @Override
    public Connection.Response cookie(String name, String value) {
        return null;
    }

    @Override
    public boolean hasCookie(String name) {
        return false;
    }

    @Override
    public Connection.Response removeCookie(String name) {
        return null;
    }

    @Override
    public Map<String, String> cookies() {
        return null;
    }
}
