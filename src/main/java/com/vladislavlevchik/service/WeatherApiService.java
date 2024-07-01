package com.vladislavlevchik.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladislavlevchik.dto.WeatherApiResponseDto;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class WeatherApiService {

    private static final String API_KEY = "82a165d34bb59b30c17b7dbc1ea9a409";
    private static final int LIMIT = 5;

    public List<WeatherApiResponseDto> getList(String city) throws ServletException, IOException {
        // Формирование URL для запроса
        String urlString = String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=%d&appid=%s", city, LIMIT, API_KEY);

        // Выполнение HTTP-запроса с использованием HttpClient
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();
        HttpResponse<String> res;
        try {
            res = client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new ServletException("Error during API call", e);
        }

        // Разбор JSON с использованием Jackson и десериализация в List<WeatherApiResponseDto>
        ObjectMapper objectMapper = new ObjectMapper();
        List<WeatherApiResponseDto> weatherApiResponseDtoList = objectMapper.readValue(res.body(), new TypeReference<List<WeatherApiResponseDto>>(){});

        return weatherApiResponseDtoList;
    }

}
