-- ==============================================================================
-- Poornima Institute of Engineering & Technology (PIET)
-- B.Tech II Year – III Semester Minor Project (NSP) - Session 2026–27
-- Student Name: AKSHI MANOJ SAIN | Student ID: PIET25CS065 | Section: 3CSD
-- Project Title: Campus Lost Key & Lock Management System
-- File: schema.sql
-- ==============================================================================

-- 1. Create Database if it does not already exist
CREATE DATABASE IF NOT EXISTS campus_lost_key_db;
USE campus_lost_key_db;

-- 2. Drop existing tables for a clean, reproducible setup
DROP TABLE IF EXISTS lost_items;
DROP TABLE IF EXISTS users;

-- 3. Table: users
-- Stores credentials and roles for Admin and Campus Users (Students/Faculty/Staff)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'Admin' or 'User'
    contact VARCHAR(50) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Table: lost_items
-- Stores records of lost, found, and returned keys and locks on campus
CREATE TABLE lost_items (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    item_type VARCHAR(30) NOT NULL,      -- 'Key', 'Lock', 'Key Ring/Set'
    description VARCHAR(255) NOT NULL,
    location VARCHAR(100) NOT NULL,      -- Campus location where lost/found
    date_reported DATE NOT NULL,         -- Date item was lost/found (YYYY-MM-DD)
    reported_by VARCHAR(100) NOT NULL,   -- Name of reporting individual
    contact_info VARCHAR(100) NOT NULL,  -- Contact / Roll No / Department
    status VARCHAR(20) NOT NULL DEFAULT 'Lost', -- 'Lost', 'Found', 'Returned'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 5. Seed Initial User Accounts (1 Admin + 3 Users)
INSERT INTO users (username, password, full_name, role, contact) VALUES
('admin', 'admin123', 'Akshi Manoj Sain (Admin)', 'Admin', 'admin.keylock@piet.poornima.org'),
('student1', 'user123', 'Rahul Sharma (CS Student)', 'User', 'rahul.s@piet.poornima.org'),
('student2', 'user123', 'Priya Verma (ECE Student)', 'User', 'priya.v@piet.poornima.org'),
('faculty1', 'user123', 'Dr. A. K. Gupta (Faculty)', 'User', 'akgupta@piet.poornima.org');

-- 6. Seed Realistic Lost & Found Records for Campus Keys and Locks
INSERT INTO lost_items (item_type, description, location, date_reported, reported_by, contact_info, status) VALUES
('Key', 'Godrej silver brass key with blue PIET keychain', 'Computer Lab 3 (Block A)', '2026-09-01', 'Rahul Sharma', '9829011221 (CS-2nd Yr)', 'Lost'),
('Lock', 'Harrison 50mm heavy brass padlock without key', 'Mechanical Workshop', '2026-09-02', 'Mahesh Kumar (Lab Tech)', 'Ext. 204 (Mech Dept)', 'Found'),
('Key Ring/Set', 'Bunch of 3 locker keys with red rubber tag (Locker #14)', 'Central Library Reading Hall', '2026-09-03', 'Priya Verma', '9876543210 (ECE-2nd Yr)', 'Returned'),
('Key', 'Honda Activa scooter key with black rubber cover', 'Campus Parking Lot B', '2026-09-04', 'Amit Patel', '9812345678 (Civil-3rd Yr)', 'Lost'),
('Lock', 'Link 6-lever iron padlock found on cabinet door', 'Chemistry Lab (Ground Floor)', '2026-09-05', 'Suresh Meena (Staff)', 'Ext. 112 (Science Dept)', 'Found'),
('Key', 'Godrej master key for Room 204 with yellow tape', 'Seminar Hall B (Block B)', '2026-09-06', 'Dr. A. K. Gupta', '9988776655 (HOD Office)', 'Returned'),
('Key Ring/Set', 'Ring with 2 small cabinet keys and mini Swiss tool', 'Campus Canteen Counter', '2026-09-07', 'Vikas Jain (Student)', '9789012345 (EE-1st Yr)', 'Lost'),
('Key', 'Single small brass desk drawer key (#42)', 'Faculty Staff Room 102', '2026-09-08', 'Mrs. Sunita Rao', 'Ext. 145 (Admin Dept)', 'Found'),
('Lock', 'Godrej Nav-Tal 7-lever brass padlock', 'Sports Ground Equipment Shed', '2026-09-08', 'Coach Balram Singh', 'Ext. 305 (Sports Dept)', 'Lost'),
('Key', 'Hero Splendor motorcycle key with metallic skull ring', 'Main Entrance Gate Security Desk', '2026-09-09', 'Security Officer Rajesh', 'Main Gate Booth #1', 'Found');

-- 7. Verification Queries
SELECT 'Users count:' AS check_label, COUNT(*) AS count FROM users;
SELECT 'Lost items count:' AS check_label, COUNT(*) AS count FROM lost_items;
SELECT item_type, status, COUNT(*) AS total FROM lost_items GROUP BY item_type, status;
