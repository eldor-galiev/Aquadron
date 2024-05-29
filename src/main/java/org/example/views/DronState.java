package org.example.views;

import lombok.Getter;
import org.example.models.Aquadron;

/**
 * Класс, представляющий текущее состояние дрона.
 */
@Getter
public class DronState {
    /**
     * Координаты дрона.
     */
    private final double[] y;

    /**
     * Скорости дрона.
     */
    private final double[] x;

    /**
     * Вес дрона.
     */
    private final double weight;

    /**
     * Момент инерции дрона.
     */
    private final double J;

    /**
     * Коэффициент сопротивления движению.
     */
    private final double cx1;

    /**
     * Коэффициент сопротивления движению.
     */
    private final double cx2;

    /**
     * Коэффициент сопротивления движению.
     */
    private final double mx3;

    /**
     * Конструктор класса DronState.
     *
     * @param aquadron Объект класса {@link Aquadron} для инициализации состояния.
     */
    public DronState(Aquadron aquadron) {
        y = aquadron.getY().getColumn(0);
        x = aquadron.getX().getColumn(0);
        weight = aquadron.getWeight();
        J = aquadron.getJ();
        cx1 = aquadron.getCx1();
        cx2 = aquadron.getCx2();
        mx3 = aquadron.getMx3();
    }
}
