package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection provides centralized, robust JDBC connectivity for the
 * Campus Lost Key & Lock Management System (PIET Minor Project NSP).
 *
 * Implements candidate password fallback handling ("", "root", "admin123", "password")
 * to ensure seamless demonstration across student laptops and evaluation lab machines.
 */
public class DBConnection {

    private static final String DB_NAME = "campus_lost_key_db";
    private static final String URL_PREFIX = "jdbc:mysql://localhost:3306/" + DB_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";

    // Common passwords tested in student lab environments
   private static final String[] CANDIDATE_PASSWORDS = {"Achint@1307"};
    private static String cachedWorkingPassword = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    /**
     * Obtains a connection to the campus_lost_key_db database.
     *
     * @return active java.sql.Connection
     * @throws SQLException if unable to establish connection
     */
    public static Connection getConnection() throws SQLException {
        if (cachedWorkingPassword != null) {
            try {
                return DriverManager.getConnection(URL_PREFIX, DB_USER, cachedWorkingPassword);
            } catch (SQLException ignored) {
                // If previously cached password no longer works, probe candidate list again
                cachedWorkingPassword = null;
            }
        }

        SQLException lastException = null;
        for (String pwd : CANDIDATE_PASSWORDS) {
            try {
                Connection conn = DriverManager.getConnection(URL_PREFIX, DB_USER, pwd);
                cachedWorkingPassword = pwd;
                return conn;
            } catch (SQLException ex) {
                lastException = ex;
            }
        }

        throw (lastException != null) ? lastException
                : new SQLException("Could not connect to MySQL database: " + DB_NAME);
    }

    /**
     * Safely closes an open JDBC connection.
     *
     * @param conn the connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[DBConnection] Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Safely closes one or more AutoCloseable resources (Statements, ResultSets).
     *
     * @param resources array of resources to close
     */
    public static void closeResources(AutoCloseable... resources) {
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Quick health check utility to verify database connectivity.
     *
     * @return true if database is reachable and responsive
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[DBConnection] Health check failed: " + e.getMessage());
            return false;
        }
    }
}
