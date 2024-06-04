import org.example.CreateOrder;
import org.example.CreateUser;
import org.example.LoginUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static org.example.OrderController.executeGetOrder;
import static org.example.OrderController.executeMakeOrder;
import static org.example.UserController.*;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
public class GetOrderTest {
    private static CreateUser createUser;
    private static String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";

    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void successGetOrdersWithAuthTest() {
        createUser = new CreateUser("john_smith123@yandex.ru", "pass!wor%d12345", "John Smith");
        executeCreate(createUser);
        token = getUserToken(new LoginUser(createUser.getEmail(), createUser.getPassword()));
        CreateOrder createOrder = new CreateOrder(new String[]{"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"});
        executeMakeOrder(createOrder);
        Response response = executeGetOrder(token, true);
        response.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .body("orders", notNullValue())
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void successGetOrdersWithoutAuthTest() {
        Response response = executeGetOrder(token,false);
        response.then().assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @AfterClass
    public static void deleteChanges() {
        executeDelete(token);
    }
}
