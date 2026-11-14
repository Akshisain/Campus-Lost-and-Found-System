package dao;

import db.DBConnection;
import model.LostItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Lost and Found Key & Lock records.
 * Demonstrates complete CRUD operations with prepared statements and parameter binding.
 */
public class LostItemDAO {

    /**
     * Adds a new lost or found item record to MySQL.
     *
     * @param item LostItem model to persist
     * @return generated record_id if successful; -1 otherwise
     */
    public int addItem(LostItem item) {
        String sql = "INSERT INTO lost_items (item_type, description, location, date_reported, reported_by, contact_info, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, item.getItemType());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getLocation());
            stmt.setDate(4, item.getDateReported());
            stmt.setString(5, item.getReportedBy());
            stmt.setString(6, item.getContactInfo());
            stmt.setString(7, item.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[LostItemDAO] Error adding item: " + e.getMessage());
        } finally {
            DBConnection.closeResources(generatedKeys, stmt, conn);
        }
        return -1;
    }

    /**
     * Retrieves all lost and found item records ordered by record_id descending.
     *
     * @return List of LostItem records
     */
    public List<LostItem> getAllItems() {
        List<LostItem> list = new ArrayList<>();
        String sql = "SELECT record_id, item_type, description, location, date_reported, reported_by, contact_info, status " +
                     "FROM lost_items ORDER BY record_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[LostItemDAO] Error fetching all items: " + e.getMessage());
        } finally {
            DBConnection.closeResources(rs, stmt, conn);
        }
        return list;
    }

    /**
     * Retrieves a single item by its primary key record_id.
     *
     * @param recordId record identifier
     * @return LostItem if found; null otherwise
     */
    public LostItem getItemById(int recordId) {
        String sql = "SELECT record_id, item_type, description, location, date_reported, reported_by, contact_info, status " +
                     "FROM lost_items WHERE record_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, recordId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return extractFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[LostItemDAO] Error getting item by ID: " + e.getMessage());
        } finally {
            DBConnection.closeResources(rs, stmt, conn);
        }
        return null;
    }

    /**
     * Updates an existing lost item record.
     *
     * @param item LostItem model containing updated data
     * @return true if update succeeded; false otherwise
     */
    public boolean updateItem(LostItem item) {
        String sql = "UPDATE lost_items SET item_type = ?, description = ?, location = ?, " +
                     "date_reported = ?, reported_by = ?, contact_info = ?, status = ? " +
                     "WHERE record_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, item.getItemType());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getLocation());
            stmt.setDate(4, item.getDateReported());
            stmt.setString(5, item.getReportedBy());
            stmt.setString(6, item.getContactInfo());
            stmt.setString(7, item.getStatus());
            stmt.setInt(8, item.getRecordId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[LostItemDAO] Error updating item: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeResources(stmt, conn);
        }
    }

    /**
     * Updates only the status field of a record.
     *
     * @param recordId target record id
     * @param newStatus "Lost", "Found", or "Returned"
     * @return true if updated; false otherwise
     */
    public boolean updateStatus(int recordId, String newStatus) {
        String sql = "UPDATE lost_items SET status = ? WHERE record_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setInt(2, recordId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[LostItemDAO] Error updating status: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeResources(stmt, conn);
        }
    }

    /**
     * Deletes a record from MySQL by record_id.
     *
     * @param recordId record id to delete
     * @return true if deleted; false otherwise
     */
    public boolean deleteItem(int recordId) {
        String sql = "DELETE FROM lost_items WHERE record_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, recordId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[LostItemDAO] Error deleting item: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeResources(stmt, conn);
        }
    }

    /**
     * Flexible multi-criteria search by keyword, item type, and status.
     *
     * @param keyword search keyword matching description, location, or reported_by
     * @param itemType "All" or specific type ("Key", "Lock", "Key Ring/Set")
     * @param status "All" or specific status ("Lost", "Found", "Returned")
     * @return matching records
     */
    public List<LostItem> searchItems(String keyword, String itemType, String status) {
        List<LostItem> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT record_id, item_type, description, location, date_reported, reported_by, contact_info, status " +
                "FROM lost_items WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(description) LIKE ? OR LOWER(location) LIKE ? OR LOWER(reported_by) LIKE ? OR CAST(record_id AS CHAR) = ?) ");
            String term = "%" + keyword.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(keyword.trim());
        }

        if (itemType != null && !itemType.equalsIgnoreCase("All") && !itemType.trim().isEmpty()) {
            sql.append("AND item_type = ? ");
            params.add(itemType.trim());
        }

        if (status != null && !status.equalsIgnoreCase("All") && !status.trim().isEmpty()) {
            sql.append("AND status = ? ");
            params.add(status.trim());
        }

        sql.append("ORDER BY record_id DESC");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[LostItemDAO] Error in searchItems: " + e.getMessage());
        } finally {
            DBConnection.closeResources(rs, stmt, conn);
        }
        return list;
    }

    /**
     * Gets count of records matching a specific status.
     */
    public int getCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM lost_items WHERE status = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[LostItemDAO] Error getCountByStatus: " + e.getMessage());
        } finally {
            DBConnection.closeResources(rs, stmt, conn);
        }
        return 0;
    }

    /**
     * Gets total count of all records.
     */
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM lost_items";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[LostItemDAO] Error getTotalCount: " + e.getMessage());
        } finally {
            DBConnection.closeResources(rs, stmt, conn);
        }
        return 0;
    }

    private LostItem extractFromResultSet(ResultSet rs) throws SQLException {
        return new LostItem(
                rs.getInt("record_id"),
                rs.getString("item_type"),
                rs.getString("description"),
                rs.getString("location"),
                rs.getDate("date_reported"),
                rs.getString("reported_by"),
                rs.getString("contact_info"),
                rs.getString("status")
        );
    }
}
