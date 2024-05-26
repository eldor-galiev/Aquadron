package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.domain.Aquadron;
import org.example.domain.AquadronInitializationRequest;
import org.example.domain.SimulationResults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aquadron")
public class ControlApi {
    private Aquadron aquadron;

    @PostMapping("/create")
    public void start(@RequestBody AquadronInitializationRequest request) {
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

        aquadron.setTargetPoint(new double[] {targetY1, targetY2});
        aquadron.setSpeed(targetSpeed);

        List<double[]> states = aquadron.simulate(t, step).stream()
                .map(array -> new double[]{array[0], array[1], array[2], array[3]})
                .toList();
        double[][] coordinates = states.stream()
                .map(arr -> new double[]{arr[0], arr[1]})
                .toArray(double[][]::new);
        double[] speed = states.stream()
                .map(array ->  array[2]).toList().stream().mapToDouble(Double::doubleValue).toArray();
        double[] angle = states.stream()
                .map(array ->  array[3]).toList().stream().mapToDouble(Double::doubleValue).toArray();

        SimulationResults results = new SimulationResults(coordinates, speed, angle, aquadron.getStopTime());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/state")
    public ResponseEntity<Aquadron> getAquadronState() {
        if (aquadron == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(aquadron);
    }
}