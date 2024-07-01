package com.vladislavlevchik.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherApiResponseDto {

    private String name;

    private Double lat;

    private Double lon;

    private String country;

    private String state;
}
