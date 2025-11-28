package edu.univ.erp.data.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ERPDBConnect {

    private static final String URL  =
            "jdbc:mysql://localhost:3306/univ_erp?useSSL=false&serverTimezone=UTC";
    private static final String USER = "erp_user";
    private static final String PASS = "Parth2303@3110#";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
