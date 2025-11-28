package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.service.CatalogService;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.util.PDFUtil;
import edu.univ.erp.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class StudentDashboardFrame extends JFrame {

    private final UserSession session;
    private final CatalogService catalogService = new CatalogService();
    private final StudentService studentService = new StudentService();

    // Catalog table
    private final DefaultTableModel catalogModel =
            new DefaultTableModel(new Object[]{"SectionID","Code","Title",
                    "Credits","Capacity","Instructor"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable tblCatalog = new JTable(catalogModel);

    // My registrations / grades (no time slot now)
    private final DefaultTableModel myModel =
            new DefaultTableModel(new Object[]{"SectionID","Course","Final Grade"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable tblMy = new JTable(myModel);

    public StudentDashboardFrame(UserSession session) {
        this.session = session;
        setTitle("Student Dashboard - " + session.getUsername());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
        loadCatalog();
        loadMyRegistrations();
    }

    private void initUI() {
        // TOP BAR
        JPanel top = new JPanel(new BorderLayout());
        JLabel lblRole = new JLabel("Role: STUDENT   |   User: " + session.getUsername());
        JButton btnLogout = new JButton("Logout");

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        top.add(lblRole, BorderLayout.WEST);
        top.add(btnLogout, BorderLayout.EAST);

        // CENTER
        JPanel center = new JPanel(new GridLayout(2, 1));

        // ---- Catalog Panel ----
        JPanel catalogPanel = new JPanel(new BorderLayout());
        catalogPanel.add(new JLabel("Course Catalog"), BorderLayout.NORTH);
        catalogPanel.add(new JScrollPane(tblCatalog), BorderLayout.CENTER);

        JPanel catalogButtons = new JPanel();
        JButton btnRegister = new JButton("Register Selected");
        JButton btnDrop = new JButton("Drop Selected");

        btnRegister.addActionListener(e -> registerSelectedFromCatalog());
        btnDrop.addActionListener(e -> dropSelectedFromCatalog());

        catalogButtons.add(btnRegister);
        catalogButtons.add(btnDrop);

        catalogPanel.add(catalogButtons, BorderLayout.SOUTH);

        // ---- My Registrations / Grades Panel ----
        JPanel myPanel = new JPanel(new BorderLayout());
        myPanel.add(new JLabel("My Registrations / Grades"), BorderLayout.NORTH);
        myPanel.add(new JScrollPane(tblMy), BorderLayout.CENTER);

        JPanel myButtons = new JPanel();
        JButton btnRefreshMy = new JButton("Refresh");
        JButton btnViewGrades = new JButton("View Grades for Selected");
        JButton btnExportPDF = new JButton("Download Transcript (PDF)");

        btnRefreshMy.addActionListener(e -> loadMyRegistrations());
        btnViewGrades.addActionListener(e -> showSelectedSectionGrades());
        btnExportPDF.addActionListener(e -> exportTranscriptPDF());

        myButtons.add(btnRefreshMy);
        myButtons.add(btnViewGrades);
        myButtons.add(btnExportPDF);

        myPanel.add(myButtons, BorderLayout.SOUTH);

        center.add(catalogPanel);
        center.add(myPanel);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    // ---------- Data loading ----------

    private void loadCatalog() {
        try {
            catalogModel.setRowCount(0);
            for (CatalogService.CatalogRow r : catalogService.listCatalog()) {
                catalogModel.addRow(new Object[]{
                        r.sectionId,
                        r.courseCode,
                        r.title,
                        r.credits,
                        r.capacity + " (" + r.enrolled + " enrolled)",
                        r.instructorId
                });
            }
        } catch (Exception e) {
            UIUtil.showError("Failed to load catalog: " + e.getMessage());
        }
    }

    private void loadMyRegistrations() {
        try {
            myModel.setRowCount(0);
            List<StudentService.GradeView> rows = studentService.listMyGrades(session);
            for (StudentService.GradeView gv : rows) {
                myModel.addRow(new Object[]{
                        gv.sectionId,
                        gv.courseCode,
                        (gv.finalGrade == null ? "-" : gv.finalGrade)
                });
            }
        } catch (Exception e) {
            UIUtil.showError("Failed to load my registrations: " + e.getMessage());
        }
    }

    // ---------- Helpers ----------

    private Integer getSelectedCatalogSectionId() {
        int row = tblCatalog.getSelectedRow();
        if (row == -1) return null;
        Object val = catalogModel.getValueAt(row, 0);
        if (val instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(val.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getSelectedMySectionId() {
        int row = tblMy.getSelectedRow();
        if (row == -1) return null;
        Object val = myModel.getValueAt(row, 0);
        if (val instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(val.toString());
        } catch (Exception e) {
            return null;
        }
    }

    // ---------- Actions ----------

    private void registerSelectedFromCatalog() {
        Integer sectionId = getSelectedCatalogSectionId();
        if (sectionId == null) {
            UIUtil.showError("Select a section in catalog first");
            return;
        }
        try {
            studentService.registerSection(session, sectionId);
            UIUtil.showInfo("Registered successfully");
            loadCatalog();
            loadMyRegistrations();
        } catch (Exception e) {
            UIUtil.showError(e.getMessage());
        }
    }

    private void dropSelectedFromCatalog() {
        Integer sectionId = getSelectedCatalogSectionId();
        if (sectionId == null) {
            UIUtil.showError("Select a section in catalog first");
            return;
        }
        try {
            studentService.dropSection(session, sectionId);
            UIUtil.showInfo("Dropped successfully");
            loadCatalog();
            loadMyRegistrations();
        } catch (Exception e) {
            UIUtil.showError(e.getMessage());
        }
    }

    private void showSelectedSectionGrades() {
        Integer sectionId = getSelectedMySectionId();
        if (sectionId == null) {
            UIUtil.showError("Select a row in 'My Registrations / Grades' first.");
            return;
        }
        try {
            StudentService.GradeDetails gd =
                    studentService.getMyGradesForSection(session, sectionId);

            String msg = "Course: " + gd.courseCode +
                    "\nSection: " + gd.sectionId +
                    "\n\nQuiz: " + gd.quiz +
                    "\nMidterm: " + gd.midterm +
                    "\nEnd-sem: " + gd.endsem +
                    "\n\nFinal Grade: " + gd.finalGrade;

            UIUtil.showInfo(msg);
        } catch (Exception e) {
            UIUtil.showError("Unable to load grades: " + e.getMessage());
        }
    }

    private void exportTranscriptPDF() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("transcript.pdf"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                List<Enrollment> list = studentService.listMyEnrollments(session);
                PDFUtil.writeTranscriptPDF(list, f.getAbsolutePath());
                UIUtil.showInfo("Transcript PDF saved: " + f.getAbsolutePath());
            }
        } catch (Exception e) {
            UIUtil.showError("Export failed: " + e.getMessage());
        }
    }
}
