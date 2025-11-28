package edu.univ.erp.data.dao;

import edu.univ.erp.data.db.ERPDBConnect;
import edu.univ.erp.domain.Instructor;

import java.sql.*;

public class InstructorDAO {

    public Instructor findByUserId(int userId) throws SQLException {
        String sql = "SELECT user_id, department FROM instructors WHERE user_id = ?";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Instructor(
                            rs.getInt("user_id"),
                            rs.getString("department")
                    );
                }
            }
        }
        return null;
    }

    public void createInstructorProfile(Instructor ins) throws SQLException {
        String sql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ins.getUserId());
            ps.setString(2, ins.getDepartment());
            ps.executeUpdate();
        }
    }
}
