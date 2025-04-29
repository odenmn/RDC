package com.xjx.example.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtil {
    private static DataSource ds;

    static {
        try {
            Properties pro = new Properties();
            pro.load(JDBCUtil.class.getClassLoader().getResourceAsStream("druid.properties"));
            ds = DruidDataSourceFactory.createDataSource(pro);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 开启事务
    public static void beginTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 提交事务
    public static void commitTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 回滚事务
    public static void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 执行查询操作
    public static ResultSet executeQuery(Connection connection, String sql, Object... params) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            beginTransaction(connection); // 开启事务
            pstmt = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            rollbackTransaction(connection); // 出现异常时回滚事务
            e.printStackTrace();
        }
        return rs;
    }

    // 执行更新操作（插入、更新、删除）
    public static int executeUpdate(Connection connection, String sql, Object... params) {
        PreparedStatement pstmt = null;
        int rows = 0;
        try {
            beginTransaction(connection); // 开启事务
            pstmt = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            rows = pstmt.executeUpdate();
            commitTransaction(connection); // 操作成功时提交事务
        } catch (SQLException e) {
            rollbackTransaction(connection); // 出现异常时回滚事务
            e.printStackTrace();
        } finally {
            closeResources(pstmt, null);
        }
        return rows;
    }

    // 关闭资源
    public static void closeResources(PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            // 不再在这里关闭连接，因为事务可能需要保持连接打开
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
