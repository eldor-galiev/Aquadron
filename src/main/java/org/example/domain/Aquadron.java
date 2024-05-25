package org.example.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

@Setter
@Getter
public class Aquadron {
    private RealMatrix x;
    private RealMatrix y;
    private RealMatrix xDesired;
    private RealMatrix yDesired;
    private RealMatrix R;
    private RealMatrix Fd;
    private RealMatrix Fv;
    private RealMatrix Bu;
    private RealMatrix M;
    private double weight;
    private double J;
    private double cx1;
    private double cx2;
    private double mx3;

    public Aquadron(double[] coordsX, double[] coordsY, double weight, double J, double cx1, double cx2, double mx3) {
        this.weight = weight;
        this.J = J;
        this.cx1 = cx1;
        this.cx2 = cx2;
        this.mx3 = mx3;

        x = new Array2DRowRealMatrix(coordsY);
        y = new Array2DRowRealMatrix(coordsX);

        recalculateMatrices();

        M = new Array2DRowRealMatrix(new double[][] {{weight, 0.0, 0.0}, {0.0, weight, 0.0}, {0.0, 0.0, J}});
        M = inverseMatrix(M);
        Bu = new Array2DRowRealMatrix(new double[][] {{1.0, 0.0}, {0.0, 0.0}, {0.0, 1.0}});
        Fv = new Array2DRowRealMatrix(new double[] {0, 0, 0});
    }

    public void recalculateMatrices() {
        double x1 = x.getEntry(0,0);
        double x2 = x.getEntry(1,0);
        double x3 = x.getEntry(2,0);
        double y3 = y.getEntry(2,0);

        R = new Array2DRowRealMatrix(new double[][] {{new Cos().value(y3), new Sin().value(y3), 0.0}, {-1 * new Sin().value(y3), new Cos().value(y3), 0.0}, {0.0, 0.0, 1.0}});
        Fd = new Array2DRowRealMatrix(new double[][] {{-weight*x2*x3-cx1*x1}, {-weight*x1*x3-cx2*x2}, {-mx3*x3}});
    }

    private RealMatrix inverseMatrix(RealMatrix matrix) {
        LUDecomposition lu = new LUDecomposition(matrix);
        matrix = lu.getSolver().getInverse();

        return matrix;
    }

    @Override
    public String toString() {
        return this.x + "  " + this.y;
    }

}
