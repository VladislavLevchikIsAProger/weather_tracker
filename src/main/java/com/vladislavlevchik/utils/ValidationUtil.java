package com.vladislavlevchik.utils;

import com.vladislavlevchik.dto.UserRequestDto;
import com.vladislavlevchik.exception.InvalidLoginException;
import com.vladislavlevchik.exception.InvalidPasswordException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationUtil {

    public static final String LOGIN_VALIDATION_REGEX = "^[a-zA-Z]+$";
    private static final String PASSWORD_VALIDATION_REGEX = "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]+$";


    public static void validate(UserRequestDto user){
        if(!user.getLogin().matches(LOGIN_VALIDATION_REGEX)){
            throw new InvalidLoginException();
        }

        if (!user.getPassword().matches(PASSWORD_VALIDATION_REGEX)){
            throw new InvalidPasswordException();
        }
    }
}
