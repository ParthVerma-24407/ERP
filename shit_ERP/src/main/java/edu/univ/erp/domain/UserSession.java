package edu.univ.erp.domain;

public class UserSession {
    private int userId;
    private String username;
    private Role role;

    public UserSession(int userId, String username, Role role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
}
