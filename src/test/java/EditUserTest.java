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
import static ru.yandex.practicum.models.UserGenerator.*;

public class EditUserTest {
    protected UserData userData;
    protected UserApi userApi = new UserApi();
    protected String accessToken;
    protected String emailUser;
    protected String nameUser;

    @Before
    public void setUpAndCreateUser() {
        userData = getRandomUser("test", "test", "Aleks");
        ValidatableResponse response = userApi.createUser(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
        ValidatableResponse responseLoginUser = userApi.loginUser(userData);
        accessToken = responseLoginUser.extract().jsonPath().get("accessToken").toString().substring(7);
        emailUser = responseLoginUser.extract().jsonPath().get("user.email").toString();
        nameUser = responseLoginUser.extract().jsonPath().get("user.name").toString();
    }

    @Test
    @DisplayName("Check authorization user can edit email /api/auth/user")
    @Description("Check authorization user can edit email")
    public void checkAuthorizationUserCanEditEmailTest() {
        userData = new UserData("update-" + emailUser, null, nameUser);
        ValidatableResponse response = userApi.editUser(accessToken, userData);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Check authorization user can edit name /api/auth/user")
    @Description("Check authorization user can edit name")
    public void checkAuthorizationUserCanEditNameTest() {
        userData = new UserData(emailUser, null, "update-" + nameUser);
        ValidatableResponse response = userApi.editUser(accessToken, userData);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Check not authorization user can`t edit email /api/auth/user")
    @Description("Check not authorization user can`t edit email")
    public void checkNotAuthorizationUserCantEditEmailTest() {
        UserData editEmailUser = new UserData("update-" + emailUser, null, nameUser);
        ValidatableResponse response = userApi.editUserWithoutAuthorization(editEmailUser);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", is("You should be authorised"));
    }

    @Test
    @DisplayName("Check not authorization user can`t edit name /api/auth/user")
    @Description("Check not authorization user can`t edit name")
    public void checkNotAuthorizationUserCantEditNameTest() {
        UserData editNameUser = new UserData(emailUser, null, "update-" + nameUser);
        ValidatableResponse response = userApi.editUserWithoutAuthorization(editNameUser);

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
                    .body("success", is(true));
        }
    }
}
