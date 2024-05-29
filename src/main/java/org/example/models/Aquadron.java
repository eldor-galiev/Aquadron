package org.example.models;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * Класс-модель, представляющий всю логику по симулированию движения дрона
 */
@Setter
@Getter
public class Aquadron {
    private RealMatrix x;
    private RealMatrix y;
    private double[] targetPoint;
    private double targetSpeed;
    private RealMatrix R;
    private RealMatrix Fd;
    private final RealMatrix Fv = new Array2DRowRealMatrix(new double[] {0, 0, 0});
    private final RealMatrix Bu = new Array2DRowRealMatrix(new double[][] {{1.0, 0.0}, {0.0, 0.0}, {0.0, 1.0}});
    private final RealMatrix M;
    private final double weight;
    private final double J;
    private final double cx1;
    private final double cx2;
    private final double mx3;
    private double stopTime;

    /**
     * Конструктор класса Aquadron.
     * Инициализирует объект с заданными параметрами.
     *
     * @param coordsY Начальные координаты и угол рысканья .
     * @param coordsX Начальные линейные и угловая скорости.
     * @param weight  Вес дрона.
     * @param J       Момент инерции.
     * @param cx1     Коэффициент сопротивления движению.
     * @param cx2     Коэффициент сопротивления движению.
     * @param mx3     Коэффициент сопротивления движению.
     */
    public Aquadron(double[] coordsY, double[] coordsX, double weight, double J, double cx1, double cx2, double mx3) {
        this.weight = weight;
        this.J = J;
        this.cx1 = cx1;
        this.cx2 = cx2;
        this.mx3 = mx3;

        y = new Array2DRowRealMatrix(coordsY);
        x = new Array2DRowRealMatrix(coordsX);

        R = recalculateMatrixR(coordsY[2]);
        Fd = recalculateMatrixFd(coordsX[0], coordsX[1], coordsX[2]);

        M = MatrixUtils.inverse(new Array2DRowRealMatrix(new double[][] {{weight, 0.0, 0.0}, {0.0, weight, 0.0}, {0.0, 0.0, J}}));
    }

    /**
     * Пересчитывает матрицу R на основе угла y3.
     *
     * @param y3 Угол рысканья.
     * @return Обновленная матрица R.
     */
    public RealMatrix recalculateMatrixR(double y3) {
        R = new Array2DRowRealMatrix(new double[][] {
                {new Cos().value(y3), new Sin().value(y3), 0.0},
                {-1 * new Sin().value(y3), new Cos().value(y3), 0.0},
                {0.0, 0.0, 1.0}
        });

        return R;
    }

    /**
     * Пересчитывает матрицу Fd на основе скоростей.
     *
     * @param x1 Линейная скорость по оси X1.
     * @param x2 Линейная скорость по оси X2.
     * @param x3 Угловая скорость.
     * @return Обновленная матрица Fd.
     */
    public RealMatrix recalculateMatrixFd(double x1, double x2, double x3) {
        Fd = new Array2DRowRealMatrix(new double[][] {
                {-weight * x2 * x3 - cx1 * x1},
                {-weight * x1 * x3 - cx2 * x2},
                {-mx3 * x3}
        });

        return Fd;
    }

    /**
     * Запускает симуляцию движения дрона.
     *
     * @param t    Максимальное время симуляции.
     * @param step Шаг интегрирования.
     * @return Список состояний дрона на каждом шаге.
     */
    public List<double[]> simulate(double t, double step) {
        FirstOrderIntegrator rk = new ClassicalRungeKuttaIntegrator(step);
        FirstOrderDifferentialEquations ode = new ODEFunction(this);

        setStopCondition(rk);

        List<double[]> states = new ArrayList<>();
        getStates(rk, states);

        double[] startY = this.y.transpose().getRow(0);
        double[] startX = this.x.transpose().getRow(0);

        double[] y0 = DoubleStream.concat(DoubleStream.of(startY), DoubleStream.of(startX)).toArray();

        rk.integrate(ode, 0.0, y0, t, y0);

        double[] lastState = states.get(states.size() - 1);

        y.setColumn(0, new double[] {lastState[0], lastState[1], lastState[2]});
        x.setColumn(0, new double[] {lastState[3], lastState[4], lastState[5]});

        return states;
    }

    /**
     * Добавляет обработчик шагов интегратора для сохранения состояний.
     *
     * @param integrator Интегратор для добавления обработчика.
     * @param stateList  Список для сохранения состояний.
     */
    private void getStates(FirstOrderIntegrator integrator, List<double[]> stateList) {
        integrator.addStepHandler(new StepHandler() {
            @Override
            public void init(double t0, double[] y0, double t) {}

            @Override
            public void handleStep(StepInterpolator interpolator, boolean isLast) {
                double[] currentState = interpolator.getInterpolatedState();
                stateList.add(currentState.clone());
            }
        });
    }

    /**
     * Устанавливает условие остановки для интегратора.
     *
     * @param integrator Интегратор для добавления условия остановки.
     */
    private void setStopCondition(FirstOrderIntegrator integrator) {
        EventHandler stopCondition = new EventHandler() {
            @Override
            public void init(double t0, double[] y0, double t) {}

            @Override
            public double g(double t, double[] y) {
                double sumSq = 0;
                for (int i = 0; i < 2; i++) {
                    sumSq += (y[i] - getTargetPoint()[i]) * (y[i] - getTargetPoint()[i]);
                }
                return sumSq - 1e-2;
            }

            @Override
            public Action eventOccurred(double t, double[] y, boolean increasing) {
                stopTime = t;
                return Action.STOP;
            }

            @Override
            public void resetState(double t, double[] y) {}
        };

        integrator.addEventHandler(stopCondition, 1, 1e-8, 10000);
    }
}
