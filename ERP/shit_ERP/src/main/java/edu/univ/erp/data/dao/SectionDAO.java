package edu.univ.erp.data.dao;

import edu.univ.erp.data.db.ERPDBConnect;
import edu.univ.erp.domain.Section;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {


    public List<Section> findAll() throws SQLException {
        String sql = "SELECT section_id, course_code, instructor_id, capacity, enrolled_count FROM sections";
        List<Section> list = new ArrayList<>();
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Section> findByInstructor(int instructorUserId) throws SQLException {
        String sql = "SELECT section_id, course_code, instructor_id, capacity, enrolled_count " +
                "FROM sections WHERE instructor_id = ?";
        List<Section> list = new ArrayList<>();
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private Section mapRow(ResultSet rs) throws SQLException {
        return new Section(
                rs.getInt("section_id"),
                rs.getString("course_code"),
                rs.getInt("instructor_id"),
                rs.getInt("capacity"),
                rs.getInt("enrolled_count")
        );
    }

    public Section findById(int sectionId) throws SQLException {
        String sql = "SELECT section_id, course_code, instructor_id, capacity, enrolled_count " +
                "FROM sections WHERE section_id = ?";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public void incrementEnrollment(int sectionId) throws SQLException {
        String sql = "UPDATE sections SET enrolled_count = enrolled_count + 1 WHERE section_id = ?";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ps.executeUpdate();
        }
    }

    public void decrementEnrollment(int sectionId) throws SQLException {
        String sql = "UPDATE sections SET enrolled_count = GREATEST(enrolled_count - 1, 0) WHERE section_id = ?";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ps.executeUpdate();
        }
    }

    // 🔹 Now takes sectionId explicitly and inserts it
    public void createSection(int sectionId, String courseCode, int instructorId, int capacity) throws SQLException {
        String sql = "INSERT INTO sections (section_id, course_code, instructor_id, capacity, enrolled_count) " +
                "VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ps.setString(2, courseCode);
            ps.setInt(3, instructorId);
            ps.setInt(4, capacity);
            ps.executeUpdate();
        }
    }
}
