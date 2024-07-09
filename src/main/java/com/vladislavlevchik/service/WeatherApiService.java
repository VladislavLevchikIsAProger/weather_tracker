package com.vladislavlevchik.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.vladislavlevchik.dto.WeatherResponseDto;
import com.vladislavlevchik.dto.api.WeatherApiResponseDirectDto;
import com.vladislavlevchik.dto.api.WeatherApiResponseWeatherDto;
import com.vladislavlevchik.entity.Location;
import com.vladislavlevchik.exception.api.EmptyBodyException;
import com.vladislavlevchik.exception.api.GeoLocationException;
import com.vladislavlevchik.exception.api.InvalidStatusCodeException;
import com.vladislavlevchik.exception.api.WeatherInfoException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.vladislavlevchik.utils.ConfigUtil.getApiKey;

public class WeatherApiService {
    private static final String BASE_API_URL = "https://api.openweathermap.org";
    private static final String WEATHER_PATH = "/data/2.5/weather";
    private static final String DIRECT_PATH = "/geo/1.0/direct";


    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public WeatherApiService(HttpClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public WeatherApiService() {
        client = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }

    public List<WeatherApiResponseDirectDto> getGeoLocationInfo(String city) throws IOException {
        try {
            URI uri = buildURI(city);

            HttpRequest req = buildRequest(uri);

            HttpResponse<String> res = getResponseIfValid(client, req);

            return objectMapper.readValue(
                    res.body(),
                    new TypeReference<List<WeatherApiResponseDirectDto>>() {
                    });

        } catch (InterruptedException | MismatchedInputException e) {
            throw new GeoLocationException();
        }
    }

    public WeatherResponseDto getWeatherInfo(Location location) throws IOException {
        try {
            URI uri = buildURI(location);

            HttpRequest req = buildRequest(uri);

            HttpResponse<String> res = getResponseIfValid(client, req);

            WeatherApiResponseWeatherDto weatherApiResponseWeatherDto = objectMapper.readValue(res.body(), WeatherApiResponseWeatherDto.class);

            return buildWeatherResponseDto(weatherApiResponseWeatherDto, location);
        } catch (InterruptedException | MismatchedInputException e) {
            throw new WeatherInfoException();
        }

    }

    private HttpRequest buildRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .build();
    }

    private URI buildURI(String city) {
        return URI.create(String.format(BASE_API_URL + DIRECT_PATH + "?q=%s&limit=5&appid=%s", city, getApiKey()));
    }

    private URI buildURI(Location location) {
        return URI.create(String.format(BASE_API_URL + WEATHER_PATH + "?lat=%s&lon=%s&appid=%s&units=metric", location.getLat(), location.getLon(), getApiKey()));
    }

    private HttpResponse<String> getResponseIfValid(HttpClient client, HttpRequest req) throws IOException, InterruptedException {
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

        int statusCode = res.statusCode();
        if ((statusCode >= 500 && statusCode < 600) || (statusCode >= 400 && statusCode < 500)) {
            throw new InvalidStatusCodeException();
        }

        String responseBody = res.body();
        if (responseBody == null || responseBody.isEmpty()) {
            throw new EmptyBodyException();
        }

        return res;
    }

    private WeatherResponseDto buildWeatherResponseDto(WeatherApiResponseWeatherDto weatherApiResponseWeatherDto, Location location) {
        return WeatherResponseDto.builder()
                .city(location.getName())
                .country(weatherApiResponseWeatherDto.getSys().getCountry())
                .temp((int) Math.round(weatherApiResponseWeatherDto.getMain().getTemp()))
                .feelsLike((int) Math.round(weatherApiResponseWeatherDto.getMain().getFeelsLike()))
                .description(weatherApiResponseWeatherDto.getWeather()[0].getDescription())
                .humidity(weatherApiResponseWeatherDto.getMain().getHumidity())
                .pressure(weatherApiResponseWeatherDto.getMain().getPressure())
                .speed(weatherApiResponseWeatherDto.getWind().getSpeed())
                .lon(location.getLon())
                .lat(location.getLat())
                .build();
    }
}
