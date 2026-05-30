package com.sharemycar.service;

import com.sharemycar.repository.BookingRepository;
import com.sharemycar.repository.TransactionLogRepository;
import com.sharemycar.repository.VehicleRepository;

import java.sql.SQLException;

/**
 * AdminService provides administrative operations such as resetting the database.
 * <p>
 * It coordinates between repositories and the fleet service to drop and recreate
 * all tables, then reseed the default data set (10 vehicles).
 * </p>
 */
public class AdminService {

    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final TransactionLogRepository logRepository;
    private final FleetService fleetService;

    /**
     * Constructs the AdminService with required dependencies.
     *
     * @param vehicleRepository repository responsible for vehicle CRUD operations
     * @param bookingRepository repository responsible for booking CRUD operations
     * @param logRepository     repository responsible for transaction log operations
     * @param fleetService      service that can seed default vehicles into the fleet
     */
    public AdminService(VehicleRepository vehicleRepository,
                        BookingRepository bookingRepository,
                        TransactionLogRepository logRepository,
                        FleetService fleetService) {
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
        this.logRepository = logRepository;
        this.fleetService = fleetService;
    }



    /**
     * Resets the entire database to a clean state.
     * <p>
     * This method will:
     * <ol>
     *   <li>Drop and recreate the `vehicles` table.</li>
     *   <li>Drop and recreate the `bookings` table.</li>
     *   <li>Drop and recreate the `transaction_logs` table.</li>
     *   <li>Seed the default set of vehicles (10 entries).</li>
     * </ol>
     *
     * @throws SQLException if any repository operation fails
     */
    public void resetDatabase() throws SQLException {
        vehicleRepository.resetTable();   // drop & recreate vehicles table
        bookingRepository.resetTable();   // drop & recreate bookings table
        logRepository.resetTable();     // drop & recreate transaction_logs table
        fleetService.initDefaultVehicles(); // insert 10 default vehicles
    }

}
