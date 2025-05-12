package com.hydroneo.aggregateService.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "aggregate_weather")
public class AggregationWeather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private int Cloud;

    private float dew_point;

    private int Humidity;

    private int Pressure;

    private float Temp;

    private float Uvi;

    private int Wind_deg;

    private float Wind_gust;

    private float Wind_speed;

    private String  timestamp;
}
