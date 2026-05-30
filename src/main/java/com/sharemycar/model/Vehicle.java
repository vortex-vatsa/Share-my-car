package com.sharemycar.model;

import java.time.LocalDate;

/**
 * Represents a vehicle in the ShareMyCar fleet.
 */
public class Vehicle {
    // Unique identifier for this vehicle
    private int id;
    // Manufacturer brand name of the vehicle
    private String brand;
    // Model name of the vehicle
    private String model;
    // Total kilometers driven by the vehicle
    private double mileage;
    // Rental price per day in euros
    private double dailyPrice;
    // Maintenance cost per kilometer in euros
    private double maintenanceCostPerKm;
    // Availability status: true if available, false if not
    private boolean available;

    /**
     * Full constructor.
     *
     * @param id                   unique vehicle ID
     * @param brand                manufacturer
     * @param model                model name
     * @param mileage              kilometers driven
     * @param dailyPrice           rental price per day
     * @param maintenanceCostPerKm cost of maintenance per km
     * @param available            availability flag
     */
    public Vehicle(int id, String brand, String model, double mileage,
                   double dailyPrice, double maintenanceCostPerKm,
                   boolean available) {
        this.id = id;                               // set ID
        this.brand = brand;                         // set brand
        this.model = model;                         // set model
        this.mileage = mileage;                     // set mileage
        this.dailyPrice = dailyPrice;               // set price/day
        this.maintenanceCostPerKm = maintenanceCostPerKm; // set cost/km
        this.available = available;                 // set availability
    }


    /**
     * Constructor for new vehicles (ID will be assigned by DB).
     */
    public Vehicle(String brand, String model, double mileage,
                   double dailyPrice, double maintenanceCostPerKm,
                   boolean available) {
        this(0, brand, model, mileage, dailyPrice, maintenanceCostPerKm, available);
    }

    /**
     * @return vehicle ID
     */
    public int getId() {
        return id;
    }

    /**
     * @param id new vehicle ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @param brand new brand
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * @return model
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model new model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return mileage
     */
    public double getMileage() {
        return mileage;
    }

    /**
     * @param mileage new mileage
     */
    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    /**
     * @return daily rental price
     */
    public double getDailyPrice() {
        return dailyPrice;
    }

    /**
     * @param dailyPrice new daily price
     */
    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    /**
     * @return maintenance cost per km
     */
    public double getMaintenanceCostPerKm() {
        return maintenanceCostPerKm;
    }

    /**
     * @param maintenanceCostPerKm new cost per km
     */
    public void setMaintenanceCostPerKm(double maintenanceCostPerKm) {
        this.maintenanceCostPerKm = maintenanceCostPerKm;
    }

    /**
     * @return availability
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * @param available new availability flag
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        // Nicely format for console output
        return String.format(
                "ID: %d | %s %s | Mileage: %.1f km | €%.2f/day | €%.2f/km | %s",
                id, brand, model, mileage, dailyPrice,
                maintenanceCostPerKm,
                available ? "Available" : "Unavailable"
        );
    }

}
