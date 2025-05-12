package com.hydroneo.aggregateService.controllers;

import com.hydroneo.aggregateService.models.AggregationSensor;
import com.hydroneo.aggregateService.services.AggregateSensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/v1/aggregate_sensor")
public class AggregateSensorController {
    private final AggregateSensorService aggregateSensorService;

    public AggregateSensorController(AggregateSensorService aggregateSensorService){
        this.aggregateSensorService = aggregateSensorService;
    }

    @GetMapping("/getAllSensor")
    public ResponseEntity<List<AggregationSensor>> getAllSensors(){
        List<AggregationSensor> sensors = aggregateSensorService.getAllSensors();
        return ResponseEntity.ok(sensors);
    }

    @GetMapping("/getSensorbySerialNumber/{serialNumber}")
    public ResponseEntity<List<AggregationSensor>> getSensorbySerialNumber(@PathVariable String serialNumber) {
        List<AggregationSensor> sensors = aggregateSensorService.getSensorbySerialNumber(serialNumber);

        if (sensors == null || sensors.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(sensors);
    }

    @GetMapping("/getSensorbySerialNumberPagination/{serialNumber}")
    public ResponseEntity<Page<AggregationSensor>> getSensorbySerialNumberPagination(
            @PathVariable String serialNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AggregationSensor> sensors = aggregateSensorService.getSensorbySerialNumberPagination(serialNumber, pageable);

        if (sensors.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(sensors);
    }

    @GetMapping("/getLatestSensorbySerialNumber/{serialNumber}")
    public ResponseEntity<AggregationSensor> getLatestSensor(
        @PathVariable String serialNumber) {
        return aggregateSensorService.getLatestAggregation(serialNumber)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

}
