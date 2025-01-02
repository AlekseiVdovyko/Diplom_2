import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.UserData;
import ru.yandex.practicum.api.UserApi;

import static org.hamcrest.core.Is.is;
import static ru.yandex.practicum.models.UserGenerator.getRandomUser;

public class LoginUserTest {
    protected UserData userData;
    protected UserApi userApi = new UserApi();

    @Before
    public void setUpAndCreateUser() {
        userData = getRandomUser("test", "test", "Aleks");
        ValidatableResponse response = userApi.createUser(userData);
    }

    @Test
    @DisplayName("Check login user /api/auth/login")
    @Description("Can login created user")
    public void checkLoginAuthorizedUserTest() {
        ValidatableResponse responseLoginUser = userApi.loginUser(userData);

        responseLoginUser.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Check login nonexistent user /api/auth/login")
    @Description("Can not login nonexistent user")
    public void checkLoginNonexistentUserTest() {
        UserData userNonexistent = getRandomUser("test", "test", "test");
        ValidatableResponse response = userApi.loginUser(userNonexistent);

        response.log().all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", is("email or password are incorrect"));
    }

    @After
    public void deleteCreatedUser() {
        ValidatableResponse responseLoginUser = userApi.loginUser(userData);

        if(responseLoginUser.extract().statusCode() == HttpStatus.SC_OK) {
            String accessToken = responseLoginUser.extract().jsonPath().get("accessToken").toString().substring(7);
            ValidatableResponse responseDeleteUser = userApi.deleteUser(accessToken);

            responseDeleteUser.log().all()
                    .assertThat()
                    .statusCode(HttpStatus.SC_ACCEPTED)
                    .body("success", is(true));
        }
    }
}
