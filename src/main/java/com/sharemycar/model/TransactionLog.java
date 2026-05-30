package com.sharemycar.model;

import java.time.LocalDate;

/**
 * Logs the financial details of each completed rental.
 */
public class TransactionLog {

    private int id;                   // log ID
    private String customerName;      // who rented
    private int vehicleId;            // which vehicle
    private int durationDays;         // days rented
    private double revenue;           // rental + distance fees
    private double cleaningFee;       // cleaning
    private double maintenanceCost;   // maintenance
    private double lateFee;           // if returned late
    private LocalDate transactionDate;// date of return

    /**
     * Constructs a TransactionLog with all financial details of a completed rental.
     *
     * @param id               unique log ID (0 if new; DB will assign a value)
     * @param customerName     name of the customer who rented the vehicle
     * @param vehicleId        ID of the rented vehicle
     * @param durationDays     number of days the vehicle was rented
     * @param revenue          total revenue from the rental (day + km fees)
     * @param cleaningFee      cleaning fee applied upon return
     * @param maintenanceCost  maintenance cost based on kilometers driven
     * @param lateFee          late fee applied for overdue return
     * @param transactionDate  date when the vehicle was returned and logged
     */
    public TransactionLog(int id, String customerName, int vehicleId,
                          int durationDays, double revenue,
                          double cleaningFee, double maintenanceCost,
                          double lateFee, LocalDate transactionDate) {
        this.id = id;
        this.customerName = customerName;
        this.vehicleId = vehicleId;
        this.durationDays = durationDays;
        this.revenue = revenue;
        this.cleaningFee = cleaningFee;
        this.maintenanceCost = maintenanceCost;
        this.lateFee = lateFee;
        this.transactionDate = transactionDate;
    }
// getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public double getCleaningFee() {
        return cleaningFee;
    }

    public void setCleaningFee(double cleaningFee) {
        this.cleaningFee = cleaningFee;
    }

    public double getMaintenanceCost() {
        return maintenanceCost;
    }

    public void setMaintenanceCost(double maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }

    public double getLateFee() {
        return lateFee;
    }

    public void setLateFee(double lateFee) {
        this.lateFee = lateFee;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
}
