package com.vladislavlevchik.service;

import com.vladislavlevchik.entity.Location;
import com.vladislavlevchik.entity.User;
import com.vladislavlevchik.repository.LocationRepository;

public class LocationService {

    private final LocationRepository locationRepository = new LocationRepository();

    public void addLocation(User user, String name, Double lat, Double lon) {
        Location location = Location.builder()
                .name(name)
                .lat(lat)
                .lon(lon)
                .user(user)
                .build();

        locationRepository.save(location);
    }

}
