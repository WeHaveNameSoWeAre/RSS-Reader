package in.nimbo.dao;

import in.nimbo.model.Config;

import java.sql.SQLException;

public interface ConfigDAO {
    Config getConfig(String link) throws SQLException;

    void insertConfig(Config config) throws SQLException;

    void updateConfig(Config config) throws SQLException;

    void insertOrUpdateConfig(Config config) throws SQLException;
}
