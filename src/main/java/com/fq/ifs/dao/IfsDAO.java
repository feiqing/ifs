package com.fq.ifs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;

/**
 * @author jifang
 * @since 2016/11/6 下午2:37.
 */
public class IfsDAO {

    private static DecimalFormat format = new DecimalFormat("######0.00");

    public int insertFile(String fileName, String fileLocation, long size) {
        try (Connection connection = ConnectionManager.getConnection("base.properties");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO t_file(name, location, size, create_time) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, fileName);
            statement.setString(2, fileLocation);

            String sizeStr = format.format(size * 1.0 / 1024 / 1024) + "m";
            statement.setString(3, sizeStr);
            statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
