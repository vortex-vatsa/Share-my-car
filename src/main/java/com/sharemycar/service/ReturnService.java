package com.sharemycar.service;

import com.sharemycar.repository.BookingRepository;
import com.sharemycar.repository.TransactionLogRepository;
import com.sharemycar.repository.VehicleRepository;
import com.sharemycar.model.Booking;
import com.sharemycar.model.ReturnRecord;
import com.sharemycar.model.TransactionLog;
import com.sharemycar.model.Vehicle;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * ReturnService handles processing of vehicle returns.
 * <p>
 * It calculates late fees, cleaning fees, and maintenance costs,
 * updates vehicle mileage and availability, marks bookings returned,
 * and logs a TransactionLog entry.
 * </p>
 */
public class ReturnService {

    private static final double LATE_FEE_PER_DAY = 10;   // €10 per day late fee
    private static final double CLEANING_FEE = 20;  // Flat €20 cleaning fee
    private static final double MAINTENANCE_THRESHOLD = 10_000; // Km threshold for maintenance

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final TransactionLogRepository logRepository;

    /**
     * Constructs ReturnService with required repositories.
     *
     * @param bookingRepository DAO to fetch and update booking records
     * @param vehicleRepository DAO to fetch and update vehicle records
     * @param logRepository     DAO to insert TransactionLog entries
     */
    public ReturnService(BookingRepository bookingRepository,
                         VehicleRepository vehicleRepository,
                         TransactionLogRepository logRepository) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.logRepository = logRepository;
    }

    /**
     * Processes a vehicle return, calculates fees, updates state, and logs the transaction.
     * <p>
     * Steps:
     * <ol>
     *   <li>Fetch the booking by ID and validate it exists and is not already returned.</li>
     *   <li>Calculate elapsed days and late days based on booking start date.</li>
     *   <li>Compute late fees and fixed cleaning fee.</li>
     *   <li>Fetch the vehicle, compute new mileage and maintenance cost.</li>
     *   <li>Determine if vehicle crosses maintenance threshold and flag availability.</li>
     *   <li>Update vehicle availability and mileage in the database.</li>
     *   <li>Mark the booking as returned and update in the database.</li>
     *   <li>Create and persist a TransactionLog entry with all financial details.</li>
     *   <li>Return a ReturnRecord summary object.</li>
     * </ol>
     *
     * @param bookingId ID of the booking to process return for
     * @param actualKm  actual kilometers driven during rental
     * @return a ReturnRecord containing dates, fees, and costs
     * @throws SQLException              if any database operation fails
     * @throws IllegalArgumentException  if booking not found
     * @throws IllegalStateException     if booking is already marked returned
     */
    public ReturnRecord processReturn(int bookingId, double actualKm)
            throws SQLException {
        // Retrieve booking by ID
        Booking b = bookingRepository.getBookingById(bookingId);
        // No booking found with given ID
        if (b == null) throw new IllegalArgumentException("Booking not found");
        // Ensure booking has not already been returned
        if (b.isReturned()) throw new IllegalStateException("Already returned");

        //  Calculate today's date and elapsed days since rental start
        LocalDate today = LocalDate.now();
        long elapsed = ChronoUnit.DAYS.between(b.getStartDate(), today);
        //  Compute late days beyond reserved duration
        long lateDays = Math.max(0, elapsed - b.getDurationDays());

        //  Calculate late fee and assign fixed cleaning fee
        double lateFee = lateDays * LATE_FEE_PER_DAY;
        double cleaningFee = CLEANING_FEE;

        //  Fetch vehicle to update mileage and availability
        Vehicle v = vehicleRepository.getVehicleById(b.getVehicleId());
        double oldMileage = v.getMileage();
        double newMileage = oldMileage + actualKm;
        //  Compute maintenance cost based on kilometers driven
        double maintenanceCost = actualKm * v.getMaintenanceCostPerKm();

        // maintenance scheduling check:
        boolean needsMaintenance =
                Math.floor(oldMileage / MAINTENANCE_THRESHOLD)
                        < Math.floor(newMileage / MAINTENANCE_THRESHOLD);

        // update availability & mileage:
        vehicleRepository.updateVehicleAvailability(v.getId(), !needsMaintenance);
        // Update vehicle mileage in database
        vehicleRepository.updateVehicleMileage(v.getId(), newMileage);

        // mark booking returned
        bookingRepository.markReturned(b.getId());
        b.setReturned(true);

        // log the financials
        TransactionLog log = new TransactionLog(
                0,
                b.getCustomerName(),
                b.getVehicleId(),
                b.getDurationDays(),
                b.getEstimatedCost(),
                cleaningFee,
                maintenanceCost,
                lateFee,
                today
        );
        //  Persist the transaction log to the database
        logRepository.addLog(log);


        //  If maintenance is required, notify via console
        if (needsMaintenance) {
            System.out.println(
                    "⚙ Vehicle #" + v.getId() +
                            " exceeded " + (int) MAINTENANCE_THRESHOLD +
                            " km – marked unavailable for maintenance."
            );
        }

        //  Return a summary ReturnRecord object
        return new ReturnRecord(
                b.getId(), today, actualKm, lateFee, cleaningFee, maintenanceCost
        );
    }
}
