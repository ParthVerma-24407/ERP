package edu.univ.erp.data.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AuthDBConnect {

    private static final String URL = "jdbc:mysql://localhost:3306/auth_db";
    private static final String USER = "erp_user";
    private static final String PASSWORD = "Parth2303@3110#";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
