package org.example.views;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий данные необходимые для инициализации дрона
 */
@AllArgsConstructor
@Getter
@Setter
public class DronInitializationRequest {
    /**
     * Начальные координаты Y.
     */
    private double[] coordsY;

    /**
     * Начальные координаты X.
     */
    private double[] coordsX;

    /**
     * Вес дрона.
     */
    private double weight;

    /**
     * Момент инерции дрона.
     */
    private double J;

    /**
     * Коэффициент сопротивления по оси X1.
     */
    private double cx1;

    /**
     * Коэффициент сопротивления по оси X2.
     */
    private double cx2;

    /**
     * Коэффициент сопротивления по оси X3.
     */
    private double mx3;
}
