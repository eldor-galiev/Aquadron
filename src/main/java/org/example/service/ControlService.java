package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.example.domain.Aquadron;
import org.example.domain.ODEFunction;
import org.springframework.stereotype.Service;

import java.util.stream.DoubleStream;

@Service
@RequiredArgsConstructor
public class ControlService {

    private final Aquadron aquadron;

    public void simulate(double[] startX, double[] startY, double t0, double t, double step) {
        FirstOrderIntegrator rk = new ClassicalRungeKuttaIntegrator(step);
        FirstOrderDifferentialEquations ode = new ODEFunction(this.aquadron);
        StepHandler stepHandler = new StepHandler() {
            public void init(double t0, double[] y0, double t) {
            }

            public void handleStep(StepInterpolator interpolator, boolean isLast) {
                double   t = interpolator.getCurrentTime();
                double[] y = interpolator.getInterpolatedState();
                System.out.println("t = " + t + ": " + y[0] + " " + y[1] + " " + y[2] + " " + y[3] + " " + y[4] + " " + y[5]);
            }
        };

        rk.addStepHandler(stepHandler);
        double[] y0 = DoubleStream.concat(DoubleStream.of(startX), DoubleStream.of(startY)).toArray();

        rk.integrate(ode, t0, y0, t, y0);
    }

}