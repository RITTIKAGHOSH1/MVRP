package org.example;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.util.Coordinate;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class CustomPlotterinital {

    public static void displaySolution(VehicleRoutingProblemSolution solution) {
        // Prepare a dataset where each series corresponds to one vehicle's route.
        XYSeriesCollection dataset = new XYSeriesCollection();
        // Define a set of colors to differentiate vehicles.
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE};

        // Iterate over all routes in the solution.
        Collection<VehicleRoute> routes = solution.getRoutes();
        int routeIndex = 0;
        for (VehicleRoute route : routes) {
            String seriesKey = route.getVehicle().getId() + "_" + routeIndex;
            // Create a series named with the vehicle's ID.
            XYSeries series = new XYSeries(seriesKey);

            // Add the start location.
            Location startLoc = route.getStart().getLocation();
            series.add(startLoc.getCoordinate().getX(), startLoc.getCoordinate().getY());

            // Add each activity's location.
            for (TourActivity activity : route.getActivities()) {
                Location loc = activity.getLocation();
                series.add(loc.getCoordinate().getX(), loc.getCoordinate().getY());
            }

            // Add the end location.
            Location endLoc = route.getEnd().getLocation();
            series.add(endLoc.getCoordinate().getX(), endLoc.getCoordinate().getY());

            dataset.addSeries(series);
            routeIndex++;
        }

        // Create an XY line chart.
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Vehicle Routing Solution",  // Chart title
                "X",                         // X-Axis Label
                "Y",                         // Y-Axis Label
                dataset,                     // Dataset
                PlotOrientation.VERTICAL,    // Orientation
                true,                        // Legend
                true,                        // Tooltips
                false                        // URLs
        );

        // Customize the plot: assign different colors to each vehicle route.
        XYPlot plot = chart.getXYPlot();
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            plot.getRenderer().setSeriesPaint(i, colors[i % colors.length]);
        }

        // Add annotations for each location with its ID.
        for (VehicleRoute route : routes) {
            // Annotate the start location.
            Location startLoc = route.getStart().getLocation();
            addAnnotation(plot, startLoc);

            // Annotate each activity's location.
            for (TourActivity activity : route.getActivities()) {
                addAnnotation(plot, activity.getLocation());
            }

            // Annotate the end location.
            Location endLoc = route.getEnd().getLocation();
            addAnnotation(plot, endLoc);
        }

        // Create and show the chart in a JFrame.
        ChartPanel panel = new ChartPanel(chart);
        JFrame frame = new JFrame("Custom Vehicle Routing Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Helper method to add an annotation at a location's coordinate.
    private static void addAnnotation(XYPlot plot, Location loc) {
        Coordinate coord = loc.getCoordinate();
        XYTextAnnotation annotation = new XYTextAnnotation(loc.getId(), coord.getX(), coord.getY());
        annotation.setFont(new Font("SansSerif", Font.BOLD, 12));
        annotation.setPaint(Color.BLACK);
        plot.addAnnotation(annotation);
    }

    // For testing, you can call this method from your main method after obtaining the solution:

}

