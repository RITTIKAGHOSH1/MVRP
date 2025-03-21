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

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import java.util.Collection;

public class CustomPlotter {

    /**
     * Displays the VRP solution in a window using an XY line chart.
     * Each vehicle's route is plotted as a separate colored series,
     * and each location is annotated with its ID.
     *
     * @param solution the VRP solution to be visualized.
     */
    public static void displaySolution(VehicleRoutingProblemSolution solution) {
        // Prepare a dataset where each series corresponds to one vehicle's route.
        XYSeriesCollection dataset = new XYSeriesCollection();
        // Define an array of colors to differentiate routes.
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.PINK, Color.YELLOW};

        // Iterate over all routes in the solution.
        Collection<VehicleRoute> routes = solution.getRoutes();
        int routeIndex = 0;
        for (VehicleRoute route : routes) {
            String seriesKey = route.getVehicle().getId() + "_" + routeIndex;
            // Create a series for the route.
            XYSeries series = new XYSeries(seriesKey);

            // Add the start (depot) location.
            Location startLoc = route.getStart().getLocation();
            series.add(startLoc.getCoordinate().getX(), startLoc.getCoordinate().getY());

            // Add each activity location (service stops).
            for (TourActivity activity : route.getActivities()) {
                Location loc = activity.getLocation();
                series.add(loc.getCoordinate().getX(), loc.getCoordinate().getY());
            }

            // Add the end location.
            Location endLoc = route.getEnd().getLocation();
            series.add(endLoc.getCoordinate().getX(), endLoc.getCoordinate().getY());

            // Add this series to the dataset.
            dataset.addSeries(series);
            routeIndex++;
        }

        // Create an XY line chart.
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Vehicle Routing Solution",   // Chart title
                "X",                          // X-Axis label
                "Y",                          // Y-Axis label
                dataset,                      // Dataset
                PlotOrientation.VERTICAL,     // Plot orientation
                true,                         // Include legend
                true,                         // Tooltips
                false                         // URLs
        );

        // Customize the plot by setting different colors for each series.
        XYPlot plot = chart.getXYPlot();
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            plot.getRenderer().setSeriesPaint(i, colors[i % colors.length]);
        }

        // Add annotations (location IDs) for every point.
        for (VehicleRoute route : routes) {
            // Annotate the start location.
            addAnnotation(plot, route.getStart().getLocation());

            // Annotate each service activity's location.
            for (TourActivity activity : route.getActivities()) {
                addAnnotation(plot, activity.getLocation());
            }

            // Annotate the end location.
            addAnnotation(plot, route.getEnd().getLocation());
        }

        // Create a chart panel and a frame to display the chart.
        ChartPanel panel = new ChartPanel(chart);
        JFrame frame = new JFrame("Custom Vehicle Routing Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null); // center on screen
        frame.setVisible(true);
    }

    /**
     * Adds an annotation to the plot at the given location.
     *
     * @param plot the XYPlot where the annotation will be added.
     * @param loc  the location whose ID and coordinates will be annotated.
     */
    private static void addAnnotation(XYPlot plot, Location loc) {
        Coordinate coord = loc.getCoordinate();
        XYTextAnnotation annotation = new XYTextAnnotation(loc.getId(), coord.getX(), coord.getY());
        annotation.setFont(new Font("SansSerif", Font.BOLD, 12));
        annotation.setPaint(Color.BLACK);
        plot.addAnnotation(annotation);
    }
}
