package com.hydroneo.aggregateService.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.Id;

@Data
@Entity
@Table(name = "aggregate_sensor")
public class AggregationSensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float do_value;

    private Float temp_value;

    private Float salinity_value;

    private Float ph_value;

    private String serialNumber;

    private String timestamp;
}
