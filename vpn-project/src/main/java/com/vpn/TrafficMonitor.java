package com.vpn;

import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;

public class TrafficMonitor extends JPanel {
    private final TimeSeries trafficSeries;

    public TrafficMonitor(String title) {
        this.trafficSeries = new TimeSeries("Bytes/sec");
        TimeSeriesCollection dataset = new TimeSeriesCollection(trafficSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title, "Time", "Bytes", dataset, false, true, false);
        XYPlot plot = chart.getXYPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        ChartPanel panel = new ChartPanel(chart);
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        Timer timer = new Timer(500, e -> repaint());
    timer.start();
    }

    public void updateTraffic(long bytes) {
        trafficSeries.addOrUpdate(new Millisecond(), bytes);
    }
}