package edu.univ.erp.data.dao;

import edu.univ.erp.data.db.ERPDBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeDAO {

    public double getScore(int enrollmentId, String component) throws SQLException {
        String sql = "SELECT score FROM grades WHERE enrollment_id = ? AND component = ?";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setString(2, component);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("score");
                }
            }
        }
        return 0.0;
    }

    public void upsertScore(int enrollmentId, String component, double score) throws SQLException {
        String sql = "INSERT INTO grades (enrollment_id, component, score) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE score = VALUES(score)";

        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setString(2, component);
            ps.setDouble(3, score);
            ps.executeUpdate();
        }
    }

    public String getFinalGrade(int enrollmentId) throws SQLException {
        String sql = "SELECT final_grade FROM grades " +
                "WHERE enrollment_id = ? AND final_grade IS NOT NULL LIMIT 1";

        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("final_grade");
                }
            }
        }
        return null;
    }

    public void updateFinalGrade(int enrollmentId, String finalGrade) throws SQLException {
        String sql = "UPDATE grades SET final_grade = ? WHERE enrollment_id = ?";

        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, finalGrade);
            ps.setInt(2, enrollmentId);
            ps.executeUpdate();
        }
    }
}
