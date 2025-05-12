package com.hydroneo.aggregateService.components;

import com.hydroneo.aggregateService.models.Sensor;
import com.hydroneo.aggregateService.models.AggregationSensor;
import com.hydroneo.aggregateService.services.SensorService;
import com.hydroneo.aggregateService.repository.AggregationSensorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SparkAggregateSensorJob {
    private static final Logger logger = LoggerFactory.getLogger(SparkAggregateSensorJob.class);

    private final SensorService sensorService;
    private final AggregationSensorRepository aggregationSensorRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public SparkAggregateSensorJob(
            SensorService sensorService,
            AggregationSensorRepository aggregationSensorRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.sensorService = sensorService;
        this.aggregationSensorRepository = aggregationSensorRepository;
        this.messagingTemplate = messagingTemplate;
        logger.info("AggregationSensorJob initialized");
    }

    @Scheduled(fixedRate = 10000)
    public void runAggregationJob() {
        logger.info("Running sensor aggregation job at: {}", LocalDateTime.now());

        try {
            // Fetch data from DynamoDB
            List<Sensor> sensors = sensorService.listAllSensors();
            logger.info("Fetched {} sensor records from DynamoDB", sensors.size());

            if (sensors.isEmpty()) {
                logger.warn("No sensor data found, skipping aggregation.");
                return;
            }

            // Log sample data for debugging
            if (sensors.size() > 0) {
                logger.info("Sample sensor data: {}", sensors.get(0));
            }

            // Group by serialNumber and calculate averages using Java Streams
            Map<String, List<Sensor>> groupedSensors = sensors.stream()
                    .collect(Collectors.groupingBy(Sensor::getSerialNumber));
            
            List<AggregationSensor> aggregationSensors = new ArrayList<>();
            
            for (Map.Entry<String, List<Sensor>> entry : groupedSensors.entrySet()) {
                String serialNumber = entry.getKey();
                List<Sensor> sensorGroup = entry.getValue();
                
                // Calculate averages
                float avgDoValue = (float) sensorGroup.stream()
                        .mapToDouble(Sensor::getDo_value)
                        .average()
                        .orElse(0.0);
                
                float avgTempValue = (float) sensorGroup.stream()
                        .mapToDouble(Sensor::getTemp_value)
                        .average()
                        .orElse(0.0);
                
                float avgSalinityValue = (float) sensorGroup.stream()
                        .mapToDouble(Sensor::getSalinity_value)
                        .average()
                        .orElse(0.0);
                
                float avgPhValue = (float) sensorGroup.stream()
                        .mapToDouble(Sensor::getPh_value)
                        .average()
                        .orElse(0.0);
                
                // Create aggregation object
                AggregationSensor aggregationSensor = new AggregationSensor();
                aggregationSensor.setSerialNumber(serialNumber);
                aggregationSensor.setDo_value(avgDoValue);
                aggregationSensor.setTemp_value(avgTempValue);
                aggregationSensor.setSalinity_value(avgSalinityValue);
                aggregationSensor.setPh_value(avgPhValue);
                aggregationSensor.setTimestamp(LocalDateTime.now().toString());
                
                aggregationSensors.add(aggregationSensor);
            }

            // Log the aggregated results
            logger.info("Aggregated {} sensor groups", aggregationSensors.size());
            if (!aggregationSensors.isEmpty()) {
                logger.info("Sample aggregated data: {}", aggregationSensors.get(0));
            }

            // Save to Database
            aggregationSensorRepository.saveAll(aggregationSensors);
            logger.info("Saved {} aggregated sensor records to PostgreSQL.", aggregationSensors.size());

            // Send to WebSocket clients
            messagingTemplate.convertAndSend("/topic/aggregation", aggregationSensors);
            logger.info("Sent aggregated sensor data to WebSocket clients.");

        } catch (Exception e) {
            logger.error("Error in aggregation job execution", e);
            e.printStackTrace();
        }
    }
}