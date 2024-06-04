import org.example.ChangeUser;
import org.example.CreateUser;
import org.example.LoginUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.example.UserController.*;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;

public class ChangeUserDataTest {
    private static CreateUser createUser;
    private static String token;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        createUser = new CreateUser("iramira_irina@yandex.ru","asdf!12345","Iramira");
        executeCreate(createUser);
        token = getUserToken(new LoginUser(createUser.getEmail(), createUser.getPassword()));
    }

    @Test
    @DisplayName("Изменение e-mail пользователя с авторизацией")
    public void successChangeUserEmailWithAuthTest() {
        LoginUser loginUser = new LoginUser(createUser.getEmail(), createUser.getPassword());
        executeLogin(loginUser);

        ChangeUser changeUserEmail = new ChangeUser("iramira_irina777a@yandex.ru", createUser.getName());
        Response response = executeChangeUserData(loginUser,changeUserEmail, true);
        response.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(changeUserEmail.getEmail()))
                .and()
                .body("user.name", equalTo(createUser.getName()))
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Изменение имени пользователя с авторизацией")
    public void successChangeUserNameWithAuthTest() {
        LoginUser loginUser = new LoginUser(createUser.getEmail(), createUser.getPassword());
        executeLogin(loginUser);

        ChangeUser changeUserName = new ChangeUser(createUser.getEmail(), "Amelia");
        Response response = executeChangeUserData(loginUser,changeUserName, true);
        response.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(createUser.getEmail()))
                .and()
                .body("user.name", equalTo(changeUserName.getName()))
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Изменение e-mail пользователя без авторизации")
    public void changeUserEmailWithoutAuthTest() {
        LoginUser loginUser = new LoginUser(createUser.getEmail(), createUser.getPassword());
        executeLogin(loginUser);

        ChangeUser changeUserEmail = new ChangeUser("iramira_irina777a@yandex.ru", createUser.getName());
        Response response = executeChangeUserData(loginUser,changeUserEmail, false);
        response.then().assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }
    @Test
    @DisplayName("Изменение имени пользователя без авторизации")
    public void changeUserNameWithoutAuthTest() {
        LoginUser loginUser = new LoginUser(createUser.getEmail(), createUser.getPassword());
        executeLogin(loginUser);

        ChangeUser changeUserName = new ChangeUser(createUser.getEmail(), "Amelia");
        Response response = executeChangeUserData(loginUser,changeUserName, false);
        response.then().assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @After
    public void deleteChanges() {
        executeDelete(token);
    }
}
