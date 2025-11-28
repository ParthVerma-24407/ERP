package edu.univ.erp.data.dao;

import edu.univ.erp.data.db.ERPDBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsDAO {

    /**
     * Fetch a setting value by key.
     * @param key setting_key column value
     * @return value or null if not found
     */
    public String getSetting(String key) throws SQLException {
        final String sql = "SELECT setting_value FROM settings WHERE setting_key = ?";

        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("setting_value");
                }
            }
        }

        return null; // setting not found
    }

    /**
     * Insert or update a setting safely.
     * If the key exists, its value is updated.
     */
    public void upsertSetting(String key, String value) throws SQLException {
        final String sql =
                "INSERT INTO settings (setting_key, setting_value) " +
                        "VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";

        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        }
    }
}
