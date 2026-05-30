package com.sharemycar.ui;


import com.sharemycar.model.Booking;
import com.sharemycar.model.ReturnRecord;
import com.sharemycar.model.Vehicle;
import com.sharemycar.service.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * ConsoleApp provides a simple text-based user interface for the ShareMyCar system.
 * <p>
 * It repeatedly displays a menu, reads user selections, and delegates actions
 * to the various services (fleet, booking, return processing, reporting, admin).
 * </p>
 */
public class ConsoleApp {

    private final FleetService fleetService;  // Service for fleet operations

    private final BookingService bookingService;  // Service for booking operations

    private final ReturnService returnService;  // Service for processing returns
    private final ReportingService reportingService; // Service for generating reports

    private final AdminService adminService;  // Service for admin tasks
    private final Scanner scanner = new Scanner(System.in); // Scanner to read user input


    /**
     * Constructs the ConsoleApp with injected service dependencies.
     *
     * @param fleetService     service handling vehicle inventory and seeding
     * @param bookingService   service handling booking creation and retrieval
     * @param reportingService service generating financial reports
     * @param returnService    service processing vehicle returns
     * @param adminService     service performing administrative tasks (reset DB)
     */
    public ConsoleApp(FleetService fleetService,
                      BookingService bookingService,
                      ReportingService reportingService,
                      ReturnService returnService,
                      AdminService adminService

    ) {
        this.fleetService = fleetService;
        this.bookingService = bookingService;
        this.reportingService = reportingService;
        this.returnService = returnService;
        this.adminService = adminService;
    }

