package com.sharemycar.model;

import java.time.LocalDate;

/**
 * Records details when a vehicle is returned.
 */
public class ReturnRecord {

    private int bookingId;         // link back to booking
    private LocalDate returnDate;  // actual return date
    private double actualKm;       // kilometers driven
    private double lateFee;        // if any
    private double cleaningFee;    // fixed per return
    private double maintenanceCost;// based on km


    /**
     * @param bookingId       original booking ID
     * @param returnDate      date returned
     * @param actualKm        kilometers driven
     * @param lateFee         late fee charged
     * @param cleaningFee     cleaning fee
     * @param maintenanceCost maintenance cost
     */
    public ReturnRecord(int bookingId, LocalDate returnDate,
                        double actualKm, double lateFee,
                        double cleaningFee, double maintenanceCost) {
        this.bookingId = bookingId;
        this.returnDate = returnDate;
        this.actualKm = actualKm;
        this.lateFee = lateFee;
        this.cleaningFee = cleaningFee;
        this.maintenanceCost = maintenanceCost;
    }
// getters and setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getActualKm() {
        return actualKm;
    }

    public void setActualKm(double actualKm) {
        this.actualKm = actualKm;
    }

    public double getLateFee() {
        return lateFee;
    }

    public void setLateFee(double lateFee) {
        this.lateFee = lateFee;
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
}
