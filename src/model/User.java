package model;

/**
 * Model representing an authenticated system user (Admin or standard Campus User).
 * Strictly aligned with PIET Minor Project guidelines for Role-Based Access Control.
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String role; // "Admin" or "User"
    private String contact;

    public User() {
    }

    public User(int userId, String username, String password, String fullName, String role, String contact) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.contact = contact;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(this.role);
    }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
