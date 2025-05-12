package com.hydroneo.aggregateService.models;

import jakarta.persistence.*;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.LocalDateTime;
import java.util.List;

@Data
@DynamoDbBean
public class Weather {
    private String id;

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
