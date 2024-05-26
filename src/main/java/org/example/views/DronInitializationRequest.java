package org.example.views;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DronInitializationRequest  {
    private double[] coordsY;
    private double[] coordsX;
    private double weight;
    private double J;
    private double cx1;
    private double cx2;
    private double mx3;
}
