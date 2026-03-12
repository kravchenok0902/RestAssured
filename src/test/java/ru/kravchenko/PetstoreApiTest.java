package ru.kravchenko;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PetstoreApiTest {

    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private static final String PET_ENDPOINT = "/pet";

    @Test
    public void testCreatePet() {
        int petId = generatePetId();
        String petName = "Fluffy";
        String petStatus = "available";

        Response response = createPet(petId, petName, petStatus);
        response.then()
                .statusCode(200)
                .body("id", equalTo(petId))
                .body("name", equalTo(petName))
                .body("status", equalTo(petStatus));
    }

    @Test
    public void testCreateAndGetPet() {
        int petId = generatePetId();
        String petName = "Rex";
        String petStatus = "sold";

        Response createResponse = createPet(petId, petName, petStatus);
        createResponse.then()
                .statusCode(200);

        Response getResponse = getPetById(petId);
        getResponse.then()
                .statusCode(200)
                .body("id", equalTo(petId))
                .body("name", equalTo(petName))
                .body("status", equalTo(petStatus))
                .body("category.name", equalTo("Dogs"))
                .body("photoUrls", hasSize(greaterThan(0)));
    }

    public Response createPet(long petId, String petName, String petStatus) {
        String petJson = """
                {
                  "id": %d,
                  "category": {
                    "id": 1,
                    "name": "Dogs"
                  },
                  "name": "%s",
                  "photoUrls": [
                    "string"
                  ],
                  "tags": [
                    {
                      "id": 0,
                      "name": "string"
                    }
                  ],
                  "status": "%s"
                }
                """.formatted(petId, petName, petStatus);

        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(petJson)
                .when()
                .post(PET_ENDPOINT);
    }

    public Response getPetById(long petId) {
        return given()
                .baseUri(BASE_URL)
                .pathParam("petId", petId)
                .when()
                .get(PET_ENDPOINT + "/{petId}");
    }

    public int generatePetId() {
        double randomValue = Math.random();
        return (int) (randomValue * 100000);
    }
}