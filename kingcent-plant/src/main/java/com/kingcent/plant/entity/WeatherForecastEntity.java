package com.kingcent.plant.entity;

import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2025/2/23 14:24
 */
@Data
public class WeatherForecastEntity {
    private String date;
    private String temperature;
    private String weather;
    private String windDirection;
    private String windPower;
    private String nightWindPower;
    private String nightWeather;
    private String nightTemperature;
    private String week;
}
