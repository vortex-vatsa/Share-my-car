package com.sharemycar.model;

import java.time.LocalDate;

/**
 * Represents a booking made by a customer.
 */
public class Booking {

    private int id;                  // booking ID
    private String customerName;     // who booked
    private int vehicleId;           // which vehicle
    private LocalDate startDate;     // when rental begins
    private int durationDays;        // for how many days
    private double estimatedKm;      // estimated distance
    private double estimatedCost;    // computed up front

    private boolean returned;          // has the vehicle been returned?


    /**
     * Full constructor.
     *
     * @param id            booking ID
     * @param customerName  who booked
     * @param vehicleId     vehicle booked
     * @param startDate     rental start date
     * @param durationDays  days of rental
     * @param estimatedKm   estimated kilometers
     * @param estimatedCost computed estimated cost
     */
    public Booking(int id, String customerName, int vehicleId,
                   LocalDate startDate, int durationDays,
                   double estimatedKm, double estimatedCost, boolean returned) {
        this.id = id;
        this.customerName = customerName;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.durationDays = durationDays;
        this.estimatedKm = estimatedKm;
        this.estimatedCost = estimatedCost;
        this.returned = returned;
    }

    /**
     * Constructor for new bookings (ID & returned-flag assigned by DAO).
     */
    public Booking(String customerName, int vehicleId,
                   LocalDate startDate, int durationDays,
                   double estimatedKm, double estimatedCost) {
        this(0, customerName, vehicleId,
                startDate, durationDays,
                estimatedKm, estimatedCost,
                false);
    }

    @Override
    public String toString() {
        return String.format(
                "Booking ID: %d | Customer: %s | Vehicle: %d | Start: %s | %d days | est. %.1f km | est. cost €%.2f",
                id, customerName, vehicleId, startDate, durationDays, estimatedKm, estimatedCost
        );
    }

//    getters and setters

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public double getEstimatedKm() {
        return estimatedKm;
    }

    public void setEstimatedKm(double estimatedKm) {
        this.estimatedKm = estimatedKm;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
