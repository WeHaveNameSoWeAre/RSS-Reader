package in.nimbo.mock;

import in.nimbo.dao.ConfigDAO;
import in.nimbo.model.Config;

import java.sql.SQLException;

public class ConfigDAOFake implements ConfigDAO {
    @Override
    public Config getConfig(String link) throws SQLException {
        return null;
    }

    @Override
    public void insertConfig(Config config) throws SQLException {

    }

    @Override
    public void updateConfig(Config config) throws SQLException {

    }

    @Override
    public void insertOrUpdateConfig(Config config) throws SQLException {

    }
}
