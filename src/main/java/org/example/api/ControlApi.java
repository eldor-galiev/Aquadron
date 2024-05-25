package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.domain.Aquadron;
import org.example.service.ControlService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/system")
public class ControlApi {

    private final ControlService controlService;

    @GetMapping("/simulate")
    public HttpStatus simulate(double[] startX, double[] startY, double t0, double t, double step) {
        controlService.simulate(startX, startY,  t0,  t,  step);
        return HttpStatus.OK;
    }

    @PostMapping("/updateParameters")
    public void updateParameters(@RequestBody Aquadron parameters) {
        // Логика обновления параметров управления
    }

    // Другие методы API для взаимодействия с модулем
}