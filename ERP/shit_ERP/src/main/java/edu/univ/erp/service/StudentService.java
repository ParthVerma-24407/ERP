package edu.univ.erp.service;

import edu.univ.erp.data.dao.EnrollmentDAO;
import edu.univ.erp.data.dao.GradeDAO;
import edu.univ.erp.data.dao.SectionDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.UserSession;

import java.util.ArrayList;
import java.util.List;

public class StudentService {

    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final GradeDAO gradeDAO = new GradeDAO();
    private final MaintenanceService maintenanceService = new MaintenanceService();

    // ---------- Actions for registration / drop ----------

    public void registerSection(UserSession session, int sectionId) throws Exception {
        if (maintenanceService.isMaintenanceOn()) {
            throw new Exception("Maintenance is ON. Registrations are temporarily blocked.");
        }

        Section sec = sectionDAO.findById(sectionId);
        if (sec == null) {
            throw new Exception("Section not found");
        }
        if (sec.getEnrolledCount() >= sec.getCapacity()) {
            throw new Exception("Section full");
        }
        if (enrollmentDAO.exists(session.getUserId(), sectionId)) {
            throw new Exception("Already enrolled in this section");
        }

        enrollmentDAO.addEnrollment(session.getUserId(), sectionId);
        sectionDAO.incrementEnrollment(sectionId);
    }

    public void dropSection(UserSession session, int sectionId) throws Exception {
        if (maintenanceService.isMaintenanceOn()) {
            throw new Exception("Maintenance is ON. Drop is temporarily blocked.");
        }

        if (!enrollmentDAO.exists(session.getUserId(), sectionId)) {
            throw new Exception("Not enrolled in this section");
        }

        enrollmentDAO.removeEnrollment(session.getUserId(), sectionId);
        sectionDAO.decrementEnrollment(sectionId);
    }

    // ---------- Used by PDF export ----------

    public List<Enrollment> listMyEnrollments(UserSession session) throws Exception {
        return enrollmentDAO.findByStudent(session.getUserId());
    }

    // ---------- Summary view (for dashboard table) ----------

    public static class GradeView {
        public int sectionId;
        public String courseCode;
        public String finalGrade;
    }

    public List<GradeView> listMyGrades(UserSession session) throws Exception {
        List<Enrollment> enrollments = enrollmentDAO.findByStudent(session.getUserId());
        List<GradeView> list = new ArrayList<>();

        for (Enrollment e : enrollments) {
            Section sec = sectionDAO.findById(e.getSectionId());

            GradeView gv = new GradeView();
            gv.sectionId = e.getSectionId();
            gv.courseCode = (sec != null ? sec.getCourseCode() : "");
            gv.finalGrade = gradeDAO.getFinalGrade(e.getEnrollmentId());

            list.add(gv);
        }

        return list;
    }

    // ---------- Detailed grades for one registered section ----------

    public static class GradeDetails {
        public String courseCode;
        public int sectionId;
        public double quiz;
        public double midterm;
        public double endsem;
        public String finalGrade;
    }

    public GradeDetails getMyGradesForSection(UserSession session, int sectionId) throws Exception {
        // Find the enrollment for this student & section
        List<Enrollment> enrollments = enrollmentDAO.findByStudent(session.getUserId());
        Enrollment target = null;
        for (Enrollment e : enrollments) {
            if (e.getSectionId() == sectionId) {
                target = e;
                break;
            }
        }
        if (target == null) {
            throw new Exception("You are not enrolled in section " + sectionId);
        }

        Section sec = sectionDAO.findById(sectionId);
        GradeDetails gd = new GradeDetails();
        gd.sectionId = sectionId;
        gd.courseCode = (sec != null ? sec.getCourseCode() : "");

        gd.quiz = gradeDAO.getScore(target.getEnrollmentId(), "QUIZ");
        gd.midterm = gradeDAO.getScore(target.getEnrollmentId(), "MIDTERM");
        gd.endsem = gradeDAO.getScore(target.getEnrollmentId(), "ENDSEM");
        gd.finalGrade = gradeDAO.getFinalGrade(target.getEnrollmentId());
        if (gd.finalGrade == null) gd.finalGrade = "-";

        return gd;
    }
}
