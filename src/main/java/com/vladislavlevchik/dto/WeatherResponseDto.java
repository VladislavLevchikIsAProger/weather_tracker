package com.vladislavlevchik.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherResponseDto {

    private String city;

    private String country;

    private int temp;

    private int feelsLike;

    private String description;

    private int humidity;

    private int pressure;

    private int speed;

}
