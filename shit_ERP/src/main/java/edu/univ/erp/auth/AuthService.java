package edu.univ.erp.auth;

import edu.univ.erp.data.dao.AuthDAO;
import edu.univ.erp.domain.Role;
import edu.univ.erp.domain.UserSession;

public class AuthService {

    private final AuthDAO authDAO = new AuthDAO();

    public UserSession login(String username, String plainPassword) throws Exception {
        AuthDAO.AuthRecord rec = authDAO.findByUsername(username);
        if (rec == null) {
            throw new Exception("Incorrect username or password");
        }
        if (!"ACTIVE".equals(rec.status)) {
            throw new Exception("Account is inactive");
        }
        if (!PasswordUtil.verifyPassword(plainPassword, rec.passwordHash)) {
            throw new Exception("Incorrect username or password");
        }
        authDAO.updateLastLogin(rec.userId);
        return new UserSession(rec.userId, rec.username, rec.role);
    }

    public int createUser(String username, Role role, String plainPassword) throws Exception {
        String hash = PasswordUtil.hashPassword(plainPassword);
        return authDAO.createUser(username, role, hash);
    }
}
