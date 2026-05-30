package com.sharemycar.repository;

import com.sharemycar.model.TransactionLog;

import java.sql.*;

/**
 * TransactionLogRepository manages persistence and retrieval of transaction logs.
 * <p>
 * Responsibilities include:
 * <ul>
 *   <li>Creating the `transaction_logs` table if it does not exist</li>
 *   <li>Inserting new transaction log entries</li>
 *   <li>Aggregating financial columns (revenue, fees) for reporting</li>
 *   <li>Resetting the table schema</li>
 * </ul>
 * </p>
 */
public class TransactionLogRepository {

    private final Connection conn;  // Active JDBC connection to the database

    /**
     * Constructs the repository and ensures the `transaction_logs` table exists.
     *
     * @param conn JDBC connection to the H2 database
     * @throws SQLException if table creation fails
     */
    public TransactionLogRepository(Connection conn) throws SQLException {
        this.conn = conn;
        createTableIfNotExists();
    }

    /**
     * Creates the `transaction_logs` table if it does not already exist.
     *
     * @throws SQLException if an error occurs executing the DDL
     */
    private void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS transaction_logs (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  customer_name VARCHAR(100),
                  vehicle_id INT,
                  duration_days INT,
                  revenue DOUBLE,
                  cleaning_fee DOUBLE,
                  maintenance_cost DOUBLE,
                  late_fee DOUBLE,
                  transaction_date DATE
                )
                """;
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }


    /**
     * Inserts a new transaction log entry.
     */
    public void addLog(TransactionLog log) throws SQLException {
        String sql = """
                INSERT INTO transaction_logs
                  (customer_name, vehicle_id, duration_days,
                   revenue, cleaning_fee, maintenance_cost,
                   late_fee, transaction_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, log.getCustomerName());
            ps.setInt(2, log.getVehicleId());
            ps.setInt(3, log.getDurationDays());
            ps.setDouble(4, log.getRevenue());
            ps.setDouble(5, log.getCleaningFee());
            ps.setDouble(6, log.getMaintenanceCost());
            ps.setDouble(7, log.getLateFee());
            ps.setDate(8, Date.valueOf(log.getTransactionDate()));
            ps.executeUpdate();
        }
    }

    /** Drops and recreates the transaction_logs table. */
    public void resetTable() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS transaction_logs");
        }
        createTableIfNotExists();
    }


    /**
     * Helper method to compute the sum of a numeric column across all logs.
     *
     * @param col the column name to sum (e.g., "revenue", "late_fee")
     * @return the sum of the specified column, or 0.0 if no rows
     * @throws SQLException if an error occurs during the query
     */
    private double sumColumn(String col) throws SQLException {
        String sql = "SELECT SUM(" + col + ") AS total FROM transaction_logs";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
            return 0.0;
        }
    }



    /**
     * Retrieves the total revenue from all transaction logs.
     *
     * @return total revenue sum
     * @throws SQLException if an error occurs during aggregation
     */
    public double getTotalRevenue() throws SQLException {
        return sumColumn("revenue");
    }

    /**
     * Retrieves the total cleaning fees from all transaction logs.
     *
     * @return total cleaning fees sum
     * @throws SQLException if an error occurs during aggregation
     */
    public double getTotalCleaning() throws SQLException {
        return sumColumn("cleaning_fee");
    }

    /**
     * Retrieves the total maintenance costs from all transaction logs.
     *
     * @return total maintenance cost sum
     * @throws SQLException if an error occurs during aggregation
     */
    public double getTotalMaintenance() throws SQLException {
        return sumColumn("maintenance_cost");
    }


    /**
     * Retrieves the total late fees from all transaction logs.
     *
     * @return total late fees sum
     * @throws SQLException if an error occurs during aggregation
     */
    public double getTotalLateFees() throws SQLException {
        return sumColumn("late_fee");
    }


}
