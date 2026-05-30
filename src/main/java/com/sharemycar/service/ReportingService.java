package com.sharemycar.service;


import com.sharemycar.repository.TransactionLogRepository;
import com.sharemycar.repository.VehicleRepository;

import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 * ReportingService generates financial and operational metrics for ShareMyCar.
 * <p>
 * It aggregates revenue, costs, and computes profit and average mileage,
 * then outputs a formatted report to the console.
 * </p>
 */
public class ReportingService {

    private final TransactionLogRepository logRepository;
    private final VehicleRepository vehicleRepository;
    private final DecimalFormat df = new DecimalFormat("0.00");

    /**
     * Constructs a ReportingService with required repository dependencies.
     *
     * @param logRepository     repository to retrieve aggregated financial data
     * @param vehicleRepository repository to retrieve mileage statistics
     */
    public ReportingService(TransactionLogRepository logRepository, VehicleRepository vehicleRepository) {
        this.logRepository = logRepository;
        this.vehicleRepository = vehicleRepository;
    }


    /**
     * Gathers financial metrics and prints a full report to the console.
     * <p>
     * Steps:
     * <ol>
     *   <li>Query total revenue from transaction logs.</li>
     *   <li>Query total cleaning, maintenance, and late fees.</li>
     *   <li>Compute total operational costs and profit.</li>
     *   <li>Query average mileage across all vehicles.</li>
     *   <li>Format and print the report.</li>
     * </ol>
     *
     * @throws SQLException if any repository query fails
     */
    public void generateReport() throws SQLException {
        double revenue = logRepository.getTotalRevenue(); // Fetch total rental revenue from logs
        double cleaning = logRepository.getTotalCleaning(); // Fetch cumulative cleaning fees
        double maintenance = logRepository.getTotalMaintenance();  // Fetch cumulative maintenance costs
        double lateFees = logRepository.getTotalLateFees(); // Fetch cumulative late fees
        double totalCosts = cleaning + maintenance + lateFees;  // Sum all operational cost components
        double profit = revenue - totalCosts; // Compute profit as revenue minus costs
        double avgMileage = vehicleRepository.getAverageMileage(); // Fetch average mileage per vehicle

        // Print the formatted financial report
        System.out.println("\n--- Financial Report ---");
        System.out.println("Total Revenue:             €" + df.format(revenue));
        System.out.println("Total Operational Costs:   €" + df.format(totalCosts));
        System.out.println("  • Cleaning Fees:         €" + df.format(cleaning));
        System.out.println("  • Maintenance Costs:     €" + df.format(maintenance));
        System.out.println("  • Late Fees:             €" + df.format(lateFees));
        System.out.println("Total Profit:              €" + df.format(profit));
        System.out.println("Average Mileage/Vehicle:   " + df.format(avgMileage) + " km");
    }

}
