package org.example.domain;

import org.apache.commons.math3.analysis.function.Atan;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

public class ODEFunction implements FirstOrderDifferentialEquations {
    private final Aquadron aquadron;

    public ODEFunction(Aquadron aquadron) {
        this.aquadron = aquadron;
    }

    @Override
    public int getDimension() {
        return 6;
    }

    @Override
    public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
        aquadron.recalculateMatrices();

        RealMatrix A1RA3 = new Array2DRowRealMatrix(new double[][] {{0, 0, 1}, {1, 0, 0}});

        RealMatrix U = (inverseMatrix(A1RA3.multiply(aquadron.getM()).multiply(aquadron.getBu()))).scalarMultiply(-1)
                .multiply(A1RA3.multiply(aquadron.getM()).multiply(aquadron.getFd().add(aquadron.getFv()))
                        .add(calculateErrorMatrix()));

        RealMatrix newY = aquadron.getR().multiply(aquadron.getX());
        RealMatrix newX = aquadron.getM().multiply(aquadron.getBu().multiply(U).add(aquadron.getFd()).add(aquadron.getFv()));

        yDot[0] = newY.getEntry(0,0);
        yDot[1] = newY.getEntry(1,0);
        yDot[2] = newY.getEntry(2,0);

        yDot[3] = newX.getEntry(0,0);
        yDot[4] = newX.getEntry(1,0);
        yDot[5] = newX.getEntry(2,0);

        aquadron.setY(aquadron.getR().multiply(aquadron.getX()));
        aquadron.setX(aquadron.getM().multiply(aquadron.getBu().multiply(U).add(aquadron.getFd()).add(aquadron.getFv())));

        yDot[0] = aquadron.getX().getEntry(0,0);
        yDot[1] = aquadron.getX().getEntry(1,0);
        yDot[2] = aquadron.getX().getEntry(2,0);

        yDot[3] = aquadron.getY().getEntry(0,0);
        yDot[4] = aquadron.getY().getEntry(1,0);
        yDot[5] = aquadron.getY().getEntry(2,0);

    }

    private RealMatrix inverseMatrix(RealMatrix matrix) {
        LUDecomposition lu = new LUDecomposition(matrix);
        matrix = lu.getSolver().getInverse();

        return matrix;
    }

    private RealMatrix calculateErrorMatrix() {
        double T1 = 25;
        double T2 = 10;
        double T3 = 5;

        calculateDesiredY();

        double Etr = aquadron.getY().getEntry(2, 0) - aquadron.getYDesired().getEntry(2, 0);
        double EtrDot = aquadron.getX().getEntry(2, 0);
        double Esp = aquadron.getX().getEntry(0, 0) - aquadron.getXDesired().getEntry(2, 0);

        double [] errors = new double[2];
        errors[0] = T2 * EtrDot + T1 * Etr;
        errors[1] = T3 * Esp;

        return new Array2DRowRealMatrix(errors);
    }


    private void calculateDesiredY() {
        double y10 = aquadron.getYDesired().getEntry(0, 0);
        double y1 = aquadron.getY().getEntry(0, 0);
        double y20 = aquadron.getYDesired().getEntry(1, 0);
        double y2 = aquadron.getY().getEntry(1, 0);

        double Txi = 10.0;
        double beta0 = 1.0;
        double xi0 = 0.0;

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

        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(1) {
        };
        integrator.integrate(ode, 0.0, xiInitial, 1.0, xiFinal);

        aquadron.getYDesired().setEntry(2, 0, -1 * new Atan().value((y20 - y2) / (y10 - y1)) + Math.max(-Math.PI / 2, Math.min(xiFinal[0], Math.PI / 2)));
    }
}
