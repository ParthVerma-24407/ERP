package edu.univ.erp.data.dao;

import edu.univ.erp.data.db.AuthDBConnect;
import edu.univ.erp.domain.Role;

import java.sql.*;

public class AuthDAO {

    public static class AuthRecord {
        public int userId;
        public String username;
        public Role role;
        public String passwordHash;
        public String status;
    }

    public AuthRecord findByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, role, password_hash, status FROM users_auth WHERE username = ?";
        try (Connection conn = AuthDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                AuthRecord rec = new AuthRecord();
                rec.userId = rs.getInt("user_id");
                rec.username = rs.getString("username");
                rec.role = Role.valueOf(rs.getString("role"));
                rec.passwordHash = rs.getString("password_hash");
                rec.status = rs.getString("status");
                return rec;
            }
        }
    }

    public void updateLastLogin(int userId) throws SQLException {
        String sql = "UPDATE users_auth SET last_login = NOW() WHERE user_id = ?";
        try (Connection conn = AuthDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public int createUser(String username, Role role, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users_auth (username, role, password_hash, status, last_login, prev_create, updated_at) " +
                "VALUES (?, ?, ?, 'ACTIVE', NULL, NOW(), NOW())";
        try (Connection conn = AuthDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, role.name());
            ps.setString(3, passwordHash);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to create user_auth");
    }
}
