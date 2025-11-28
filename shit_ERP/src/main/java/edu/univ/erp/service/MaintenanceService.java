package edu.univ.erp.service;

import edu.univ.erp.data.dao.SettingsDAO;

public class MaintenanceService {

    private static final String KEY_MAINTENANCE = "maintenance_mode";
    private final SettingsDAO settingsDAO = new SettingsDAO();

    public boolean isMaintenanceOn() throws Exception {
        String val = settingsDAO.getSetting(KEY_MAINTENANCE);
        // default is OFF if not set
        return "true".equalsIgnoreCase(val);
    }

    public void setMaintenance(boolean on) throws Exception {
        settingsDAO.upsertSetting(KEY_MAINTENANCE, on ? "true" : "false");
    }
}
