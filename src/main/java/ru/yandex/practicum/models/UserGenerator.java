package ru.yandex.practicum.models;

import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {

    @Step("Generate random user with all parameters")
    public static UserData getRandomUser(String emailParam, String passwordParam, String nameParam) {
        String email = emailParam + RandomStringUtils.randomAlphabetic(3) + "@" + RandomStringUtils.randomAlphabetic(3) + ".ru";
        String password = passwordParam + RandomStringUtils.randomAlphabetic(3);
        String name = nameParam + RandomStringUtils.randomAlphabetic(3);

        return new UserData(email, password, name);
    }

    @Step("Generate random user without email")
    public static UserData getRandomUserWithoutEmailArgument(String passwordParam, String nameParam) {
        String password = passwordParam + RandomStringUtils.randomAlphabetic(3);
        String name = nameParam + RandomStringUtils.randomAlphabetic(3);

        return new UserData(null, password, name);
    }

    @Step("Generate random user without password")
    public static UserData getRandomUserWithoutPasswordArgument(String emailParam, String nameParam) {
        String email = emailParam + RandomStringUtils.randomAlphabetic(3) + "@" + RandomStringUtils.randomAlphabetic(3) + ".ru";
        String name = nameParam + RandomStringUtils.randomAlphabetic(3);

        return new UserData(email, null, name);
    }
}
