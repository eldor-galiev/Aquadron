package org.example.views;

import lombok.Getter;
import org.example.models.Aquadron;

@Getter
public class DronState {
    private final double[] y;
    private final double[] x;
    private final double weight;
    private final double J;
    private final double cx1;
    private final double cx2;
    private final double mx3;

    public DronState(Aquadron aquadron) {
        y = aquadron.getY().getColumn(0);
        x = aquadron.getX().getColumn(0);
        weight = aquadron.getWeight();
        J = aquadron.getJ();
        cx1 = aquadron.getCx1();
        cx2 = aquadron.getCx2();
        mx3 = aquadron.getMx3();
    }
}
