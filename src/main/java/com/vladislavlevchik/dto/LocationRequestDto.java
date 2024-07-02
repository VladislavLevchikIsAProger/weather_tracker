package com.vladislavlevchik.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationRequestDto {

    private String name;

    private double lat;

    private double lon;

}
