package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SimulationResults {
    private double[][] coordinates;
    private double[] speed;
    private double[] angle;
    private double time;
}
