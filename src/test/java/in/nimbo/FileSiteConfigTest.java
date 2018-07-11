package in.nimbo;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class FileSiteConfigTest {
    @Test
    public void getBodyPattern() throws IOException {
        FileSiteConfig config = new FileSiteConfig("test.ir");
        assertEquals("test",config.getBodyPattern());
    }

    @Test
    public void getAdPatternsSingle() throws IOException {
        FileSiteConfig config = new FileSiteConfig("test.ir");
        String[] answer = new String[]{"testPattern"};
        assertArrayEquals(answer,config.getAdPatterns());
    }

    @Test
    public void getAdPatternsNone() throws IOException {
        FileSiteConfig config = new FileSiteConfig("test3.ir");
        String[] answer = new String[0];
        assertArrayEquals(answer,config.getAdPatterns());
    }

    @Test
    public void getAdPatternsMultiple() throws IOException {
        FileSiteConfig config = new FileSiteConfig("test2.ir");
        String[] answer = new String[]{"testPattern","test2"};
        assertArrayEquals(answer,config.getAdPatterns());
    }
}