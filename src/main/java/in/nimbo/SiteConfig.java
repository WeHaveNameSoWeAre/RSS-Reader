package in.nimbo;

public interface SiteConfig {
    String getBodyPattern();

    String[] getAdPatterns();

    void save() throws Exception;
}
