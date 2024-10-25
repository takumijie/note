package com.msb.note.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBUtil {

//  创建配置文件对象
    private  static Properties properties = new Properties();

//加载配置文件&驱动
static {
    try {
        InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
        properties.load(in);
        Class.forName(properties.getProperty("JDBC.DRIVER"));
    } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException(e);
    }
}

//获取连接
public static Connection getConnection(){
    try {
        Connection conn = null;
        String url = properties.getProperty("JDBC.URL");
        String username = properties.getProperty("JDBC.USERNAME");
        String password = properties.getProperty("JDBC.PASSWORD");
        conn = DriverManager.getConnection(url,username,password);
        return conn;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    }
//关闭连接
    public static void closeConnection(Connection conn, PreparedStatement ps,ResultSet rs){

            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

}


