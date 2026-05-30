package com.sharemycar.repository;


import com.sharemycar.model.Booking;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingRepository handles persistence and retrieval of Booking objects.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Create the `bookings` table if it does not exist.</li>
 *   <li>Insert new bookings and assign generated IDs.</li>
 *   <li>Fetch bookings by ID or by vehicle ID (active booking).</li>
 *   <li>Mark bookings as returned.</li>
 *   <li>Retrieve all bookings for reporting or display.</li>
 *   <li>Reset the bookings table for administrative purposes.</li>
 * </ul>
 * </p>
 */
public class BookingRepository {

    private final Connection conn; // Active JDBC connection

    /**
     * Constructs the repository and ensures the `bookings` table exists.
     *
     * @param conn JDBC Connection to the H2 database
     * @throws SQLException if table creation fails
     */
    public BookingRepository(Connection conn) throws SQLException {
        this.conn = conn;
        createTableIfNotExists();
    }

    /**
     * Creates the `bookings` table if it does not already exist.
     * <p>
     * Columns:
     * id, customer_name, vehicle_id, start_date, duration_days,
     * estimated_km, estimated_cost, returned flag.
     * </p>
     *
     * @throws SQLException if an error occurs executing the DDL
     */
    private void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS bookings (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  customer_name VARCHAR(100),
                  vehicle_id INT,
                  start_date DATE,
                  duration_days INT,
                  estimated_km DOUBLE,
                  estimated_cost DOUBLE,
                  returned BOOLEAN DEFAULT FALSE
                )
                """;
        try (Statement st = conn.createStatement()) {
            st.execute(sql);   // Execute DDL
        }
    }

    /**
     * Inserts a new booking record and sets its generated ID on the Booking object.
     *
     * @param b Booking object containing customer, vehicle, dates, estimates
     * @throws SQLException if an error occurs during insertion
     */
    public void addBooking(Booking b) throws SQLException {
        String sql = """
                INSERT INTO bookings
                  (customer_name, vehicle_id, start_date,
                   duration_days, estimated_km, estimated_cost, returned)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getCustomerName());
            ps.setInt(2, b.getVehicleId());
            ps.setDate(3, Date.valueOf(b.getStartDate()));
            ps.setInt(4, b.getDurationDays());
            ps.setDouble(5, b.getEstimatedKm());
            ps.setDouble(6, b.getEstimatedCost());
            ps.setBoolean(7, b.isReturned());
            ps.executeUpdate();
            // Retrieve generated primary key
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    b.setId(keys.getInt(1));   // Set generated ID on object
                }
            }
        }
    }

    /**
     * Retrieves a booking by its unique ID.
     *
     * @param id the booking ID to look up
     * @return Booking object if found, or null if not present
     * @throws SQLException if an error occurs during query
     */
    public Booking getBookingById(int id) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);  // Map row to Booking
                }
            }
        }
        return null;
    }

    /**
     * Finds an active (not returned) booking for a specific vehicle.
     *
     * @param vehicleId the vehicle ID to search for
     * @return active Booking if exists, otherwise null
     * @throws SQLException if an error occurs during query
     */
    public Booking getActiveBookingByVehicleId(int vehicleId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE vehicle_id = ? AND returned = FALSE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Marks a booking as returned by setting its `returned` flag to TRUE.
     *
     * @param bookingId the ID of the booking to mark returned
     * @throws SQLException if an error occurs during update
     */
    public void markReturned(int bookingId) throws SQLException {
        String sql = "UPDATE bookings SET returned = TRUE WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        }
    }


    /**
     * Maps the current row of a ResultSet to a Booking object.
     *
     * @param rs ResultSet positioned at a valid row
     * @return Booking object with fields populated from the row
     * @throws SQLException if an error occurs reading from ResultSet
     */
    private Booking mapRow(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("id"),
                rs.getString("customer_name"),
                rs.getInt("vehicle_id"),
                rs.getDate("start_date").toLocalDate(),
                rs.getInt("duration_days"),
                rs.getDouble("estimated_km"),
                rs.getDouble("estimated_cost"),
                rs.getBoolean("returned")
        );
    }


    /**
     * Retrieves all bookings (returned or not).
     *
     * @return list of all Booking objects
     * @throws SQLException on DB error
     */
    public List<Booking> getAllBookings() throws SQLException {
        String sql = "SELECT * FROM bookings ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Booking> list = new ArrayList<>();
            while (rs.next()) {
                Booking b = new Booking(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getInt("vehicle_id"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getInt("duration_days"),
                        rs.getDouble("estimated_km"),
                        rs.getDouble("estimated_cost"),
                        rs.getBoolean("returned")
                );
                list.add(b);
            }
            return list;
        }
    }



    /** Drops and recreates the bookings table. */
    public void resetTable() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS bookings");
        }
        createTableIfNotExists();
    }




}
