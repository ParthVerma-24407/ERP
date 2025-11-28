package edu.univ.erp.service;

import edu.univ.erp.data.dao.CourseDAO;
import edu.univ.erp.data.dao.SectionDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogService {

    private final CourseDAO courseDAO = new CourseDAO();
    private final SectionDAO sectionDAO = new SectionDAO();

    // Simple DTO used by Student UI
    public static class CatalogRow {
        public int sectionId;
        public String courseCode;
        public String title;
        public int credits;
        public int capacity;
        public int enrolled;
        public int instructorId; // we’ll display this as “instructor”
    }

    // For completeness, still keep these if you use them elsewhere
    public List<Course> listCourses() throws Exception {
        return courseDAO.findAll();
    }

    // New: full catalog with titles/credits/capacity/instructor
    public List<CatalogRow> listCatalog() throws Exception {
        List<Section> sections = sectionDAO.findAll();
        Map<String, Course> courseCache = new HashMap<>();

        List<CatalogRow> rows = new ArrayList<>();
        for (Section s : sections) {
            Course c = courseCache.get(s.getCourseCode());
            if (c == null) {
                c = courseDAO.findByCode(s.getCourseCode());
                if (c != null) {
                    courseCache.put(c.getCourseCode(), c);
                }
            }
            CatalogRow row = new CatalogRow();
            row.sectionId = s.getSectionId();
            row.courseCode = s.getCourseCode();
            row.title = (c != null ? c.getTitle() : "");
            row.credits = (c != null ? c.getCredits() : 0);
            row.capacity = s.getCapacity();
            row.enrolled = s.getEnrolledCount();
            row.instructorId = s.getInstructorId(); // we show numeric ID as “instructor”
            rows.add(row);
        }
        return rows;
    }
}
