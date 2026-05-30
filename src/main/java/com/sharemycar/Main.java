package com.sharemycar;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.sharemycar.repository.BookingRepository;
import com.sharemycar.repository.TransactionLogRepository;
import com.sharemycar.repository.VehicleRepository;
import com.sharemycar.service.*;
import com.sharemycar.ui.ConsoleApp;

/**
 * Entry point for the ShareMyCar application.
 * Responsibilities:
 *   Load database configuration
 *   Establish database connection
 *   Initialize repositories and services
 *   Seed default vehicles if necessary
 *   Launch the console-based UI
 */
public class Main {

    /**
     * Main method to launch the ShareMyCar system.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        // Load DB properties from resources
        Properties props = new Properties();
        try (InputStream in = Main.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            props.load(in);
        } catch (Exception e) {
            System.err.println("Failed to load application.properties");
            e.printStackTrace();
            System.exit(1); // Exit if we cannot read configuration
        }

        // Connect to H2 embedded database
        String url = props.getProperty("jdbc.url");
        String user = props.getProperty("jdbc.user");
        String pass = props.getProperty("jdbc.pass");

        //  Connect to the H2 embedded database using try-with-resources
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {


            //  Initialize the repository (DAO) layer with the shared Connection
            VehicleRepository vehicleDao = new VehicleRepository(conn);
            BookingRepository bookingDao = new BookingRepository(conn);
            TransactionLogRepository logRepository = new TransactionLogRepository(conn);


            // Initialize the service layer, injecting the repositories
            FleetService fleetService = new FleetService(vehicleDao);
            BookingService bookingService = new BookingService(bookingDao, vehicleDao);
            ReturnService returnService = new ReturnService(bookingDao, vehicleDao, logRepository);
            ReportingService reportingService = new ReportingService(logRepository, vehicleDao);
            AdminService adminService = new AdminService(vehicleDao, bookingDao, logRepository, fleetService);

            //  Seed default vehicles if the database has no entries yet
            fleetService.initDefaultVehicles();

            // Create and start the console-based UI
            ConsoleApp app = new ConsoleApp(fleetService, bookingService, reportingService, returnService, adminService);
            app.start(); // Runs the interactive menu until user exits

        } catch (Exception e) {
            //  Handle any startup errors (e.g., DB connection failure)
            System.err.println("Startup failure: " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // Non-zero status indicates abnormal termination
        }
    }
}