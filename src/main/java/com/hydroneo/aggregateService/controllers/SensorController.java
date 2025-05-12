package com.hydroneo.aggregateService.controllers;

import com.hydroneo.aggregateService.models.Sensor;
import com.hydroneo.aggregateService.services.SensorService;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/sensor")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping("/getAllSensors")
    public List<Sensor> getAllSensors() {
        return sensorService.listAllSensors();
    }

    @GetMapping("/findBySerialNumber/{serialNumber}")
    public List<Sensor> getSensorsBySerialNumber(@PathVariable String serialNumber) {
        return sensorService.findSensorBySerialNumber(serialNumber);
    }

    @GetMapping("/findBySerialNumberPaginated/{serialNumber}")
    public Map<String, Object> getSensorsBySerialNumberPaginated(
            @PathVariable String serialNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Map<String, String> lastEvaluatedKeyRaw
    ) {
        int pageSizeValue = (pageSize != null) ? pageSize : 10; // Default page size = 10

        Map<String, AttributeValue> lastEvaluatedKey = null;
        if (lastEvaluatedKeyRaw != null && !lastEvaluatedKeyRaw.isEmpty()) {
            lastEvaluatedKey = lastEvaluatedKeyRaw.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> AttributeValue.builder().s(e.getValue()).build()
                    ));
        }

        var page = sensorService.findSensorBySerialNumberPaginated(serialNumber, lastEvaluatedKey, pageSizeValue);

        Map<String, Object> response = new HashMap<>();
        response.put("items", page.items());
        response.put("lastEvaluatedKey", page.lastEvaluatedKey());

        return response;
    }

}
