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
     * Начальные координаты Y1.
     */
    private double coordX;

    /**
     * Начальные координаты Y2.
     */
    private double coordY;

    /**
     * Вес дрона.
     */
    private double weight;

    /**
     * Момент инерции дрона.
     */
    private double J;
}
