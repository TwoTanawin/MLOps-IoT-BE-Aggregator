package com.hydroneo.aggregateService.repository;

import com.hydroneo.aggregateService.models.AggregationWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregationWeatherRepository extends JpaRepository<AggregationWeather, Long> {
}

