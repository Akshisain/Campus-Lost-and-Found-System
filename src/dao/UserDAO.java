package dao;

import db.DBConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for User authentication and credential management.
 */
public class UserDAO {

    /**
     * Authenticates a user by matching username and password.
     *
     * @param username the entered username
     * @param password the entered password
     * @return User object if credentials are valid; null otherwise
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT user_id, username, password, full_name, role, contact " +
                     "FROM users WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("contact")
                );
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Authentication error: " + e.getMessage());
        } finally {
            DBConnection.closeResources(rs, stmt, conn);
        }
        return null;
    }

    /**
     * Changes password for an existing user after validating the current password.
     *
     * @param userId user's database ID
     * @param oldPassword current password for verification
     * @param newPassword the new password to set
     * @return true if successfully changed; false otherwise
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        String checkSql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
        String updateSql = "UPDATE users SET password = ? WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            // Verify old password first
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            checkStmt.setString(2, oldPassword);
            rs = checkStmt.executeQuery();

            if (!rs.next()) {
                return false; // Old password does not match
            }

            // Update with new password
            updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newPassword);
            updateStmt.setInt(2, userId);

            return updateStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Password change error: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeResources(rs, checkStmt, updateStmt, conn);
        }
    }

    /**
     * Retrieves user details by ID.
     */
    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, password, full_name, role, contact " +
                     "FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("contact")
                );
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getUserById error: " + e.getMessage());
        } finally {
            DBConnection.closeResources(rs, stmt, conn);
        }
        return null;
    }
}
