package ru.yandex.practicum.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.practicum.models.OrderData;

import static io.restassured.RestAssured.given;
import static ru.yandex.practicum.constants.Constants.*;

public class OrderApi extends RestApi{

    @Step("Send POST request to /api/orders")
    public ValidatableResponse createOrder(String accessToken, OrderData orderData) {
        return given()
                .spec(requestSpecification())
                .auth().oauth2(accessToken)
                .and()
                .body(orderData)
                .when()
                .post(CREATE_ORDER_URI)
                .then();
    }

    @Step("Send POST request with ingredients to /api/orders")
    public ValidatableResponse createOrderWithIngredients(OrderData orderData) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(orderData)
                .when()
                .post(CREATE_ORDER_URI)
                .then();
    }

    @Step("Send POST request with authorization to /api/orders")
    public ValidatableResponse createOrderWithAuthorization(String accessToken) {
        return given()
                .spec(requestSpecification())
                .auth().oauth2(accessToken)
                .when()
                .post(CREATE_ORDER_URI)
                .then();
    }


    @Step("Send GET request user orders to /api/orders")
    public ValidatableResponse getOrdersUser(String accessToken) {
        return given()
                .spec(requestSpecification())
                .auth().oauth2(accessToken)
                .when()
                .get(GET_ORDERS_URI)
                .then();
    }

    @Step("Send GET request all orders to /api/orders")
    public ValidatableResponse getOrdersUserWithoutAuthorization() {
        return given()
                .spec(requestSpecification())
                .when()
                .get(GET_ORDERS_URI)
                .then();
    }

    @Step("Send GET request all ingredients to /api/ingredients")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(requestSpecification())
                .when()
                .get(GET_INGREDIENTS_URI)
                .then();
    }
}
