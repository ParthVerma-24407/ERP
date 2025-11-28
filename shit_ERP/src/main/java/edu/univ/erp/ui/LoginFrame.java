package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.domain.Role;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.util.UIUtil;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField txtUser = new JTextField(15);
    private final JPasswordField txtPass = new JPasswordField(15);
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("University ERP - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; panel.add(txtUser, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; panel.add(txtPass, gbc);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> doLogin());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);

        add(panel);
    }

    private void doLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());
        try {
            UserSession session = authService.login(username, password);
            UIUtil.showInfo("Login successful as " + session.getRole());

            SwingUtilities.invokeLater(() -> {
                dispose();
                Role r = session.getRole();
                if (r == Role.STUDENT) {
                    new StudentDashboardFrame(session).setVisible(true);
                } else if (r == Role.ADMIN) {
                    new AdminDashboardFrame(session).setVisible(true);
                } else if (r == Role.INSTRUCTOR) {
                    new InstructorDashboardFrame(session).setVisible(true);
                }
            });
        } catch (Exception ex) {
            UIUtil.showError(ex.getMessage());
        }
    }
}
