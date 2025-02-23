package com.kingcent.plant.constroller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.WeatherEntity;
import com.kingcent.plant.entity.WeatherForecastEntity;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2025/2/23 13:27
 */
@RequestMapping("/weather")
@Controller
public class WeatherController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("https://restapi.amap.com/v3/weather/weatherInfo")
    private String url;

    @GetMapping("/today")
    @ResponseBody
    public Result<WeatherEntity> today(){
        JSONObject today = restTemplate.getForObject(url + "?key=3e1655a562feacada3d9c70453c0ec49&city=440100", JSONObject.class);
        if(today == null || !"1".equals(today.getString("status"))){
            return Result.fail("获取失败");
        }
        JSONObject forecast = restTemplate.getForObject(url + "?key=3e1655a562feacada3d9c70453c0ec49&city=440100&extensions=all", JSONObject.class);
        if(forecast == null || !"1".equals(forecast.getString("status"))){
            return Result.fail("获取失败");
        }
        try{
            List<WeatherForecastEntity> forecastList = new ArrayList<>();
            JSONObject forecasts = (JSONObject) forecast.getJSONArray("forecasts").get(0);
            System.out.println(forecasts);
            JSONArray cast =  forecasts.getJSONArray("casts");
            for (Object o : cast) {
                JSONObject c = (JSONObject) o;
                WeatherForecastEntity weatherForecastEntity = new WeatherForecastEntity();
                weatherForecastEntity.setNightWeather(c.getString("nightweather"));
                weatherForecastEntity.setDate(c.getString("date"));
                weatherForecastEntity.setWeek(c.getString("week"));
                weatherForecastEntity.setTemperature(c.getString("daytemp"));
                weatherForecastEntity.setNightTemperature(c.getString("nighttemp"));
                weatherForecastEntity.setWeather(c.getString("dayweather"));
                weatherForecastEntity.setNightWeather(c.getString("nightweather"));
                weatherForecastEntity.setWindDirection("daywind");
                weatherForecastEntity.setWindPower("daypower");
                weatherForecastEntity.setNightWindPower("nightpower");
                forecastList.add(weatherForecastEntity);
            }
            WeatherEntity weatherEntity = new WeatherEntity();
            JSONArray lives = today.getJSONArray("lives");
            JSONObject todayWeather = (JSONObject) lives.get(0);
            weatherEntity.setWeather(todayWeather.getString("weather"));
            weatherEntity.setHumidity(todayWeather.getString("humidity"));
            weatherEntity.setWindPower(todayWeather.getString("windpower"));
            weatherEntity.setWindDirection(todayWeather.getString("winddirection"));
            weatherEntity.setUpdateTime(todayWeather.getString("reporttime"));
            weatherEntity.setTemperature(todayWeather.getString("temperature"));
            weatherEntity.setProvince(todayWeather.getString("province"));
            weatherEntity.setCity(todayWeather.getString("city"));
            weatherEntity.setForecast(forecastList);
            return Result.success(weatherEntity);
        }catch (Exception e){
            e.printStackTrace(System.out);
            return Result.fail("解析失败");
        }


    }
}
