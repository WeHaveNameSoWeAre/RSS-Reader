package in.nimbo.impl.mysql;

import in.nimbo.dao.ConfigDAO;
import in.nimbo.model.Config;

import java.sql.SQLException;

public class MysqlConfigDAOImpl implements ConfigDAO {
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
}
