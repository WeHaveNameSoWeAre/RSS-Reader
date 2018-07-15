package in.nimbo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FileSiteConfig implements SiteConfig {
    @Override
    public void save() throws Exception {

    }

    final Logger logger = LoggerFactory.getLogger(FileSiteConfig.class);
    private String siteName;
    private Properties properties = new Properties();

    public FileSiteConfig(String siteName) throws IOException {
        this.siteName = siteName;
        InputStream configFile;

        try {
            configFile = FileSiteConfig.class.getResourceAsStream("/siteConfigs/" + siteName + ".properties");
        } catch (Exception e) {
            throw new IOException("config file for site " + siteName + " not found");
        }

        properties.load(configFile);

        logger.info("Site configs loaded for site: {}", siteName);
    }

    @Override
    public String getBodyPattern() {
        return properties.getProperty("bodyPattern");
    }

    @Override
    public String[] getAdPatterns() {
        String adPatternsProperty = properties.getProperty("adPatterns");

        if (adPatternsProperty == null || adPatternsProperty.isEmpty())
            return new String[0];

        return adPatternsProperty.split(";");
    }
}
