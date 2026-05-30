package com.sharemycar.service;

import com.sharemycar.repository.VehicleRepository;
import com.sharemycar.model.Vehicle;

import java.sql.SQLException;
import java.util.List;

/**
 * FleetService manages business logic related to the vehicle fleet.
 * <p>
 * It handles seeding default vehicles, listing inventory, adding new vehicles,
 * and removing existing vehicles from the fleet.
 * </p>
 */
public class FleetService {

    private final VehicleRepository vehicleRepo;  // DAO dependency

    /**
     * @param vehicleRepo DAO for vehicles
     */
    public FleetService(VehicleRepository vehicleRepo) {
        this.vehicleRepo = vehicleRepo;      // store for use
    }

    /** If no vehicles exist, insert 10 defaults. */
    public void initDefaultVehicles() throws SQLException {

        // Retrieve all vehicles; if list is empty, proceed to seed defaults
        if (vehicleRepo.getAllVehicles().isEmpty()) {
            // sample defaults
            Vehicle[] defaults = {
                    new Vehicle("Toyota", "Corolla", 24000, 40, 1, true),
                    new Vehicle("Honda",  "Civic",   18500, 38, 1.2, true),
                    new Vehicle("Ford",   "Focus",   30500, 35, 0.9, true),
                    new Vehicle("BMW",    "320i",    12000, 60, 2, true),
                    new Vehicle("Audi",   "A3",      15000, 65, 2.2, true),
                    new Vehicle("VW",     "Golf",    22000, 45, 1.1, true),
                    new Vehicle("Renault","Clio",    18000, 30, 0.8, true),
                    new Vehicle("Kia",    "Rio",     14000, 28, 0.7, true),
                    new Vehicle("Hyundai","Elantra", 20000, 33, 0.85, true),
                    new Vehicle("Mazda",  "3",       17000, 37, 1, true)
            };
            // Loop through each default vehicle and add it to the repository
            for (Vehicle v : defaults) {
                vehicleRepo.addVehicle(v);  // Persist the vehicle; sets its generated ID
            }
        }
    }

    /**
     * Returns all vehicles in the fleet.
     *
     * @return list of vehicles
     * @throws SQLException on DB error
     */
    public List<Vehicle> listAllVehicles() throws SQLException {
        return vehicleRepo.getAllVehicles(); // delegate to DAO
    }

    /**
     * Adds a new vehicle to the fleet.
     */
    public void addVehicle(Vehicle v) throws SQLException {
        vehicleRepo.addVehicle(v);
    }

    /** Deletes a vehicle from the fleet. */
    public void removeVehicle(int vehicleId) throws SQLException {
        vehicleRepo.deleteVehicle(vehicleId);
    }
}
