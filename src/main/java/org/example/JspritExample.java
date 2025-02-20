package org.example;

import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import java.util.Collection;

public class JspritExample {
    public static void main(String[] args) {
        // Create locations with varying y-coordinates to avoid a zero range.
        Location depot = Location.Builder.newInstance()
                .setId("depot")
                .setCoordinate(Coordinate.newInstance(0.0, 0.0))
                .build();
        Location location1 = Location.Builder.newInstance()
                .setId("location1")
                .setCoordinate(Coordinate.newInstance(10.0, 5.0)) // y = 5.0
                .build();
        Location location2 = Location.Builder.newInstance()
                .setId("location2")
                .setCoordinate(Coordinate.newInstance(20.0, 8.0))
                .build();

        // Create a vehicle type with capacity dimension 0 = 10
        VehicleType vehicleType = VehicleTypeImpl.Builder.newInstance("vehicleType")
                .addCapacityDimension(0, 10)
                .build();

        // Create a vehicle that starts at the depot location
        Vehicle vehicle = VehicleImpl.Builder.newInstance("vehicle")
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

        // Build the vehicle routing problem instance using a cost matrix
        VehicleRoutingProblem problem = VehicleRoutingProblem.Builder.newInstance()
                .addVehicle(vehicle)
                .addJob(service1)
                .addJob(service2)
                .setRoutingCost(VehicleRoutingTransportCostsMatrix.Builder.newInstance(true)
                        .addTransportDistance("depot", "location1", 10)
                        .addTransportDistance("location1", "location2", 15)
                        .addTransportDistance("location2", "depot", 20)
                        .build())
                .build();

        // Solve the problem using Jsprit's algorithm
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

        // Print the best solution to the console
        SolutionPrinter.print(Solutions.bestOf(solutions));

        // Plot the solution (the Plotter now has a valid range)
        new Plotter(problem, Solutions.bestOf(solutions)).plot("output.png", "Jsprit Example");

    }
}