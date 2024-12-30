import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.OrderData;
import ru.yandex.practicum.models.UserData;
import ru.yandex.practicum.api.OrderApi;
import ru.yandex.practicum.api.UserApi;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static ru.yandex.practicum.models.UserGenerator.getRandomUser;

public class CreateOrderTest {

    protected OrderData orderData = new OrderData();;
    protected OrderApi orderApi = new OrderApi();
    protected UserData userData;
    protected UserApi userApi = new UserApi();
    protected String accessToken;
    protected List<String> ingredients;

    @Before
    public void createUser() {
        userData = getRandomUser("test", "test", "Aleks");
        ValidatableResponse response = userApi.createUser(userData);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", Is.is(true));
        ValidatableResponse responseLoginUser = userApi.loginUser(userData);
        accessToken = responseLoginUser.extract().jsonPath().get("accessToken").toString().substring(7);

        JsonPath ingredient = orderApi.getIngredients().extract().jsonPath();

        ingredients = List.of(ingredient.get("data._id[0]").toString(),
                ingredient.get("data._id[4]").toString(),
                ingredient.get("data._id[9]").toString());
    }

    @Test
    @DisplayName("Check create order with authorization /api/orders")
    @Description("Can create order with authorization and ingredients")
    public void checkOrderWithAuthorizationAndIngredientsTest() {
        orderData.setIngredients(ingredients);
        ValidatableResponse response = orderApi.createOrder(accessToken, orderData);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Check create order without authorization /api/orders")
    @Description("Can create order without authorization")
    public void checkOrderWithIngredientsTest() {
        orderData.setIngredients(ingredients);
        ValidatableResponse response = orderApi.createOrderWithIngredients(orderData);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Check create order with authorization /api/orders")
    @Description("Can`t create order with authorization")
    public void checkOrderWithAuthorizationTest() {
        ValidatableResponse response = orderApi.createOrderWithAuthorization(accessToken);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Check create order /api/orders")
    @Description("Can`t be created order with wrong ingredient test")
    public void checkOrderWithWrongIngredientsTest() {
        ingredients = List.of("INGREDIENT_NONEXISTENT");
        orderData.setIngredients(ingredients);

        ValidatableResponse response = orderApi.createOrder(accessToken, orderData);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void deleteCreatedUser() {
        ValidatableResponse responseLoginUser = userApi.loginUser(userData);

        if(responseLoginUser.extract().statusCode() == HttpStatus.SC_OK) {
            ValidatableResponse responseDeleteUser = userApi.deleteUser(accessToken);

            responseDeleteUser.log().all()
                    .assertThat()
                    .statusCode(HttpStatus.SC_ACCEPTED)
                    .body("success", Is.is(true));
        }
    }
}