    /**
     * Starts the main menu loop.
     * <p>
     * Continuously displays options, reads user choice, and invokes the corresponding action.
     * Handles SQL and state-related exceptions by printing error messages.
     * </p>
     */
    public void start() {
        while (true) {   // Loop indefinitely until exit
            // display options
            System.out.println("\nPlease choose an option:");
            System.out.println(" 1) View full vehicle inventory");
            System.out.println(" 2) Add a new vehicle");
            System.out.println(" 3) Book a vehicle");
            System.out.println(" 4) Return a vehicle");
            System.out.println(" 5) Generate financial report");
            System.out.println(" 6) View booking details");
            System.out.println(" 7) Delete a vehicle (Admin control)");
            System.out.println(" 8) Reset database (clean slate)");
            System.out.println(" 9) Exit");
            System.out.print("Enter choice [1–9]: ");

            String choice = scanner.nextLine(); // read selection

            try {
                switch (choice) {      // Dispatch based on choice
                    case "1" -> showInventory();
                    case "2" -> addNewVehicle();
                    case "3" -> createBooking();
                    case "4" -> processReturn();
                    case "5" -> reportingService.generateReport();
                    case "6" -> viewAllBookings();
                    case "7" -> deleteVehicle();
                    case "8" -> resetDatabase();
                    case "9" -> {
                        System.out.println("ShareMyCar system shutting down. Goodbye!");
                        return; // exit loop
                    }
                    default -> System.out.println("✔ Option not yet implemented.");
                }
            } catch (SQLException e) {     // Catch JDBC errors
                System.err.println("Database error: " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                // Catch domain errors like invalid ID or already returned
                System.err.println("⚠ " + e.getMessage());
            }
        }

    }



    /**
     * Fetches and displays all vehicles in the fleet.
     * <p>
     * Delegates to FleetService to retrieve a list of Vehicle objects,
     * then prints each one using its toString() method.
     * </p>
     *
     * @throws SQLException if a database access error occurs
     */
    private void showInventory() throws SQLException {
        List<Vehicle> all = fleetService.listAllVehicles(); // get all vehicles
        System.out.println("\n--- Vehicle Inventory ---");  // If no vehicles
        if (all.isEmpty()) {
            System.out.println("  (no vehicles yet)");
        } else {
            for (Vehicle v : all) {      // Print each vehicle
                System.out.println("  " + v); // uses Vehicle.toString()
            }
        }
    }

    /**
     * Prompts the user for new vehicle details and adds it to the fleet.
     * <p>
     * Reads brand, model, mileage, daily price, and cost per km from console,
     * constructs a Vehicle object, and calls FleetService.addVehicle().
     * </p>
     *
     * @throws SQLException if a database access error occurs
     */
    private void addNewVehicle() throws SQLException {
        //  Read user added values
        System.out.print("Enter brand: ");
        String brand = scanner.nextLine().trim();

        System.out.print("Enter model: ");
        String model = scanner.nextLine().trim();

        System.out.print("Enter starting mileage (km): ");
        double mileage = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Enter daily rental price (€): ");
        double dailyPrice = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Enter maintenance cost per km (€): ");
        double costPerKm = Double.parseDouble(scanner.nextLine().trim());

        Vehicle v = new Vehicle(brand, model, mileage, dailyPrice, costPerKm, true);
        fleetService.addVehicle(v);

        System.out.println("\nVehicle added!");  // Save data to DB
        System.out.println("  " + v);   // Display the added vehicle
    }

    /**
     * Guides the booking creation process.
     * <p>
     * Prompts for customer name, vehicle ID, duration, and estimated kms.
     * Calls BookingService.createBooking() and prints the resulting Booking.
     * </p>
     *
     * @throws SQLException if a database access error occurs
     */
    private void createBooking() throws SQLException {
        //  Read user added booking values
        System.out.print("Enter your name: ");
        String customer = scanner.nextLine().trim();

        System.out.print("Enter Vehicle ID to book: ");
        int vid = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter rental duration (days): ");
        int days = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter estimated kilometers to drive: ");
        double km = Double.parseDouble(scanner.nextLine().trim());

        Booking b = bookingService.createBooking(customer, vid, days, km); // Create booking
        System.out.println("\nBooking created!");
        System.out.println("  " + b);    // Display booking info
    }


    /**
     * Processes a vehicle return and displays the fee summary.
     * <p>
     * Prompts for booking ID and actual kilometers driven,
     * invokes ReturnService.processReturn(),
     * then retrieves the Booking to calculate revenue vs. costs.
     * </p>
     *
     * @throws SQLException if a database access error occurs
     */
    private void processReturn() throws SQLException {
        System.out.print("Enter Booking ID to return: ");
        int bid = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter actual kilometers driven: ");
        double km = Double.parseDouble(scanner.nextLine().trim());

        ReturnRecord rr = returnService.processReturn(bid, km);
        Booking b = bookingService.getBookingById(bid);

        System.out.println("\n--- Return Summary ---");
        System.out.println("Rental Revenue:    €" + String.format("%.2f", b.getEstimatedCost()));
        System.out.println("Late Fee:          €" + String.format("%.2f", rr.getLateFee()));
        System.out.println("Cleaning Fee:      €" + String.format("%.2f", rr.getCleaningFee()));
        System.out.println("Maintenance Cost:  €" + String.format("%.2f", rr.getMaintenanceCost()));
        double totalExtras = rr.getLateFee() + rr.getCleaningFee() + rr.getMaintenanceCost();  // Sum extras
        System.out.println("Total Additional:  €" + String.format("%.2f", totalExtras));
        System.out.println("Profit This Rental:€" + String.format("%.2f", b.getEstimatedCost() - totalExtras));  // Profit
    }

    /**
     * Deletes a vehicle from the inventory by ID.
     * <p>
     * Prompts for vehicle ID and calls FleetService.removeVehicle().
     * </p>
     *
     * @throws SQLException if a database access error occurs
     */
    private void deleteVehicle() throws SQLException {
        System.out.print("Enter Vehicle ID to delete: ");
        int vid = Integer.parseInt(scanner.nextLine().trim());
        fleetService.removeVehicle(vid);
        System.out.println("✅ Vehicle #" + vid + " deleted.");
    }

    /**
     * Displays all bookings in the system.
     * <p>
     * Calls BookingService.listAllBookings() and prints each Booking.
     * </p>
     *
     * @throws SQLException if a database access error occurs
     */
    private void viewAllBookings() throws SQLException {
        List<Booking> bookings = bookingService.listAllBookings();
        System.out.println("\n--- All Bookings ---");
        if (bookings.isEmpty()) {
            System.out.println("  (no bookings yet)");
        } else {
            for (Booking b : bookings) {
                System.out.println("  " + b);
            }
        }
    }

    /**
     * Resets the entire database after user confirmation.
     * <p>
     * Prompts user with a warning, then calls AdminService.resetDatabase()
     * if confirmed, which drops and recreates all tables and reseeds defaults.
     * </p>
     *
     * @throws SQLException if a database access error occurs
     */
    private void resetDatabase() throws SQLException {
        System.out.print("⚠ This will wipe ALL data. Are you sure? (Y/N): ");
        String ans = scanner.nextLine().trim().toLowerCase(); // Read confirmation
        if (ans.equals("Y") || ans.equals("yes") || ans.equals("y")) {
            adminService.resetDatabase();   // Perform reset
            System.out.println("✅ Database reset complete; default vehicles re-seeded.");
        } else {
            System.out.println("✘ Operation cancelled."); // Abort reset
        }
    }
}
