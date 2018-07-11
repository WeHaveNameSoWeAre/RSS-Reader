package in.nimbo;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FileConfig implements Config {
    private String siteName;
    private Properties properties = new Properties();

    public FileConfig(String siteName) throws IOException {
        this.siteName = siteName;
        InputStream configFile = FileConfig.class.getResourceAsStream("/siteConfigs" + siteName + ".properties");
        properties.load(configFile);
    }

    @Override
    public String getBodyPattern() {
        return properties.getProperty("bodyPattern");
    }

    @Override
    public String[] getAdPatterns() {
        String adPatternsProperty = properties.getProperty("adPatterns");

        if(adPatternsProperty == null)
            return new String[0];

        return adPatternsProperty.split(";");
    }
}
