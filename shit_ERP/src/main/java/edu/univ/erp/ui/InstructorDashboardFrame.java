package edu.univ.erp.ui;

import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDashboardFrame extends JFrame {

    private final UserSession session;
    private final InstructorService instructorService = new InstructorService();

    private final DefaultTableModel sectionsModel =
            new DefaultTableModel(new Object[]{"SectionID", "CourseCode", "Capacity", "Enrolled"}, 0);
    private final JTable tblSections = new JTable(sectionsModel);

    private final DefaultTableModel gradesModel =
            new DefaultTableModel(new Object[]{"EnrollID", "StudentID", "RollNo",
                    "Quiz", "Midterm", "EndSem", "Final"}, 0);
    private final JTable tblGrades = new JTable(gradesModel);

    public InstructorDashboardFrame(UserSession session) {
        this.session = session;
        setTitle("Instructor Dashboard - " + session.getUsername());
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        loadSections();
    }

    private void initUI() {
        // TOP BAR (Role + Logout)
        JPanel top = new JPanel(new BorderLayout());
        JLabel lblRole = new JLabel("Role: INSTRUCTOR   |   User: " + session.getUsername());
        JButton btnLogout = new JButton("Logout");

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        top.add(lblRole, BorderLayout.WEST);
        top.add(btnLogout, BorderLayout.EAST);

        // Split: left = sections, right = grades
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.3);

        // Left: sections list
        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("Sections"), BorderLayout.NORTH);
        left.add(new JScrollPane(tblSections), BorderLayout.CENTER);

        JButton btnLoadStudents = new JButton("Load Students");
        btnLoadStudents.addActionListener(e -> loadStudentsForSelectedSection());
        left.add(btnLoadStudents, BorderLayout.SOUTH);

        // Right: grades table
        JPanel right = new JPanel(new BorderLayout());
        right.add(new JLabel("Grades (enter Quiz / Midterm / EndSem / Final)"), BorderLayout.NORTH);
        right.add(new JScrollPane(tblGrades), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton btnSave = new JButton("Save Scores");   // only save, no compute button
        btnSave.addActionListener(e -> saveScores());
        actions.add(btnSave);

        right.add(actions, BorderLayout.SOUTH);

        split.setLeftComponent(left);
        split.setRightComponent(right);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    // ---------- Sections ----------

    private void loadSections() {
        try {
            sectionsModel.setRowCount(0);
            for (Section s : instructorService.listMySections(session)) {
                sectionsModel.addRow(new Object[]{
                        s.getSectionId(),
                        s.getCourseCode(),
                        s.getCapacity(),
                        s.getEnrolledCount()
                });
            }
        } catch (Exception e) {
            UIUtil.showError("Failed to load sections: " + e.getMessage());
        }
    }

    private Integer getSelectedSectionId() {
        int row = tblSections.getSelectedRow();
        if (row == -1) return null;
        return (Integer) sectionsModel.getValueAt(row, 0);
    }

    // ---------- Load Students & Scores ----------

    private void loadStudentsForSelectedSection() {
        Integer sectionId = getSelectedSectionId();
        if (sectionId == null) {
            UIUtil.showError("Select a section first");
            return;
        }
        try {
            gradesModel.setRowCount(0);
            for (InstructorService.GradeRow r : instructorService.listStudentsWithGrades(session, sectionId)) {
                gradesModel.addRow(new Object[]{
                        r.enrollmentId,
                        r.studentId,
                        r.rollNo,
                        r.quiz,
                        r.midterm,
                        r.endsem,
                        r.finalGrade
                });
            }
        } catch (Exception e) {
            // If this is another instructor’s section, message will be "Not your section."
            UIUtil.showError("Failed to load students: " + e.getMessage());
        }
    }

    // ---------- Save Scores ----------

    private void saveScores() {
        Integer sectionId = getSelectedSectionId();
        if (sectionId == null) {
            UIUtil.showError("Select a section first");
            return;
        }
        try {
            List<InstructorService.GradeRow> rows = new ArrayList<>();
            for (int i = 0; i < gradesModel.getRowCount(); i++) {
                InstructorService.GradeRow r = new InstructorService.GradeRow();
                r.enrollmentId = (Integer) gradesModel.getValueAt(i, 0);
                r.studentId    = (Integer) gradesModel.getValueAt(i, 1);
                r.rollNo       = (String) gradesModel.getValueAt(i, 2);
                r.quiz         = parseDouble(gradesModel.getValueAt(i, 3));
                r.midterm      = parseDouble(gradesModel.getValueAt(i, 4));
                r.endsem       = parseDouble(gradesModel.getValueAt(i, 5));
                Object fgObj   = gradesModel.getValueAt(i, 6);
                r.finalGrade   = (fgObj == null ? null : fgObj.toString());
                rows.add(r);
            }
            instructorService.saveScores(session, sectionId, rows);
            UIUtil.showInfo("Scores saved.");
        } catch (Exception e) {
            // If section is not theirs, they will see: "Not your section."
            UIUtil.showError("Save failed: " + e.getMessage());
        }
    }

    private double parseDouble(Object val) {
        if (val == null) return 0.0;
        if (val instanceof Number n) return n.doubleValue();
        try {
            String s = val.toString().trim();
            if (s.isEmpty()) return 0.0;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
