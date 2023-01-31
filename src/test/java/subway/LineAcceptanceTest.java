package subway;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql("/init.sql")
public class LineAcceptanceTest {
    private final SubwayRestApiClient client = new SubwayRestApiClient();

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = client.createLine(TestFixture.SinBunDangLine);

        // then
        RestAssuredValidationUtils.validateStatusCode(response, HttpStatus.CREATED);
        RestAssuredValidationUtils.validateFieldEquals(response, "name", String.class, "신분당선");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findLines() {
        // given
        client.createLine(TestFixture.SinBunDangLine);
        client.createLine(TestFixture.BunDangLine);

        // when
        ExtractableResponse<Response> response = client.findAllLines();
        List<String> lineNames = response.jsonPath().getList("name", String.class);

        // then
        RestAssuredValidationUtils.validateStatusCode(response, HttpStatus.OK);
        RestAssuredValidationUtils.validateFieldBodyHasSize(lineNames, 2);
        RestAssuredValidationUtils.validateFieldContainsExactly(lineNames, "신분당선", "분당선");
    }

    @DisplayName("특정 지하철 노선을 조회한다.")
    @Test
    void findLineById() {
        // given
        var line = client.createLine(TestFixture.SinBunDangLine);
        long lineId = line.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = client.findLineById(lineId);

        // then
        RestAssuredValidationUtils.validateStatusCode(response, HttpStatus.OK);
        RestAssuredValidationUtils.validateFieldEquals(response, "id", Long.class, lineId);
    }

    @DisplayName("특정 지하철 노선의 정보를 갱신한다.")
    @Test
    void updateLine() {
        // given
        var line = client.createLine(TestFixture.SinBunDangLine);
        long lineId = line.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = client.updateLine(lineId, TestFixture.BunDangLine);

        // then
        RestAssuredValidationUtils.validateStatusCode(response, HttpStatus.OK);
        RestAssuredValidationUtils.validateFieldEquals(response, "id", Long.class, lineId);
        RestAssuredValidationUtils.validateFieldEquals(response, "name", String.class, "분당선");
    }

    @DisplayName("특정 지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        var line = client.createLine(TestFixture.SinBunDangLine);
        long lineId = line.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> deleteResponse = client.deleteLine(lineId);
        ExtractableResponse<Response> findResponse = client.findLineById(lineId);

        // then
        RestAssuredValidationUtils.validateStatusCode(deleteResponse, HttpStatus.NO_CONTENT);
        RestAssuredValidationUtils.validateStatusCode(findResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
