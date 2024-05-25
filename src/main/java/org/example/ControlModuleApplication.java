package org.example;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.example.domain.Aquadron;
import org.example.domain.ODEFunction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.stream.DoubleStream;

@SpringBootApplication
public class ControlModuleApplication {

    public static void main(String[] args) {
        double[] startX = new double[] {0.0, 0.0, 0};
        double[] startY = new double[] {0.0, 0.0, 0};

        Aquadron aquadron = new Aquadron(startX, startY, 50, 15, 0.1, 0.7, 0.2);

        aquadron.setXDesired(new Array2DRowRealMatrix(new double[] {10, 10, 0}));
        aquadron.setYDesired(new Array2DRowRealMatrix(new double[] {10, 10, 0}));

        FirstOrderIntegrator rk = new ClassicalRungeKuttaIntegrator(0.1);
        FirstOrderDifferentialEquations ode = new ODEFunction(aquadron);
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
//        System.out.println(y[0] + " " + y[1] + " " + y[2] + " " + y[3] + " " + y[4] + " " + y[5]);
        rk.integrate(ode, 0.0, y0, 10.0, y0);
        SpringApplication.run(ControlModuleApplication.class, args);
    }
}