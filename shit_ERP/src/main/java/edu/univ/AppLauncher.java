package edu.univ.erp;

import edu.univ.erp.ui.LoginFrame;

import javax.swing.*;

public class AppLauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
