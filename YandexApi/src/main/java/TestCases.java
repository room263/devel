import static io.restassured.RestAssured.get;
import static org.junit.jupiter.api.Assertions.*;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

class TestCases
{
    private yandexApiTest yandexApiTest;
    private Map<String, String> queryParams;

    private enum SeasonNames { winter, spring, summer, autumn }
    private EnumMap<Month,SeasonNames> seasons;
    private List<String> moonCodes;

    @BeforeEach
    void init()
    {
        ApiKey yandexApiKey = new ApiKey("X-Yandex-API-Key","2ad3fbbe-94df-4525-ade5-01ec6db1403a");
        yandexApiTest = new yandexApiTest("https://api.weather.yandex.ru/v1/forecast", yandexApiKey);

        queryParams = new HashMap<>();
        queryParams.put("lat", "55.75396");
        queryParams.put("lon", "37.62039");
        queryParams.put("limit", "2");

        seasons = new EnumMap<>(Month.class);
        seasons.put(Month.JANUARY, SeasonNames.winter);
        seasons.put(Month.FEBRUARY, SeasonNames.winter);
        seasons.put(Month.MARCH, SeasonNames.spring);
        seasons.put(Month.APRIL, SeasonNames.spring);
        seasons.put(Month.MAY, SeasonNames.spring);
        seasons.put(Month.JUNE, SeasonNames.summer);
        seasons.put(Month.JULY, SeasonNames.summer);
        seasons.put(Month.AUGUST, SeasonNames.summer);
        seasons.put(Month.SEPTEMBER, SeasonNames.autumn);
        seasons.put(Month.OCTOBER, SeasonNames.autumn);
        seasons.put(Month.NOVEMBER, SeasonNames.autumn);
        seasons.put(Month.DECEMBER, SeasonNames.winter);

        moonCodes = new ArrayList<>();
        moonCodes.add("full-moon");
        moonCodes.add("decreasing-moon");
        moonCodes.add("decreasing-moon");
        moonCodes.add("decreasing-moon");
        moonCodes.add("last-quarter");
        moonCodes.add("decreasing-moon");
        moonCodes.add("decreasing-moon");
        moonCodes.add("decreasing-moon");
        moonCodes.add("new-moon");
        moonCodes.add("growing-moon");
        moonCodes.add("growing-moon");
        moonCodes.add("growing-moon");
        moonCodes.add("first-quarter");
        moonCodes.add("growing-moon");
        moonCodes.add("growing-moon");
        moonCodes.add("growing-moon");
    }

    @Test
    void checkLat()
    {
        String expected = queryParams.get("lat");

        JsonPath jsonPath = yandexApiTest.getRequest(queryParams);
        String actual = jsonPath.getString("info.lat");

        assertEquals(expected, actual);
    }

    @Test
    void checkLon()
    {
        String expected = queryParams.get("lon");

        JsonPath jsonPath = yandexApiTest.getRequest(queryParams);
        String actual = jsonPath.getString("info.lon");

        assertEquals(expected, actual);
    }

    @Test
    void checkOffset()
    {
        int expected = TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET) / 1000;

        JsonPath jsonPath = yandexApiTest.getRequest(queryParams);
        int actual = jsonPath.getInt("info.tzinfo.offset");

        assertEquals(expected, actual);
    }

    @Test
    void checkTimeName()
    {
        String expected = TimeZone.getDefault().getID();

        JsonPath jsonPath = yandexApiTest.getRequest(queryParams);
        String actual = jsonPath.getString("info.tzinfo.name");

        assertEquals(expected, actual);
    }


    @Test
    void checkAbbrTimeName()
    {
        String expected = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT, Locale.US);

        JsonPath jsonPath = yandexApiTest.getRequest(queryParams);
        String actual = jsonPath.getString("info.tzinfo.abbr");

        assertEquals(expected, actual);
    }


    @Test
    void checkDst()
    {
        Boolean expected = TimeZone.getDefault().getDSTSavings() == 3600000;

        JsonPath jsonPath = yandexApiTest.getRequest(queryParams);
        Boolean actual = jsonPath.getBoolean("info.tzinfo.dst");

        assertEquals(expected, actual);
    }


    @Test
    void checkUrl()
    {
        int expectedExitCode = 200;
        Map<String, String> expectedParams = new  HashMap<String, String>();
        expectedParams.put("lat", queryParams.get("lat"));
        expectedParams.put("lon", queryParams.get("lon"));

        JsonPath jsonPath = yandexApiTest.getRequest(queryParams);
        String url = jsonPath.getString("info.url");

        int actualExitCode = get(url).statusCode();
        Map<String, String> actualParams = getQueryParams(url);

        assertEquals(expectedExitCode, actualExitCode);
        assertEquals(expectedParams, actualParams);
    }

    @Test
    void checkNumberOfDays()
    {
        int expected = Integer.parseInt(queryParams.get("limit"));
        JsonPath jsonPath = yandexApiTest.getRequest(queryParams);
        int actual = jsonPath.getList("forecasts").size();

        assertEquals(expected, actual);
    }

    @Test
    void checkSeason()
    {
        String expected = seasons.get(LocalDate.now().getMonth()).name();

        JsonPath jsonPath = yandexApiTest.getRequest(queryParams);
        String actual = jsonPath.getString("fact.season");

        assertEquals(expected, actual);
    }

    @Test
    void checkMoonCode()
    {
        JsonPath jsonPath = yandexApiTest.getRequest(queryParams);
        List<HashMap<String, Integer>> secondDayDataForMoonCode = jsonPath.getList("forecasts");
        int secondDayMoonCode = secondDayDataForMoonCode.get(1).get("moon_code");
        String expected = moonCodes.get(secondDayMoonCode);

        List<HashMap<String, String>> secondDayDataForMoonTxt = jsonPath.getList("forecasts");
        String actual = secondDayDataForMoonTxt.get(1).get("moon_text");

        assertEquals(expected, actual);
    }


    private static Map<String, String> getQueryParams(String query)
    {
        String[] onlyParamsString = query.split("\\?");
        String[] params = onlyParamsString[1].split("&");

        Map<String, String> result = new HashMap<>();
        for (String param : params)
        {
            String[] nameValue = param.split("=");
            result.put(nameValue[0], nameValue[1]);
        }
        return result;
    }
}
