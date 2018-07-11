package in.nimbo;

import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.*;

public class FileConfigTest {
    @Test
    public void getBodyPattern() throws IOException {
        FileConfig config = new FileConfig("test.ir");
        assertEquals("test",config.getBodyPattern());
    }

    @Test
    public void getAdPatternsSingle() throws IOException {
        FileConfig config = new FileConfig("test.ir");
        String[] answer = new String[]{"testPattern"};
        assertArrayEquals(answer,config.getAdPatterns());
    }

    @Test
    public void getAdPatternsMultiple() throws IOException {
        FileConfig config = new FileConfig("test2.ir");
        String[] answer = new String[]{"testPattern","test2"};
        assertArrayEquals(answer,config.getAdPatterns());
    }
}