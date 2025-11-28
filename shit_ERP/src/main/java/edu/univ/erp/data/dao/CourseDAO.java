package edu.univ.erp.data.dao;

import edu.univ.erp.data.db.ERPDBConnect;
import edu.univ.erp.domain.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> findAll() throws SQLException {
        String sql = "SELECT course_code, title, credits FROM courses";
        List<Course> list = new ArrayList<>();
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Course(
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getInt("credits")
                ));
            }
        }
        return list;
    }

    public Course findByCode(String code) throws SQLException {
        String sql = "SELECT course_code, title, credits FROM courses WHERE course_code = ?";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                            rs.getString("course_code"),
                            rs.getString("title"),
                            rs.getInt("credits")
                    );
                }
            }
        }
        return null;
    }

    public void createCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_code, title, credits) VALUES (?, ?, ?)";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getTitle());
            ps.setInt(3, course.getCredits());
            ps.executeUpdate();
        }
    }
}
