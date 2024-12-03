package assignment4;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * API Testing Class
 * This class serves as a foundation for testing various API endpoints.
 * It includes basic validations and documents areas for enhancement and future updates.
 */
public class ApiTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    /**
     * Validates a single GET request by verifying the status code, content type, 
     * and ensuring the response body contains expected data.
     * Future enhancement: Include more comprehensive checks for different response scenarios.
     */
    @Test
    public void testGetRequest() {
        Response response = RestAssured.get(BASE_URL + "/posts/1");
        Assert.assertEquals(response.getStatusCode(), 200, "Unexpected status code.");
        Assert.assertNotNull(response.getBody(), "Response body is null.");
        Assert.assertTrue(response.getBody().asString().contains("userId"), "Response does not contain 'userId'.");
        Assert.assertEquals(response.getContentType(), "application/json; charset=utf-8", "Incorrect content type.");
    }

    /**
     * Fetches and validates all posts from the API by verifying the response status 
     * and ensuring data is returned. 
     * Future plans: Add specific validations for individual posts.
     */
    @Test
    public void testAllPosts() {
        Response response = RestAssured.get(BASE_URL + "/posts");
        Assert.assertEquals(response.getStatusCode(), 200, "Unexpected status code.");
        Assert.assertTrue(response.getBody().jsonPath().getList("$").size() > 0, "No data found in the response.");
    }

    /**
     * Sends a POST request to create a new post and verifies the operation was successful.
     * Known limitation: The API may not persist the post data due to being a mock service.
     * Future improvement: Handle error scenarios and validate response consistency under different conditions.
     */
    @Test
    public void testPostRequest() {
        // Perform POST request
        Response response = RestAssured.given()
            .contentType("application/json")
            .body("{ \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1 }")
            .post(BASE_URL + "/posts");

        // Assert POST response
        Assert.assertEquals(response.getStatusCode(), 201, "Unexpected status code.");
        Assert.assertNotNull(response.getBody(), "Response body is null.");
        Assert.assertTrue(response.getBody().asString().contains("\"id\":"), "Response does not contain 'id'.");

        // Extract the ID of the newly created post
        int postId = response.jsonPath().getInt("id");
        System.out.println("New Post ID: " + postId);

        // Fetch the newly created post
        Response getResponse = RestAssured.get(BASE_URL + "/posts/" + postId);

        // Handle scenarios where the post is not found due to API limitations
        if (getResponse.getStatusCode() == 404) {
            System.out.println("Warning: Unable to locate the created post. This might be due to mock API restrictions.");
        } else {
            Assert.assertEquals(getResponse.getStatusCode(), 200, "Unexpected status code while fetching the new post.");
            Assert.assertTrue(getResponse.getBody().asString().contains("\"id\":" + postId), "Fetched post does not match the created post ID.");
        }
    }
}
