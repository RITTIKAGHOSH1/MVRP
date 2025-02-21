

package org.example;

import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class multiple100locations {
    // Earth radius in kilometers (for Haversine distance)
    public static final double EARTH_RADIUS = 6371;

    public static void main(String[] args) {
        // 1. Create a depot location.
        // For consistency with our clusters, we choose a depot in New York.
        Location depot = Location.Builder.newInstance()
                .setId("depot")
                .setCoordinate(Coordinate.newInstance(40.7128, -74.0060))
                .build();

        // 2. Generate 100 service locations spread across three clusters:
        //    - New York (34 locations), Los Angeles (33 locations), Chicago (33 locations).
        List<Location> serviceLocations = new ArrayList<>();
        Random rand = new Random(42);

        // New York cluster: center around (40.7128, -74.0060)
        for (int i = 0; i < 34; i++) {
            double lat = 40.7128 + (rand.nextDouble() - 0.5) * 0.1; // variation ±0.05°
            double lon = -74.0060 + (rand.nextDouble() - 0.5) * 0.1;
            String id = "loc" + (i + 1);
            Location loc = Location.Builder.newInstance()
                    .setId(id)
                    .setCoordinate(Coordinate.newInstance(lat, lon))
                    .build();
            serviceLocations.add(loc);
        }
        // Los Angeles cluster: center around (34.0522, -118.2437)
        for (int i = 34; i < 67; i++) {
            double lat = 34.0522 + (rand.nextDouble() - 0.5) * 0.1;
            double lon = -118.2437 + (rand.nextDouble() - 0.5) * 0.1;
            String id = "loc" + (i + 1);
            Location loc = Location.Builder.newInstance()
                    .setId(id)
                    .setCoordinate(Coordinate.newInstance(lat, lon))
                    .build();
            serviceLocations.add(loc);
        }
        // Chicago cluster: center around (41.8781, -87.6298)
        for (int i = 67; i < 100; i++) {
            double lat = 41.8781 + (rand.nextDouble() - 0.5) * 0.1;
            double lon = -87.6298 + (rand.nextDouble() - 0.5) * 0.1;
            String id = "loc" + (i + 1);
            Location loc = Location.Builder.newInstance()
                    .setId(id)
                    .setCoordinate(Coordinate.newInstance(lat, lon))
                    .build();
            serviceLocations.add(loc);
        }

        // 3. Create service jobs for each location with a fixed demand (e.g., 5).
        List<Service> services = new ArrayList<>();
        for (Location loc : serviceLocations) {
            Service service = Service.Builder.newInstance("service_" + loc.getId())
                    .setLocation(loc)
                    .addSizeDimension(0, 2)
                    .build();
            services.add(service);
        }

        // 4. Create a vehicle type (with a capacity sufficient for multiple jobs).
        VehicleType vehicleType = VehicleTypeImpl.Builder.newInstance("truck")
                .addCapacityDimension(0, 50)
                .build();

        // 5. Create several vehicles that start at the depot.
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(VehicleImpl.Builder.newInstance("v1")
                .setStartLocation(depot)
                .setType(vehicleType)
                .build());
        vehicles.add(VehicleImpl.Builder.newInstance("ve2")
                .setStartLocation(depot)
                .setType(vehicleType)
                .build());
        vehicles.add(VehicleImpl.Builder.newInstance("veh3")
                .setStartLocation(depot)
                .setType(vehicleType)
                .build());
        vehicles.add(VehicleImpl.Builder.newInstance("vehi4")
                .setStartLocation(depot)
                .setType(vehicleType)
                .build());
        vehicles.add(VehicleImpl.Builder.newInstance("vehicle5")
                .setStartLocation(depot)
                .setType(vehicleType)
                .build());

        // 6. Build a distance (cost) matrix that includes the depot and all service locations.
        // Create a list of all nodes (depot + services)
        List<Location> allLocations = new ArrayList<>();
        allLocations.add(depot);
        allLocations.addAll(serviceLocations);

        VehicleRoutingTransportCostsMatrix.Builder matrixBuilder =
                VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);
        for (Location from : allLocations) {
            for (Location to : allLocations) {
                double distance = haversine(
                        from.getCoordinate().getY(), from.getCoordinate().getX(),
                        to.getCoordinate().getY(), to.getCoordinate().getX()
                );
                matrixBuilder.addTransportDistance(from.getId(), to.getId(), distance);
            }
        }

        // 7. Build the vehicle routing problem by adding vehicles, jobs, and the cost matrix.
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        for (Vehicle v : vehicles) {
            vrpBuilder.addVehicle(v);
        }
        for (Service s : services) {
            vrpBuilder.addJob(s);
        }
        vrpBuilder.setRoutingCost(matrixBuilder.build());

        VehicleRoutingProblem problem = vrpBuilder.build();

        // 8. Solve the problem using Jsprit's algorithm.
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        System.out.println("All possible solutions: " + solutions);

        // Print the best solution to the console.
        System.out.println("The best solution:");
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
        SolutionPrinter.print(bestSolution);

        // Plot the solution.
        new Plotter(problem, bestSolution).plot("outputGraph.png", "JspritEx2experiment");
        // CustomPlotter.displaySolution(bestSolution); // Uncomment if you have CustomPlotter available.
        CustomPlotter.displaySolution(bestSolution);
        System.out.println("Number of routes: " + bestSolution.getRoutes().size());
        for (VehicleRoute route : bestSolution.getRoutes()) {
            System.out.println("Route for " + route.getVehicle().getId() + " has " + route.getActivities().size() + " stops.");
        }


    }

    // Haversine formula to compute the distance (in kilometers) between two latitude/longitude pairs.
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
