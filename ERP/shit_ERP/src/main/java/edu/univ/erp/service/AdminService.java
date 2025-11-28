package edu.univ.erp.service;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.dao.CourseDAO;
import edu.univ.erp.data.dao.InstructorDAO;
import edu.univ.erp.data.dao.SectionDAO;
import edu.univ.erp.data.dao.StudentDAO;
import edu.univ.erp.domain.*;

public class AdminService {

    private final AuthService authService = new AuthService();
    private final StudentDAO studentDAO = new StudentDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final MaintenanceService maintenanceService = new MaintenanceService();

    public int createStudentUser(String username, String password,
                                 String rollNo, String program, int year) throws Exception {
        int userId = authService.createUser(username, Role.STUDENT, password);
        Student s = new Student(userId, rollNo, program, year);
        // make sure StudentDAO has this method name:
        studentDAO.createStudentProfile(s);
        return userId;
    }

    public int createInstructorUser(String username, String password,
                                    String department) throws Exception {
        int userId = authService.createUser(username, Role.INSTRUCTOR, password);
        Instructor ins = new Instructor(userId, department);
        instructorDAO.createInstructorProfile(ins);
        return userId;
    }

    public void createCourse(String code, String title, int credits) throws Exception {
        courseDAO.createCourse(new Course(code, title, credits));
    }

    // includes sectionId (admin chooses the ID)
    public void createSection(int sectionId, String courseCode, int instructorId, int capacity) throws Exception {
        sectionDAO.createSection(sectionId, courseCode, instructorId, capacity);
    }

    // Maintenance proxy methods for AdminDashboard
    public boolean isMaintenanceOn() throws Exception {
        return maintenanceService.isMaintenanceOn();
    }

    public void setMaintenance(boolean on) throws Exception {
        maintenanceService.setMaintenance(on);
    }
}
