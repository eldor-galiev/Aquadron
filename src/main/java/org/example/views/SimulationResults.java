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
     * Угол рысканья дрона на каждом шаге симуляции.
     */
    private double angle;

    /**
     * Скорость дрона на каждом шаге симуляции.
     */
    private double speed;

    /**
     * Конечное время симуляции.
     */
    private double time;

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
        angle = states.stream()
                .map(array ->  array[2]).toList().stream()
                .mapToDouble(Double::doubleValue).toArray()[states.size() - 1];
        speed = states.stream()
                .map(array ->  array[3]).toList().stream()
                .mapToDouble(Double::doubleValue).toArray()[states.size() - 1];
        time = DoubleStream.iterate(0, t -> t <= stopTime, t -> t + step)
                .toArray()[states.size() - 1];
        time = (double) Math.round(time * 1000) / 1000;
    }
}
