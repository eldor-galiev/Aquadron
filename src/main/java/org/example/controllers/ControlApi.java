package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.models.Aquadron;
import org.example.views.DronInitializationRequest;
import org.example.views.DronState;
import org.example.views.SimulationResults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/aquadron")
public class ControlApi {
    private Aquadron aquadron;

    @PostMapping("/create")
    public void create(@RequestBody DronInitializationRequest request) {
        this.aquadron = new Aquadron(
                request.getCoordsY(),
                request.getCoordsX(),
                request.getWeight(),
                request.getJ(),
                request.getCx1(),
                request.getCx2(),
                request.getMx3()
        );
    }

    @GetMapping("/simulate")
    public ResponseEntity<SimulationResults> simulate(@RequestParam double targetY1, @RequestParam double targetY2, @RequestParam double targetSpeed) {
        double t = 100.0;
        double step = 0.01;

        targetSpeed = aquadron.getY().getEntry(0,0) <= targetY1 ? targetSpeed : - targetSpeed;

        aquadron.setTargetPoint(new double[] {targetY1, targetY2});
        aquadron.setTargetSpeed(targetSpeed);

        List<double[]> states = aquadron.simulate(t, step).stream()
                .map(array -> new double[]{array[0], array[1], array[2], array[3]})
                .toList();

        SimulationResults results = new SimulationResults(states, aquadron.getStopTime());

        return ResponseEntity.ok(results);
    }

    @GetMapping("/state")
    public ResponseEntity<DronState> getAquadronState() {
        if (aquadron == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new DronState(aquadron));
    }
}