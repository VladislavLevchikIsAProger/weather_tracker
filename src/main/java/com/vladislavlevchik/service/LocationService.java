package com.vladislavlevchik.service;

import com.vladislavlevchik.dto.LocationRequestDto;
import com.vladislavlevchik.dto.WeatherResponseDto;
import com.vladislavlevchik.entity.Location;
import com.vladislavlevchik.entity.User;
import com.vladislavlevchik.repository.LocationRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationService {

    private final WeatherApiService weatherApiService = new WeatherApiService();
    private final LocationRepository locationRepository = new LocationRepository();

    public void addLocationToUser(User user, LocationRequestDto locationRequestDto) {
        Location location = Location.builder()
                .name(locationRequestDto.getName())
                .lat(locationRequestDto.getLat())
                .lon(locationRequestDto.getLon())
                .user(user)
                .build();

        locationRepository.save(location);
    }

    public List<WeatherResponseDto> getWeathers(List<Location> locations) throws IOException {
        List<WeatherResponseDto> list = new ArrayList<>();

        for(Location location: locations){
            list.add(weatherApiService.getWeatherInfo(location));
        }

        return list;
    }

}
