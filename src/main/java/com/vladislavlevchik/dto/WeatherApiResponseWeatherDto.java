package com.vladislavlevchik.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponseWeatherDto {

    @JsonProperty("main")
    private WeatherMainDto main;

    @JsonProperty("weather")
    private WeatherDto[] weather;

    @JsonProperty("wind")
    private WindDto wind;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherMainDto {
        private double temp;
        @JsonProperty("feels_like")
        private double feelsLike;
        private int pressure;
        private int humidity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherDto {
        private String description;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WindDto {
        private int speed;
    }
}
