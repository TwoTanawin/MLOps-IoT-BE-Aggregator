package com.hydroneo.aggregateService.repository;

import com.hydroneo.aggregateService.models.AggregationSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface AggregationSensorRepository extends JpaRepository<AggregationSensor, Long> {
    List<AggregationSensor> findBySerialNumber(String serialNumber);
    Page<AggregationSensor> findBySerialNumberOrderByTimestampDesc(String serialNumber, Pageable pageable);
    Optional<AggregationSensor> findTopBySerialNumberOrderByTimestampDesc(String serialNumber);
}
