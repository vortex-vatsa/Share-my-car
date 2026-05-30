package com.sharemycar.service;

import com.sharemycar.model.Booking;
import com.sharemycar.model.Vehicle;
import com.sharemycar.repository.BookingRepository;
import com.sharemycar.repository.VehicleRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


/**
 * BookingService handles all booking-related business logic.
 * <p>
 * It validates vehicle availability, calculates estimated cost,
 * creates bookings, and locks vehicles during rental periods.
 * </p>
 */
public class BookingService {

    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;


    /**
     * Constructs BookingService with required repository dependencies.
     *
     * @param bookingDao repository for booking persistence
     * @param vehicleDao repository for vehicle data
     */
    public BookingService(BookingRepository bookingDao, VehicleRepository vehicleDao) {
        this.bookingRepository = bookingDao;
        this.vehicleRepository = vehicleDao;
    }


    /**
     * Creates a booking (if vehicle is available), locks the vehicle,
     * and returns the new Booking.
     *
     * @throws IllegalArgumentException if vehicle not found
     * @throws IllegalStateException    if vehicle unavailable
     * @throws SQLException             on DB error
     */
    public Booking createBooking(String customerName,
                                 int vehicleId,
                                 int durationDays,
                                 double estimatedKm)
            throws SQLException {
        // Retrieve the vehicle from the repository
        Vehicle v = vehicleRepository.getVehicleById(vehicleId);
        if (v == null) {
            // Vehicle not found in database
            throw new IllegalArgumentException("Vehicle ID not found");
        }
        if (!v.isAvailable()) {
            // Vehicle already booked or under maintenance
            throw new IllegalStateException("Vehicle is currently unavailable");
        }

        // cost = days×dailyPrice + km×costPerKm
        double cost = durationDays * v.getDailyPrice()
                + estimatedKm * v.getMaintenanceCostPerKm();

        // Create new Booking object with current date as start date
        Booking b = new Booking(customerName, vehicleId,
                LocalDate.now(),
                durationDays, estimatedKm, cost);
        // Persist booking to database (ID will be auto-generated)
        bookingRepository.addBooking(b);
        // Lock the vehicle so it cannot be double-booked
        vehicleRepository.updateVehicleAvailability(vehicleId, false);
        return b;  // Return the created booking
    }


    /**
     * Fetches a booking by its ID.
     */
    public Booking getBookingById(int id) throws SQLException {
        return bookingRepository.getBookingById(id);
    }

    /**
     * Returns all bookings in the system.
     */
    public List<Booking> listAllBookings() throws SQLException {
        return bookingRepository.getAllBookings();
    }

}
