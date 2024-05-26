package org.example.models;

import lombok.AllArgsConstructor;
import org.apache.commons.math3.analysis.function.Atan;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

@AllArgsConstructor
public class ODEFunction implements FirstOrderDifferentialEquations {
    private final Aquadron aquadron;

    @Override
    public int getDimension() {
        return 6;
    }

    @Override
    public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
        RealMatrix Y = new Array2DRowRealMatrix(new double[] {y[0], y[1], y[2]});
        RealMatrix X = new Array2DRowRealMatrix(new double[] {y[3], y[4], y[5]});

        RealMatrix R = aquadron.recalculateMatrixR(y[2]);
        RealMatrix Fd = aquadron.recalculateMatrixFd(y[3], y[4], y[5]);

        RealMatrix M = aquadron.getM();
        RealMatrix Bu = aquadron.getBu();
        RealMatrix Fv = aquadron.getFv();

        RealMatrix A1RA3 = new Array2DRowRealMatrix(new double[][] {{0, 0, 1}, {1, 0, 0}});

        RealMatrix U = (MatrixUtils.inverse(A1RA3.multiply(M).multiply(Bu))).scalarMultiply(-1)
                .multiply(A1RA3.multiply(M).multiply(Fd.add(Fv))
                        .add(calculateErrorMatrix(Y, X)));

        RealMatrix newY = R.multiply(X);
        RealMatrix newX = M.multiply(Bu.multiply(U).add(Fd).add(Fv));

        yDot[0] = newY.getEntry(0,0);
        yDot[1] = newY.getEntry(1,0);
        yDot[2] = newY.getEntry(2,0);

        yDot[3] = newX.getEntry(0,0);
        yDot[4] = newX.getEntry(1,0);
        yDot[5] = newX.getEntry(2,0);

    }

    private RealMatrix calculateErrorMatrix(RealMatrix Y, RealMatrix X) {
        double T1 = 25;
        double T2 = 10;
        double T3 = 5;

        double desiredY3 = calculateDesiredY3(Y);

        double Etr = Y.getEntry(2, 0) - desiredY3;
        double EtrDot = X.getEntry(2, 0);

        double Esp = X.getEntry(0, 0) - aquadron.getTargetSpeed();

        double [] errors = new double[2];
        errors[0] = T2 * EtrDot + T1 * Etr;
        errors[1] = T3 * Esp;

        return new Array2DRowRealMatrix(errors);
    }


    private double calculateDesiredY3(RealMatrix Y) {
        double y10 = aquadron.getTargetPoint()[0];
        double y1 = Y.getEntry(0, 0);
        double y20 = aquadron.getTargetPoint()[1];
        double y2 = Y.getEntry(1, 0);

        return  -1 * new Atan().value((y20 - y2) / (y10 - y1));
    }

    private double calculateDeltaY3() {
        double Txi = 10.0;
        double beta0 = 1.0;
        double xi0 = 1.0;
        FirstOrderDifferentialEquations ode = new FirstOrderDifferentialEquations() {
            @Override
            public int getDimension() {
                return 1;
            }

            @Override
            public void computeDerivatives(double t, double[] xi, double[] xiDot) {
                double beta = Txi + beta0;
                xiDot[0] = -(Txi + beta) * xi[0] + beta;
            }
        };

        double[] xiInitial = {xi0};
        double[] xiFinal = new double[1];

        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(0.001);
        integrator.integrate(ode, 0.0, xiInitial, 10.0, xiFinal);

        return Math.max(-Math.PI / 2, Math.min(xiFinal[0], Math.PI / 2));
    }
}
