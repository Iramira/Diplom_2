import org.example.CreateUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.example.UserController.executeCreate;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserParametrizedTest {
    private final CreateUser credential;

    public CreateUserParametrizedTest(CreateUser credential) {
        this.credential = credential;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Parameterized.Parameters
    public static Object[][] getJsonCreateVariable() {
        return new Object[][] {
                {new CreateUser("jornoJulia@jojo.com","","Jorno Julia")},
                {new CreateUser("","password12345","Jorno Julia")},
        };
    }
    @Test
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей")
    public void failCreateUserTest() {
        Response response = executeCreate(credential);
        response.then().assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }
}
