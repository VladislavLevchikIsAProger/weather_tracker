package com.vladislavlevchik.utils;

import com.vladislavlevchik.dto.UserRequestDto;
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
}
