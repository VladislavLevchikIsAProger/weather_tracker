package com.vladislavlevchik.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladislavlevchik.dto.WeatherResponseDto;
import com.vladislavlevchik.dto.api.WeatherApiResponseDirectDto;
import com.vladislavlevchik.dto.api.WeatherApiResponseWeatherDto;
import com.vladislavlevchik.entity.Location;
import com.vladislavlevchik.exception.api.GeoLocationException;
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


    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<WeatherApiResponseDirectDto> getGeoLocationInfo(String city) throws IOException {
        try {
            URI uri = buildURI(city);

            HttpRequest request = buildRequest(uri);

            HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(
                    res.body(),
                    new TypeReference<List<WeatherApiResponseDirectDto>>() {
                    });

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

            return buildWeatherResponseDto(weatherApiResponseWeatherDto, location);
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
        return URI.create(String.format(BASE_API_URL + DIRECT_PATH + "?q=%s&limit=5&appid=%S", city, getApiKey()));
    }

    private URI buildURI(Location location) {
        return URI.create(String.format(BASE_API_URL + WEATHER_PATH + "?lat=%s&lon=%s&appid=%s", location.getLat(), location.getLon(), getApiKey()));
    }

    private WeatherResponseDto buildWeatherResponseDto(WeatherApiResponseWeatherDto weatherApiResponseWeatherDto, Location location) {
        return WeatherResponseDto.builder()
                .city(location.getName())
                .country(weatherApiResponseWeatherDto.getSys().getCountry())
                .temp((int) Math.round(weatherApiResponseWeatherDto.getMain().getTemp() - 273.15))
                .feelsLike((int) Math.round(weatherApiResponseWeatherDto.getMain().getFeelsLike() - 273.15))
                .description(weatherApiResponseWeatherDto.getWeather()[0].getDescription())
                .humidity(weatherApiResponseWeatherDto.getMain().getHumidity())
                .pressure(weatherApiResponseWeatherDto.getMain().getPressure())
                .speed(weatherApiResponseWeatherDto.getWind().getSpeed())
                .lon(location.getLon())
                .lat(location.getLat())
                .build();
    }
}
