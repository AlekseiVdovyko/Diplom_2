import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import ru.yandex.practicum.models.UserData;
import ru.yandex.practicum.api.UserApi;

import static org.hamcrest.core.Is.is;
import static ru.yandex.practicum.models.UserGenerator.*;

public class CreateUserTest {

    protected UserData userData;
    protected UserApi userApi = new UserApi();

    @Test
    @DisplayName("Check create user /api/auth/register")
    @Description("Can be created user test")
    public void checkUserCanBeCreateTest() {
        userData = getRandomUser("test", "test","Alex");

        ValidatableResponse response = userApi.createUser(userData);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Check create identical user /api/auth/register")
    @Description("Can not create identical user")
    public void checkCanNotCreateIdenticalUserTest() {
        userData = getRandomUser("test", "test","Aleks");
        ValidatableResponse response = userApi.createUser(userData);
        ValidatableResponse responseIdenticalUser = userApi.createUser(userData);

        responseIdenticalUser.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Check create courier without email argument /api/auth/register")
    @Description("Can not create courier without email argument")
    public void checkCanNotCreateUserWithoutEmailArgumentTest() {
        userData = getRandomUserWithoutEmailArgument("test", "Aleks");
        ValidatableResponse response = userApi.createUser(userData);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check create courier without password argument /api/auth/register")
    @Description("Can not create courier without password argument")
    public void checkCanNotCreateUserWithoutPasswordArgumentTest() {
        userData = getRandomUserWithoutPasswordArgument("test", "Aleks");
        ValidatableResponse response = userApi.createUser(userData);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"));
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
