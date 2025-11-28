package edu.univ.erp.ui;

import edu.univ.erp.domain.UserSession;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.MaintenanceService;
import edu.univ.erp.util.UIUtil;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {

    private final UserSession session;
    private final AdminService adminService = new AdminService();
    private final MaintenanceService maintenanceService = new MaintenanceService();

    public AdminDashboardFrame(UserSession session) {
        this.session = session;
        setTitle("Admin Dashboard - " + session.getUsername());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // --------- TOP BAR ----------
        JPanel top = new JPanel(new BorderLayout());
        JLabel lblRole = new JLabel("Role: ADMIN   |   User: " + session.getUsername());
        JButton btnLogout = new JButton("Logout");

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        top.add(lblRole, BorderLayout.WEST);
        top.add(btnLogout, BorderLayout.EAST);

        // --------- CENTER: TABS ----------
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Maintenance", buildMaintenancePanel());
        tabs.addTab("Create Student", buildCreateStudentPanel());
        tabs.addTab("Create Instructor", buildCreateInstructorPanel());
        tabs.addTab("Create Course/Section", buildCreateCourseSectionPanel());

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // ============ TAB: Maintenance ============
    private JPanel buildMaintenancePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblStatus = new JLabel();
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD, 14f));

        JButton btnOn = new JButton("Turn Maintenance ON");
        JButton btnOff = new JButton("Turn Maintenance OFF");

        // helper to refresh label
        Runnable refreshStatus = () -> {
            try {
                boolean on = maintenanceService.isMaintenanceOn();
                if (on) {
                    lblStatus.setText("Current status: MAINTENANCE IS ON");
                    lblStatus.setForeground(Color.RED);
                } else {
                    lblStatus.setText("Current status: Maintenance is OFF");
                    lblStatus.setForeground(new Color(0, 128, 0));
                }
            } catch (Exception ex) {
                lblStatus.setText("Failed to load status: " + ex.getMessage());
                lblStatus.setForeground(Color.RED);
            }
        };
        refreshStatus.run();

        btnOn.addActionListener(e -> {
            try {
                maintenanceService.setMaintenance(true);
                refreshStatus.run();
                UIUtil.showInfo(
                        "Maintenance mode has been turned ON.\n" +
                                "Students cannot register/drop and instructors cannot edit grades."
                );
            } catch (Exception ex) {
                UIUtil.showError("Failed to turn ON maintenance: " + ex.getMessage());
            }
        });

        btnOff.addActionListener(e -> {
            try {
                maintenanceService.setMaintenance(false);
                refreshStatus.run();
                UIUtil.showInfo(
                        "Maintenance mode has been turned OFF.\n" +
                                "System is now fully available."
                );
            } catch (Exception ex) {
                UIUtil.showError("Failed to turn OFF maintenance: " + ex.getMessage());
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblStatus, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(btnOn, gbc);

        gbc.gridx = 1;
        panel.add(btnOff, gbc);

        return panel;
    }

    // ============ TAB: Create Student ============
    private JPanel buildCreateStudentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField txtUsername = new JTextField(20);
        JPasswordField txtPassword = new JPasswordField(20);
        JTextField txtRollNo = new JTextField(20);
        JTextField txtProgram = new JTextField(20);
        JTextField txtYear = new JTextField(5);

        JButton btnCreate = new JButton("Create Student");

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Roll No:"), gbc);
        gbc.gridx = 1;
        panel.add(txtRollNo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Program:"), gbc);
        gbc.gridx = 1;
        panel.add(txtProgram, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        panel.add(txtYear, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnCreate, gbc);

        btnCreate.addActionListener(e -> {
            try {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword());
                String rollNo   = txtRollNo.getText().trim();
                String program  = txtProgram.getText().trim();
                String yearStr  = txtYear.getText().trim();

                if (username.isEmpty() || password.isEmpty() ||
                        rollNo.isEmpty() || program.isEmpty() || yearStr.isEmpty()) {
                    UIUtil.showError("All fields are required.");
                    return;
                }

                int year;
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException ex) {
                    UIUtil.showError("Year must be a number.");
                    return;
                }

                adminService.createStudentUser(username, password, rollNo, program, year);
                UIUtil.showInfo("Student created successfully.");

                txtUsername.setText("");
                txtPassword.setText("");
                txtRollNo.setText("");
                txtProgram.setText("");
                txtYear.setText("");

            } catch (Exception ex) {
                UIUtil.showError("Failed to create student: " + ex.getMessage());
            }
        });

        return panel;
    }

    // ============ TAB: Create Instructor ============
    private JPanel buildCreateInstructorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField txtUsername = new JTextField(20);
        JPasswordField txtPassword = new JPasswordField(20);
        JTextField txtDept = new JTextField(20);
        JButton btnCreate = new JButton("Create Instructor");

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDept, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnCreate, gbc);

        btnCreate.addActionListener(e -> {
            try {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword());
                String dept     = txtDept.getText().trim();

                if (username.isEmpty() || password.isEmpty() || dept.isEmpty()) {
                    UIUtil.showError("All fields are required.");
                    return;
                }

                adminService.createInstructorUser(username, password, dept);
                UIUtil.showInfo("Instructor created successfully.");

                txtUsername.setText("");
                txtPassword.setText("");
                txtDept.setText("");
            } catch (Exception ex) {
                UIUtil.showError("Failed to create instructor: " + ex.getMessage());
            }
        });

        return panel;
    }

    // ============ TAB: Create Course / Section ============
    private JPanel buildCreateCourseSectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // ----- Course fields -----
        JLabel lblCourseTitle = new JLabel("Create Course");
        lblCourseTitle.setFont(lblCourseTitle.getFont().deriveFont(Font.BOLD, 14f));

        JTextField txtCourseCode = new JTextField(10);
        JTextField txtCourseTitleField = new JTextField(20);
        JTextField txtCredits = new JTextField(5);
        JButton btnCreateCourse = new JButton("Create Course");

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(lblCourseTitle, gbc);
        row++;

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCourseCode, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCourseTitleField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Credits:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCredits, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnCreateCourse, gbc);

        // Separator
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JSeparator(), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        row++;

        // ----- Section fields -----
        JLabel lblSectionTitle = new JLabel("Create Section");
        lblSectionTitle.setFont(lblSectionTitle.getFont().deriveFont(Font.BOLD, 14f));

        JTextField txtSectionId = new JTextField(5);
        JTextField txtSectionCourseCode = new JTextField(10);
        JTextField txtInstructorId = new JTextField(5);
        JTextField txtCapacity = new JTextField(5);
        JButton btnCreateSection = new JButton("Create Section");

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(lblSectionTitle, gbc);
        row++;

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Section ID:"), gbc);
        gbc.gridx = 1;
        panel.add(txtSectionId, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        panel.add(txtSectionCourseCode, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Instructor User ID:"), gbc);
        gbc.gridx = 1;
        panel.add(txtInstructorId, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCapacity, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnCreateSection, gbc);

        // ----- Actions -----
        btnCreateCourse.addActionListener(e -> {
            try {
                String code = txtCourseCode.getText().trim();
                String title = txtCourseTitleField.getText().trim();
                String creditsStr = txtCredits.getText().trim();

                if (code.isEmpty() || title.isEmpty() || creditsStr.isEmpty()) {
                    UIUtil.showError("All course fields are required.");
                    return;
                }

                int credits;
                try {
                    credits = Integer.parseInt(creditsStr);
                } catch (NumberFormatException ex) {
                    UIUtil.showError("Credits must be a number.");
                    return;
                }

                adminService.createCourse(code, title, credits);
                UIUtil.showInfo("Course created successfully.");

                txtCourseCode.setText("");
                txtCourseTitleField.setText("");
                txtCredits.setText("");

            } catch (Exception ex) {
                UIUtil.showError("Failed to create course: " + ex.getMessage());
            }
        });

        btnCreateSection.addActionListener(e -> {
            try {
                String sectionIdStr = txtSectionId.getText().trim();
                String courseCode = txtSectionCourseCode.getText().trim();
                String instructorIdStr = txtInstructorId.getText().trim();
                String capacityStr = txtCapacity.getText().trim();

                if (sectionIdStr.isEmpty() || courseCode.isEmpty()
                        || instructorIdStr.isEmpty() || capacityStr.isEmpty()) {
                    UIUtil.showError("All section fields are required.");
                    return;
                }

                int sectionId;
                int instructorId;
                int capacity;
                try {
                    sectionId = Integer.parseInt(sectionIdStr);
                    instructorId = Integer.parseInt(instructorIdStr);
                    capacity = Integer.parseInt(capacityStr);
                } catch (NumberFormatException ex) {
                    UIUtil.showError("Section ID, Instructor ID, and Capacity must be numbers.");
                    return;
                }

                adminService.createSection(sectionId, courseCode, instructorId, capacity);
                UIUtil.showInfo("Section created successfully.");

                txtSectionId.setText("");
                txtSectionCourseCode.setText("");
                txtInstructorId.setText("");
                txtCapacity.setText("");

            } catch (Exception ex) {
                UIUtil.showError("Failed to create section: " + ex.getMessage());
            }
        });

        return panel;
    }
}
