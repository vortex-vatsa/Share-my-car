package com.sharemycar.repository;


import com.sharemycar.model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * VehicleRepository persists and retrieves Vehicle objects in an H2 database.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Create the `vehicles` table if it does not exist.</li>
 *   <li>Insert, update, delete, and query Vehicle records.</li>
 *   <li>Compute aggregate statistics like average mileage.</li>
 * </ul>
 * </p>
 */
public class VehicleRepository {

    private final Connection conn;  // active JDBC connection


    /**
     * Constructor: stores the connection and ensures the table exists.
     *
     * @param conn JDBC Connection to H2
     * @throws SQLException if table creation fails
     */
    public VehicleRepository(Connection conn) throws SQLException {
        this.conn = conn;               // keep connection
        createTableIfNotExists();       // auto-create schema
    }


    /**
     * Creates the vehicles table if it doesn't already exist.
     */
    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS vehicles (
              id INT AUTO_INCREMENT PRIMARY KEY,
              brand VARCHAR(100),
              model VARCHAR(100),
              mileage DOUBLE,
              daily_price DOUBLE,
              maintenance_cost_per_km DOUBLE,
              available BOOLEAN
            )
            """;
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    /**
     * Fetches all vehicles from the DB.
     *
     * @return list of Vehicle objects
     * @throws SQLException on query error
     */
    public List<Vehicle> getAllVehicles() throws SQLException {
        String sql = "SELECT * FROM vehicles ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Vehicle> list = new ArrayList<>();
            while (rs.next()) {
                // map each row into a Vehicle
                Vehicle v = new Vehicle(
                        rs.getInt("id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getDouble("mileage"),
                        rs.getDouble("daily_price"),
                        rs.getDouble("maintenance_cost_per_km"),
                        rs.getBoolean("available")
                );
                list.add(v);             // add to list
            }
            return list;                // return all
        }
    }

    /**
     * Inserts a new vehicle and sets its generated ID on the object.
     */
    public void addVehicle(Vehicle v) throws SQLException {
        String sql = """
            INSERT INTO vehicles
              (brand, model, mileage, daily_price,
               maintenance_cost_per_km, available)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, v.getBrand());
            ps.setString(2, v.getModel());
            ps.setDouble(3, v.getMileage());
            ps.setDouble(4, v.getDailyPrice());
            ps.setDouble(5, v.getMaintenanceCostPerKm());
            ps.setBoolean(6, v.isAvailable());
            ps.executeUpdate();

            // fetch generated key
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    v.setId(keys.getInt(1));
                }
            }
        }
    }
    /**
     * Updates the availability flag for a vehicle.
     */
    public void updateVehicleAvailability(int vehicleId, boolean available) throws SQLException {
        String sql = "UPDATE vehicles SET available = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, vehicleId);
            ps.executeUpdate();        // run UPDATE
        }
    }


    /**
     * Fetches a single vehicle by its ID.
     *
     * @param id vehicle ID
     * @return Vehicle object or null if not found
     * @throws SQLException on DB error
     */
    public Vehicle getVehicleById(int id) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Vehicle(
                            rs.getInt("id"),
                            rs.getString("brand"),
                            rs.getString("model"),
                            rs.getDouble("mileage"),
                            rs.getDouble("daily_price"),
                            rs.getDouble("maintenance_cost_per_km"),
                            rs.getBoolean("available")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Updates the mileage for a given vehicle.
     *
     * @param vehicleId the vehicle’s ID
     * @param mileage   the new total mileage
     */
    public void updateVehicleMileage(int vehicleId, double mileage) throws SQLException {
        String sql = "UPDATE vehicles SET mileage = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, mileage);
            ps.setInt(2, vehicleId);
            ps.executeUpdate();
        }
    }

    /**
     * Returns the average mileage across all vehicles.
     *
     * @return average mileage (0.0 if none)
     */
    public double getAverageMileage() throws SQLException {
        String sql = "SELECT AVG(mileage) AS avg_mileage FROM vehicles";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("avg_mileage");
            }
            return 0.0;
        }
    }

    /** Deletes the vehicle row with the given ID. */
    public void deleteVehicle(int vehicleId) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.executeUpdate();
        }
    }

    /** Drops and recreates the vehicles table. */
    public void resetTable() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS vehicles");
        }
        createTableIfNotExists();
    }


}
