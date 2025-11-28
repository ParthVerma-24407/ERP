package edu.univ.erp.service;

import edu.univ.erp.data.dao.EnrollmentDAO;
import edu.univ.erp.data.dao.GradeDAO;
import edu.univ.erp.data.dao.SectionDAO;
import edu.univ.erp.data.dao.StudentDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Role;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.UserSession;

import java.util.ArrayList;
import java.util.List;

public class InstructorService {

    private final SectionDAO sectionDAO = new SectionDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final GradeDAO gradeDAO = new GradeDAO();
    private final MaintenanceService maintenanceService = new MaintenanceService();

    public static class GradeRow {
        public int enrollmentId;
        public int studentId;
        public String rollNo;
        public double quiz;
        public double midterm;
        public double endsem;
        public String finalGrade;
    }

    private void ensureInstructorOwnsSection(UserSession session, int sectionId) throws Exception {
        Section sec = sectionDAO.findById(sectionId);

        if (sec == null) throw new Exception("Section not found");

        if (session.getRole() != Role.INSTRUCTOR ||
                sec.getInstructorId() != session.getUserId()) {

            throw new Exception("Not your section.");
        }
    }

    public List<Section> listMySections(UserSession session) throws Exception {
        if (session.getRole() != Role.INSTRUCTOR)
            throw new Exception("Not an instructor");

        return sectionDAO.findByInstructor(session.getUserId());
    }

    public List<GradeRow> listStudentsWithGrades(UserSession session, int sectionId) throws Exception {
        ensureInstructorOwnsSection(session, sectionId);

        List<Enrollment> enrollments = enrollmentDAO.findBySection(sectionId);
        List<GradeRow> rows = new ArrayList<>();

        for (Enrollment e : enrollments) {
            Student s = studentDAO.findByUserId(e.getStudentId());
            GradeRow row = new GradeRow();

            row.enrollmentId = e.getEnrollmentId();
            row.studentId = e.getStudentId();
            row.rollNo = (s != null ? s.getRollNo() : "");

            row.quiz = gradeDAO.getScore(e.getEnrollmentId(), "QUIZ");
            row.midterm = gradeDAO.getScore(e.getEnrollmentId(), "MIDTERM");
            row.endsem = gradeDAO.getScore(e.getEnrollmentId(), "ENDSEM");
            row.finalGrade = gradeDAO.getFinalGrade(e.getEnrollmentId());

            rows.add(row);
        }
        return rows;
    }

    public void saveScores(UserSession session, int sectionId, List<GradeRow> rows) throws Exception {
        if (maintenanceService.isMaintenanceOn())
            throw new Exception("Maintenance is ON. Grade entry blocked.");

        ensureInstructorOwnsSection(session, sectionId);

        for (GradeRow r : rows) {
            gradeDAO.upsertScore(r.enrollmentId, "QUIZ",    r.quiz);
            gradeDAO.upsertScore(r.enrollmentId, "MIDTERM", r.midterm);
            gradeDAO.upsertScore(r.enrollmentId, "ENDSEM",  r.endsem);

            if (r.finalGrade != null && !r.finalGrade.trim().isEmpty()) {
                gradeDAO.updateFinalGrade(r.enrollmentId, r.finalGrade.trim());
            }
        }
    }
}
