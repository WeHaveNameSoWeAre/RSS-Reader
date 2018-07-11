package in.nimbo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException {
        Properties p = new Properties();
        p.put("bodyPattern", "test");
        p.put("adPatterns", "testPattern;teste");
        Path path = Paths.get("./tabnak.ir.properties");
        p.store(new FileOutputStream(path.toFile()), "Comments");

    }
}
