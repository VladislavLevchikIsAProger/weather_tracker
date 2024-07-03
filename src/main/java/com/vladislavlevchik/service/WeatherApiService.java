package com.vladislavlevchik.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladislavlevchik.dto.api.WeatherApiResponseDirectDto;
import com.vladislavlevchik.dto.api.WeatherApiResponseWeatherDto;
import com.vladislavlevchik.dto.WeatherResponseDto;
import com.vladislavlevchik.entity.Location;
import com.vladislavlevchik.exception.api.GeoLocationException;
import com.vladislavlevchik.exception.api.WeatherInfoException;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.vladislavlevchik.utils.MapperUtil.convertToDto;

public class WeatherApiService {

    private static final String API_ID = "82a165d34bb59b30c17b7dbc1ea9a409";
    private static final String BASE_API_URL = "https://api.openweathermap.org";
    private static final String WEATHER_PATH = "/data/2.5/weather";
    private static final String DIRECT_PATH = "/geo/1.0/direct";


    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<WeatherApiResponseDirectDto> getGeoLocationInfo(String city) throws ServletException, IOException {
        try {
            URI uri = buildURI(city);

            HttpRequest request = buildRequest(uri);

            HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(
                    res.body(),
                    new TypeReference<List<WeatherApiResponseDirectDto>>() {});

        } catch (InterruptedException e) {
            throw new GeoLocationException();
        }
    }

    public WeatherResponseDto getWeatherInfo(Location location) throws IOException {
        try {
            URI uri = buildURI(location);

            HttpRequest req = buildRequest(uri);

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            WeatherApiResponseWeatherDto weatherApiResponseWeatherDto = objectMapper.readValue(res.body(), WeatherApiResponseWeatherDto.class);

            return convertToDto(weatherApiResponseWeatherDto);
        } catch (InterruptedException e) {
            throw new WeatherInfoException();
        }

    }

    private HttpRequest buildRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .build();
    }

    private URI buildURI(String city) {
        return URI.create(String.format(BASE_API_URL + DIRECT_PATH + "?q=%s&limit=5&appid=%S", city, API_ID));
    }

    private URI buildURI(Location location) {
        return URI.create(String.format(BASE_API_URL + WEATHER_PATH + "?lat=%s&lon=%s&appid=%s", location.getLat(), location.getLon(), API_ID));
    }
}
