import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.UserData;
import ru.yandex.practicum.api.OrderApi;
import ru.yandex.practicum.api.UserApi;

import static org.hamcrest.CoreMatchers.is;
import static ru.yandex.practicum.models.UserGenerator.getRandomUser;

public class UserOrdersTest {

    protected UserData userData;
    protected UserApi userApi = new UserApi();
    protected OrderApi orderApi = new OrderApi();
    protected String accessToken;

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
    }

    @Test
    @DisplayName("Check all orders user with authorization /api/orders")
    @Description("Can get all orders user")
    public void getAllOrdersUserWithAuthorizationTest() {
        ValidatableResponse response = orderApi.getOrdersUser(accessToken);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Check get orders without authorization /api/orders")
    @Description("Can`t get orders without authorization")
    public void getAllOrdersWithoutAuthorizationTest() {
        ValidatableResponse response = orderApi.getOrdersUserWithoutAuthorization();

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", is("You should be authorised"));
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
