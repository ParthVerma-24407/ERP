package edu.univ.erp.data.dao;

import edu.univ.erp.data.db.ERPDBConnect;
import edu.univ.erp.domain.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public boolean exists(int studentId, int sectionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public void addEnrollment(int studentId, int sectionId) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'ENROLLED')";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ps.executeUpdate();
        }
    }

    public void removeEnrollment(int studentId, int sectionId) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ps.executeUpdate();
        }
    }

    public List<Enrollment> findByStudent(int studentId) throws SQLException {
        String sql = "SELECT enrollment_id, student_id, section_id FROM enrollments WHERE student_id = ?";
        List<Enrollment> list = new ArrayList<>();
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getInt("student_id"),
                            rs.getInt("section_id")
                    ));
                }
            }
        }
        return list;
    }

    public List<Enrollment> findBySection(int sectionId) throws SQLException {
        String sql = "SELECT enrollment_id, student_id, section_id FROM enrollments WHERE section_id = ?";
        List<Enrollment> list = new ArrayList<>();
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getInt("student_id"),
                            rs.getInt("section_id")
                    ));
                }
            }
        }
        return list;
    }
}
