package org.example;

import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import java.util.Collection;

public class JspritEx2experiment {
    public static void main(String[] args) {
        // Create locations with varying y-coordinates to avoid a zero range.
        Location depot = Location.Builder.newInstance()
                .setId("depot")
                .setCoordinate(Coordinate.newInstance(0.0, 0.0))
                .build();
        Location location1 = Location.Builder.newInstance()
                .setId("location1")
                .setCoordinate(Coordinate.newInstance(10.0, 10.0)) // y = 5.0
                .build();
        Location location2 = Location.Builder.newInstance()
                .setId("location2")
                .setCoordinate(Coordinate.newInstance(20.0, 5.0))
                .build();
        Location location3 = Location.Builder.newInstance()
                .setId("location3")
                .setCoordinate(Coordinate.newInstance(15.0, 15.0))
                .build();
        Location location4 = Location.Builder.newInstance()
                .setId("location4")
                .setCoordinate(Coordinate.newInstance(13.0, 10.0))
                .build();
        Location location5 = Location.Builder.newInstance()
                .setId("location5")
                .setCoordinate(Coordinate.newInstance(25.0, 20.0))
                .build();

        // Create a vehicle type with capacity dimension 0 = 10
        VehicleType vehicleType = VehicleTypeImpl.Builder.newInstance("Toto")
                .addCapacityDimension(0, 15)
                .build();

        // Create a vehicle that starts at the depot location
        Vehicle vehicle1 = VehicleImpl.Builder.newInstance("vehicle1")
                .setStartLocation(depot)
                .setType(vehicleType)
                .build();
        Vehicle vehicle2 = VehicleImpl.Builder.newInstance("vehicle2")
                .setStartLocation(depot)
                .setType(vehicleType)
                .build();
        Vehicle vehicle3 = VehicleImpl.Builder.newInstance("vehicle3")
                .setStartLocation(depot)
                .setType(vehicleType)
                .build();

        // Create two service jobs with a demand of 5 each (using capacity dimension 0)
        Service service1 = Service.Builder.newInstance("service1")
                .setLocation(location1)
                .addSizeDimension(0, 5)
                .build();

        Service service2 = Service.Builder.newInstance("service2")
                .setLocation(location2)
                .addSizeDimension(0, 5)
                .build();
        Service service3 = Service.Builder.newInstance("service3")
                .setLocation(location3)
                .addSizeDimension(0, 5)
                .build();

        Service service4 = Service.Builder.newInstance("service4")
                .setLocation(location4)
                .addSizeDimension(0, 5)
                .build();
        Service service5 = Service.Builder.newInstance("service5")
                .setLocation(location5)
                .addSizeDimension(0, 5)
                .build();


        // Build the vehicle routing problem instance using a cost matrix
        VehicleRoutingProblem problem = VehicleRoutingProblem.Builder.newInstance()
                .addVehicle(vehicle1)
                .addVehicle(vehicle2)
                .addVehicle(vehicle3)
                .addJob(service1)
                .addJob(service2)
                .addJob(service3)
                .addJob(service4)
                .addJob(service5)
                .setRoutingCost(VehicleRoutingTransportCostsMatrix.Builder.newInstance(true)
                        .addTransportDistance("depot", "location1", 14)
                        .addTransportDistance("depot", "location2", 21)
                        .addTransportDistance("depot", "location3", 21)
                        .addTransportDistance("depot", "location4", 32)
                        .addTransportDistance("depot", "location5", 32)

                        // And for the reverse directions (if needed, though 'symmetric' may handle it):
                        .addTransportDistance("A", "depot", 14)
        .addTransportDistance("location2", "depot", 21)
                        .addTransportDistance("location3", "depot", 21)
        .addTransportDistance("location4", "depot", 32)
        .addTransportDistance("location5", "depot", 32)

                        .addTransportDistance("location1", "location2", 11)
                        .addTransportDistance("location1", "location3", 7)
                        .addTransportDistance("location1", "location4", 20)
                        .addTransportDistance("location1", "location5", 18)
                        .addTransportDistance("location2", "location3", 11)
                        .addTransportDistance("location2", "location4", 11)
                        .addTransportDistance("location2", "location5", 16)
                        .addTransportDistance("location3", "location4", 16)
                        .addTransportDistance("location3", "location5", 11)
                        .addTransportDistance("location4", "location5", 11)
                        .build())
                .build();

        // Solve the problem using Jsprit's algorithm
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        System.out.println("All possible solutions: "+solutions);
        // Print the best solution to the console
        System.out.println("The best sol: ");
        VehicleRoutingProblemSolution bestSolution=Solutions.bestOf(solutions);
        SolutionPrinter.print(bestSolution);

        // Plot the solution (the Plotter now has a valid range)
        new Plotter(problem, Solutions.bestOf(solutions)).plot("outputGraph.png", "JspritEx2experiment");
CustomPlotter.displaySolution(bestSolution);
    }
}