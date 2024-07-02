package com.vladislavlevchik.utils;

import com.vladislavlevchik.dto.UserRequestDto;
import com.vladislavlevchik.dto.api.WeatherApiResponseWeatherDto;
import com.vladislavlevchik.dto.WeatherResponseDto;
import com.vladislavlevchik.entity.User;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;

@UtilityClass
public class MapperUtil {

    private static final ModelMapper MODEL_MAPPER;

    static {
        MODEL_MAPPER = new ModelMapper();
    }

    public static User convertToEntity(UserRequestDto userRequestDto) {
        return MODEL_MAPPER.map(userRequestDto, User.class);
    }

    public static WeatherResponseDto convertToDto(WeatherApiResponseWeatherDto weatherApiResponseWeatherDto) {
        return WeatherResponseDto.builder()
                .temp((int) Math.round(weatherApiResponseWeatherDto.getMain().getTemp() - 273.15))
                .feelsLike((int) Math.round(weatherApiResponseWeatherDto.getMain().getFeelsLike() - 273.15))
                .description(weatherApiResponseWeatherDto.getWeather()[0].getDescription())
                .humidity(weatherApiResponseWeatherDto.getMain().getHumidity())
                .pressure(weatherApiResponseWeatherDto.getMain().getPressure())
                .speed(weatherApiResponseWeatherDto.getWind().getSpeed())
                .build();
    }
}
