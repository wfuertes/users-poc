package users;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.UUID;

import jakarta.ws.rs.core.MediaType;

@QuarkusTest
class UserResourceTest {
    @Test
    void createsAndRetrievesUser() {
        String email = "e2e-" + UUID.randomUUID() + "@example.test";

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(new CreateUserDto(email, "secret-password"))
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("email", is(email))
            .body("password", is("******"))
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue());

        given()
            .queryParam("email", email)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .body("size()", is(1))
            .body("[0].email", is(email))
            .body("[0].password", is("******"));
    }

}