package com.hydroneo.aggregateService.models;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
public class Sensor {
    private String id; //UUID

    private Float do_value;

    private Float temp_value;

    private Float salinity_value;

    private Float ph_value;

    private String serialNumber;

    private String timestamp;

}

