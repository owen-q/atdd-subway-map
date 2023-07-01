package line;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.SchemaInitSql;
import subway.StationInitSql;
import subway.SubwayApplication;
import subway.line.LineCreateRequest;

@SchemaInitSql
@StationInitSql
@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(classes = SubwayApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {
    private static final String API_CREATE_LINE = "/lines";
    private static final String API_GET_LINE = "/lines";

    @DisplayName("노선을 생성한다")
    @Test
    void createLine() {
        ExtractableResponse<Response> createdResponse = 노선생성("신분당선", "bg-red-600", 1, 2, 10);

        // then
        assertThat(createdResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createdResponse.jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(createdResponse.jsonPath().getString("color")).isEqualTo("bg-red-600");
        assertThat(createdResponse.jsonPath().getList("stations.id")).containsSequence(List.of(1, 2));
    }

    private ExtractableResponse<Response> 노선생성(String name, String color, long upStationId, long downStationId, int distance) {
        LineCreateRequest request = new LineCreateRequest(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                          .body(request)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post(API_CREATE_LINE)
                          .then().log().all()
                          .extract();
    }

    @DisplayName("지하철 노선을 조회한다")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 노선생성("신분당선", "bg-red-600", 1, 2, 10);

        Long createdLineId = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                   .contentType(MediaType.APPLICATION_JSON_VALUE)
                   .when().get(getLineRequestUrl(createdLineId))
                   .then().log().all()
                   .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("bg-red-600");
        assertThat(response.jsonPath().getList("stations.id")).containsSequence(List.of(1, 2));
    }

    private String getLineRequestUrl(long id) {
        return API_GET_LINE + "/" + id;
    }
}
