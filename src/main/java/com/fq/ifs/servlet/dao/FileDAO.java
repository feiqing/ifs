package com.fq.ifs.servlet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author jifang
 * @since 2016/11/6 下午2:37.
 */
public class FileDAO {

    public int insertFile(String fileName, String fileLocation) {
        try (Connection connection = ConnectionManager.getConnection("base.properties");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO t_file(name, location) VALUES (?, ?)")) {
            statement.setString(1, fileName);
            statement.setString(2, fileLocation);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
