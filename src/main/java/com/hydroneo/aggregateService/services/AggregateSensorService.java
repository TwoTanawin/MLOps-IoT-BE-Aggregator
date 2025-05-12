package com.hydroneo.aggregateService.services;

import com.hydroneo.aggregateService.models.AggregationSensor;
import com.hydroneo.aggregateService.repository.AggregationSensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class AggregateSensorService {
    private final AggregationSensorRepository aggregationSensorRepository;

    public AggregateSensorService(AggregationSensorRepository aggregationSensorRepository){
        this.aggregationSensorRepository = aggregationSensorRepository;
    }

    public List<AggregationSensor> getAllSensors(){
        return aggregationSensorRepository.findAll();
    }

    public List<AggregationSensor> getSensorbySerialNumber(String serialNumber){
        return aggregationSensorRepository.findBySerialNumber(serialNumber);
    }

    public Page<AggregationSensor> getSensorbySerialNumberPagination(String serialNumber, Pageable pageable) {
        return aggregationSensorRepository.findBySerialNumberOrderByTimestampDesc(serialNumber, pageable);
    }

    public Optional<AggregationSensor> getLatestAggregation(String serialNumber) {
        return aggregationSensorRepository.findTopBySerialNumberOrderByTimestampDesc(serialNumber);
    }
}
