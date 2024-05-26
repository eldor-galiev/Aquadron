package org.example.views;

import lombok.Getter;

import java.util.List;

@Getter
public class SimulationResults {
    private double[][] coordinates;
    private double[] speed;
    private double[] angle;
    private double time;

    public SimulationResults(List<double[]> states, double stopTime) {
        coordinates = states.stream()
                .map(arr -> new double[]{arr[0], arr[1]})
                .toArray(double[][]::new);
        speed = states.stream()
                .map(array ->  array[2]).toList().stream()
                .mapToDouble(Double::doubleValue).toArray();
        angle = states.stream()
                .map(array ->  array[3]).toList().stream()
                .mapToDouble(Double::doubleValue).toArray();
        time = stopTime;
    }
}
