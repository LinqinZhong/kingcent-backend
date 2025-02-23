package com.kingcent.plant.entity;

import lombok.Data;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2025/2/23 14:22
 */
@Data
public class WeatherEntity {
    private String updateTime;
    private String weather;
    private String temperature;
    private String humidity;
    private String windDirection;
    private String windPower;
    private String province;
    private String city;
    private List<WeatherForecastEntity> forecast;
}
