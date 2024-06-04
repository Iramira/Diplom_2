import org.example.CreateUser;
import org.example.LoginUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.example.UserController.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CreateUserTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    @Test
    @DisplayName("Создать уникального пользователя")
    public void successSingleTest() {
        CreateUser createUser = new CreateUser("irina_irina123@yandex.ru","password12345","Irina Irina");
        LoginUser loginUser = new LoginUser(createUser.getEmail(), createUser.getPassword());
        if (executeLogin(loginUser).getStatusCode() == SC_OK) {
            executeDelete(loginUser);
        } else {
            Response response = executeCreate(createUser);
            response.then().assertThat()
                    .body("success", equalTo(true))
                    .and()
                    .body("user.email", equalTo(createUser.getEmail()))
                    .and()
                    .body("user.name", equalTo(createUser.getName()))
                    .and()
                    .body("accessToken", startsWith("Bearer"))
                    .and()
                    .body("refreshToken", notNullValue())
                    .and()
                    .statusCode(SC_OK);
        }
    }

    @Test
    @DisplayName("Создать пользователя, который уже зарегистрирован")
    public void failDoubleTest() {
        CreateUser createUser = new CreateUser("irina_irina123@yandex.ru","password12345","Irina Irina");
        executeCreate(createUser);
        Response response = executeCreate(createUser);
        response.then().assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("User already exists"))
                .and()
                .statusCode(SC_FORBIDDEN);

    }

    @After
    public void deleteChanges() {
        LoginUser loginUser = new LoginUser("irina_irina123@yandex.ru","password12345");
        executeDelete(loginUser);
    }
}
