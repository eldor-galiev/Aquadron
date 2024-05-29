package org.example.models;

import lombok.AllArgsConstructor;
import org.apache.commons.math3.analysis.function.Atan;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

/**
 * Класс, представляющий обыкновенное дифференциального уравнения (ODE) для системы {@link Aquadron}.
 * <p>
 * Эта функция вычисляет производные переменных состояния относительно времени.
 * Она реализует интерфейс {@link FirstOrderDifferentialEquations}.
 */
@AllArgsConstructor
public class ODEFunction implements FirstOrderDifferentialEquations {
    private final Aquadron aquadron;

    /**
     * Получает размерность системы ODE.
     * @return Размерность системы ODE.
     */
    @Override
    public int getDimension() {
        return 6;
    }

    /**
     * Вычисляет производные переменных состояния относительно времени.
     *
     * @param t     Текущее значение независимой переменной (время).
     * @param y     Текущие значения переменных состояния.
     * @param yDot  Массив, куда будут сохранены вычисленные производные.
     * @throws MaxCountExceededException  если количество оценок превышает максимум.
     * @throws DimensionMismatchException если размерности массивов не соответствуют размерности системы ODE.
     */
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

    /**
     * Вычисляет матрицу ошибок, используемую в алгоритме управления.
     *
     * @param Y Текущие значения переменных состояния, связанных с позицией.
     * @param X Текущие значения переменных состояния, связанных со скоростью.
     * @return Матрица ошибок.
     */
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

    /**
     * Вычисляет желаемое значение угла.
     *
     * @param Y Текущие значения координат.
     * @return Желаемое значение угла.
     */
    private double calculateDesiredY3(RealMatrix Y) {
        double y10 = aquadron.getTargetPoint()[0];
        double y1 = Y.getEntry(0, 0);
        double y20 = aquadron.getTargetPoint()[1];
        double y2 = Y.getEntry(1, 0);

        return  -1 * new Atan().value((y20 - y2) / (y10 - y1));
    }
}
