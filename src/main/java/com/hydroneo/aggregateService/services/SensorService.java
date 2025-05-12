package com.hydroneo.aggregateService.services;

import com.hydroneo.aggregateService.models.Sensor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SensorService {

    private final DynamoDbTable<Sensor> sensorTable;

    public SensorService(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.sensorTable = enhancedClient.table("SensorData", TableSchema.fromBean(Sensor.class));
    }

    public List<Sensor> listAllSensors() {
        return StreamSupport.stream(sensorTable.scan().items().spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Sensor> findSensorBySerialNumber(String serialNumber) {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":serialNumber", AttributeValue.builder().s(serialNumber).build());

        Expression expression = Expression.builder()
                .expression("serialNumber = :serialNumber")
                .expressionValues(expressionValues)
                .build();

        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .build();

        return StreamSupport.stream(sensorTable.scan(request).items().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Page<Sensor> findSensorBySerialNumberPaginated(String serialNumber, Map<String, AttributeValue> lastEvaluatedKey, int pageSize) {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":serialNumber", AttributeValue.builder().s(serialNumber).build());

        Expression expression = Expression.builder()
                .expression("serialNumber = :serialNumber")
                .expressionValues(expressionValues)
                .build();

        ScanEnhancedRequest.Builder requestBuilder = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .limit(pageSize); // Set page size

        if (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty()) {
            requestBuilder.exclusiveStartKey(lastEvaluatedKey);
        }

        return sensorTable.scan(requestBuilder.build()).iterator().next();
    }
}
