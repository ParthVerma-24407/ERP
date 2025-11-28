package edu.univ.erp.data.dao;

import edu.univ.erp.data.db.ERPDBConnect;
import edu.univ.erp.domain.Student;

import java.sql.*;

public class StudentDAO {

    public Student findByUserId(int userId) throws SQLException {
        String sql = "SELECT user_id, roll_no, program, year FROM students WHERE user_id = ?";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("user_id"),
                            rs.getString("roll_no"),
                            rs.getString("program"),
                            rs.getInt("year")
                    );
                }
            }
        }
        return null;
    }

    public void createStudentProfile(Student s) throws SQLException {
        String sql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
        try (Connection conn = ERPDBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getUserId());
            ps.setString(2, s.getRollNo());
            ps.setString(3, s.getProgram());
            ps.setInt(4, s.getYear());
            ps.executeUpdate();
        }
    }
}
