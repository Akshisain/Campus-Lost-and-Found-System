package model;

import java.sql.Date;

/**
 * Model representing a lost, found, or returned Key or Lock record.
 * Designed according to PIET campus operational requirements.
 */
public class LostItem {

    private int recordId;
    private String itemType;      // "Key", "Lock", "Key Ring/Set"
    private String description;
    private String location;      // e.g. "Computer Lab 3", "Library Hall"
    private Date dateReported;    // YYYY-MM-DD
    private String reportedBy;    // Name of student/staff/security
    private String contactInfo;   // Phone number, Roll No, or Department
    private String status;        // "Lost", "Found", "Returned"

    public LostItem() {
    }

    public LostItem(int recordId, String itemType, String description, String location,
                    Date dateReported, String reportedBy, String contactInfo, String status) {
        this.recordId = recordId;
        this.itemType = itemType;
        this.description = description;
        this.location = location;
        this.dateReported = dateReported;
        this.reportedBy = reportedBy;
        this.contactInfo = contactInfo;
        this.status = status;
    }

    public LostItem(String itemType, String description, String location,
                    Date dateReported, String reportedBy, String contactInfo, String status) {
        this.itemType = itemType;
        this.description = description;
        this.location = location;
        this.dateReported = dateReported;
        this.reportedBy = reportedBy;
        this.contactInfo = contactInfo;
        this.status = status;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDateReported() {
        return dateReported;
    }

    public void setDateReported(Date dateReported) {
        this.dateReported = dateReported;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "#" + recordId + " [" + itemType + "] " + description + " (" + status + ")";
    }
}
