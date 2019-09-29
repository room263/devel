import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

class yandexApiTest
{
    private ApiKey yandexApiKey;
    private String url;

     yandexApiTest(String url, ApiKey apiKey)
    {
        this.url = url;
        yandexApiKey = apiKey;
    }

    JsonPath getRequest(Map<String, String> queryParams)
    {
        Response response =  given().header(yandexApiKey.getKeyName(), yandexApiKey.getGuid()).queryParams(queryParams).get(url);

        return response.jsonPath();
    }

}
