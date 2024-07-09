package com.vladislavlevchik.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladislavlevchik.dto.WeatherResponseDto;
import com.vladislavlevchik.dto.api.WeatherApiResponseDirectDto;
import com.vladislavlevchik.entity.Location;
import com.vladislavlevchik.exception.api.EmptyBodyException;
import com.vladislavlevchik.exception.api.GeoLocationException;
import com.vladislavlevchik.exception.api.InvalidStatusCodeException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WeatherApiServiceTest {
    @Mock
    private HttpClient mockHttpClient;
    @Mock
    private HttpResponse<String> mockHttpResponse;

    private WeatherApiService weatherApiService;

    @BeforeEach
    void setUp() {
        weatherApiService = new WeatherApiService(mockHttpClient, new ObjectMapper());
    }

    private void setUpHttpClientMock() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);
    }


    @Nested
    @DisplayName("Geo Locations Info Tests")
    class GeoLocationInfoTest {

        @Test
        void testGetGeoLocationInfoSuccess() throws Exception {
            String jsonResponse = "[{\"name\":\"London\",\"lat\":51.5073219,\"lon\":-0.1276474, \"country\":\"GB\"}]";

            setUpHttpClientMock();
            when(mockHttpResponse.body()).thenReturn(jsonResponse);

            List<WeatherApiResponseDirectDto> result = weatherApiService.getGeoLocationInfo("London");

            assertNotNull(result);
            assertFalse(result.isEmpty());

            WeatherApiResponseDirectDto dto = result.get(0);

            assertEquals("London", dto.getName());
            assertEquals(51.5073219, dto.getLat());
            assertEquals(-0.1276474, dto.getLon());
            assertEquals("GB", dto.getCountry());
        }

        @Test
        void testGetGeoLocationInfoWithInvalidCity() throws IOException, InterruptedException {
            setUpHttpClientMock();
            when(mockHttpResponse.body()).thenReturn("[]");

            List<WeatherApiResponseDirectDto> response = weatherApiService.getGeoLocationInfo("UnknownCity");

            assertTrue(response.isEmpty());
        }

        @Test
        void testGetGeoLocationInfoWithServerError() throws IOException, InterruptedException {
            setUpHttpClientMock();
            when(mockHttpResponse.statusCode()).thenReturn(500);

            assertThrows(InvalidStatusCodeException.class,
                    () -> weatherApiService.getGeoLocationInfo("London"));
        }

        @Test
        void testGetGeoLocationInfoWithClientError() throws IOException, InterruptedException {
            setUpHttpClientMock();
            when(mockHttpResponse.statusCode()).thenReturn(404);

            assertThrows(InvalidStatusCodeException.class,
                    () -> weatherApiService.getGeoLocationInfo("London"));
        }

        @Test
        void testGetGeoLocationInfoWithEmptyResponseBody() throws IOException, InterruptedException {
            setUpHttpClientMock();
            when(mockHttpResponse.body()).thenReturn("");

            assertThrows(EmptyBodyException.class,
                    () -> weatherApiService.getGeoLocationInfo("London"));
        }

        @Test
        void testGetGeoLocationInfoWithInvalidResponseFormat() throws IOException, InterruptedException {
            String invalidResponse = "{\"invalid\":\"response\"}";

            setUpHttpClientMock();
            when(mockHttpResponse.body()).thenReturn(invalidResponse);

            assertThrows(GeoLocationException.class,
                    () -> weatherApiService.getGeoLocationInfo("London"));
        }
    }

    @Nested
    @DisplayName("Weather Info Tests")
    class WeatherInfoTest {

        @Test
        void testGetWeatherInfoSuccess() throws IOException, InterruptedException {
            String jsonResponse = getJsonResponseForWeather();

            setUpHttpClientMock();
            when(mockHttpResponse.body()).thenReturn(jsonResponse);

            Location location = Location.builder()
                    .name("London")
                    .lat(51.5081)
                    .lon(-0.1283)
                    .build();

            WeatherResponseDto response = weatherApiService.getWeatherInfo(location);

            assertEquals("London", response.getCity());
            assertEquals("GB", response.getCountry());
            assertEquals(289, response.getTemp());
            assertEquals(289, response.getFeelsLike());
            assertEquals("overcast clouds", response.getDescription());
            assertEquals(90, response.getHumidity());
            assertEquals(1010, response.getPressure());
            assertEquals(3, response.getSpeed());
            assertEquals(location.getLat(), response.getLat());
            assertEquals(location.getLon(), response.getLon());
        }

        @Test
        void testGetWeatherInfoWithEmptyResponseBody() throws IOException, InterruptedException {
            setUpHttpClientMock();
            when(mockHttpResponse.body()).thenReturn("");

            Location location = Location.builder()
                    .name("London")
                    .lat(51.5081)
                    .lon(-0.1283)
                    .build();

            assertThrows(EmptyBodyException.class,
                    () -> weatherApiService.getWeatherInfo(location));
        }

        @Test
        void testGetWeatherInfoWithServerError() throws IOException, InterruptedException {
            setUpHttpClientMock();
            when(mockHttpResponse.statusCode()).thenReturn(500);

            Location location = Location.builder()
                    .name("London")
                    .lat(51.5081)
                    .lon(-0.1283)
                    .build();

            assertThrows(InvalidStatusCodeException.class,
                    () -> weatherApiService.getWeatherInfo(location));
        }

        @Test
        void testGetWeatherInfoWithClientError() throws IOException, InterruptedException {
            setUpHttpClientMock();
            when(mockHttpResponse.statusCode()).thenReturn(404);

            Location location = Location.builder()
                    .name("London")
                    .lat(51.5081)
                    .lon(-0.1283)
                    .build();

            assertThrows(InvalidStatusCodeException.class,
                    () -> weatherApiService.getWeatherInfo(location));
        }
    }

    private String getJsonResponseForWeather() {
        return """
                {
                    "coord": {
                        "lon": -0.1283,
                        "lat": 51.5081
                    },
                    "weather": [
                        {
                            "id": 804,
                            "main": "Clouds",
                            "description": "overcast clouds",
                            "icon": "04d"
                        }
                    ],
                    "base": "stations",
                    "main": {
                        "temp": 288.89,
                        "feels_like": 288.87,
                        "temp_min": 288.09,
                        "temp_max": 289.66,
                        "pressure": 1010,
                        "humidity": 90,
                        "sea_level": 1010,
                        "grnd_level": 1006
                    },
                    "visibility": 10000,
                    "wind": {
                        "speed": 3.09,
                        "deg": 110
                    },
                    "clouds": {
                        "all": 100
                    },
                    "dt": 1720506082,
                    "sys": {
                        "type": 2,
                        "id": 2075535,
                        "country": "GB",
                        "sunrise": 1720497273,
                        "sunset": 1720556196
                    },
                    "timezone": 3600,
                    "id": 2643743,
                    "name": "London",
                    "cod": 200
                }""";
    }
}
