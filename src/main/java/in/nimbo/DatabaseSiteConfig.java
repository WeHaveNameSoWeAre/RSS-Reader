package in.nimbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class DatabaseSiteConfig implements SiteConfig {
    final Logger logger = LoggerFactory.getLogger(DatabaseSiteConfig.class);
    private Integer id;
    private String siteLink;
    private String bodyPattern;
    private String[] adPatterns;
    private DatabaseHandler db = DatabaseHandler.getInstance();

    public DatabaseSiteConfig(String siteLink) {
        this.siteLink = siteLink;
        try {
            Object[] configs = db.getConfig(siteLink);
            id = (Integer) configs[0];
            bodyPattern = (String) configs[1];

            if (configs[2] != null && !((String) configs[2]).isEmpty())
                adPatterns = ((String) configs[2]).split(";");
            else
                adPatterns = null;

        } catch (SQLException e) {
            logger.warn("config for {} not found!", siteLink);
            throw new IllegalStateException("config for " + siteLink + " not found!", e);
        }
    }

    public DatabaseSiteConfig(String siteLink, String bodyPattern, String[] adPatterns) {
        this.siteLink = siteLink;
        this.bodyPattern = bodyPattern;
        this.adPatterns = adPatterns;

        try {
            Object[] configs = db.getConfig(siteLink);
            id = (Integer) configs[0];
        } catch (SQLException e) {
            logger.warn("SQL error detected", e);
            id = null;
        } catch (IllegalStateException e) {
            id = null;
        }
    }

    @Override
    public String getBodyPattern() {
        return bodyPattern;
    }

    @Override
    public String[] getAdPatterns() {
        if (adPatterns == null)
            return new String[0];

        return adPatterns;
    }

    @Override
    public void save() throws SQLException {
        String adPatterns = this.adPatterns != null ? String.join(";", this.adPatterns) : null;
        if (id == null) {
            try {
                db.insertConfig(siteLink, bodyPattern, adPatterns);
            } catch (SQLException e) {
                logger.error("cant add Config File for site {}", siteLink);
                throw new SQLException(e);
            }
        } else {
            try {
                db.updateConfig(id, bodyPattern, adPatterns);
            } catch (SQLException e) {
                logger.warn("cant add Config File for site {}", siteLink);
                throw new SQLException(e);
            }
        }
    }
}
