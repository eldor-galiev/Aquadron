package org.example.views;

import lombok.Getter;

import java.util.List;
import java.util.stream.DoubleStream;

/**
 * Класс, представляющий результаты симуляции дрона.
 */
@Getter
public class SimulationResults {
    /**
     * Координаты дрона на каждом шаге симуляции.
     */
    private double[][] coordinates;

    /**
     * Скорость дрона на каждом шаге симуляции.
     */
    private double[] speed;

    /**
     * Угол рысканья дрона на каждом шаге симуляции.
     */
    private double[] angle;

    /**
     * Время на каждом шаге симуляции.
     */
    private double[] time;

    /**
     * Конструктор класса SimulationResults.
     *
     * @param states   Список состояний дрона на каждом шаге симуляции.
     * @param stopTime Время остановки симуляции.
     * @param step Шаг интегрирования
     */
    public SimulationResults(List<double[]> states, double stopTime, double step) {
        coordinates = states.stream()
                .map(arr -> new double[]{arr[0], arr[1]})
                .toArray(double[][]::new);
        speed = states.stream()
                .map(array ->  array[2]).toList().stream()
                .mapToDouble(Double::doubleValue).toArray();
        angle = states.stream()
                .map(array ->  array[3]).toList().stream()
                .mapToDouble(Double::doubleValue).toArray();
        time = DoubleStream.iterate(0, t -> t <= stopTime, t -> t + step)
                .toArray();
    }
}
