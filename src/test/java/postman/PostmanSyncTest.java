package postman;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import testcase.ServiceRequestTestData;
import testcase.ServiceTestCase;
import util.DataImportUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static assertions.ITAssertions.assertThat;

public abstract class PostmanSyncTest {

    private static JsonNode postmanCollectionJson;

    @Before
    public void setUp() throws Exception {
        postmanCollectionJson = DataImportUtil.loadJsonFile(postmanCollectionFilePath());
    }


    @Test
    public void checkPostmanCollectionMatchesRequestJSON() throws Exception {
        final ServiceTestCase serviceTestCase = new ServiceTestCase(serviceTestCasesFilePath());

        for (final Map.Entry<String, List<ServiceRequestTestData>> entry : serviceTestCase.retrieveRequestTestDataAsMap().entrySet()) {

            final String directoryName = entry.getKey();
            final List<ServiceRequestTestData> testsInDirectory = entry.getValue();

            for (final ServiceRequestTestData requestTestData : testsInDirectory) {
                final String postmanRequestName = requestTestData.getRequestName();
                final ObjectNode expectedRequestJson = requestTestData.getRequestBody();
                checkPostmanRequestBodyMatches(postmanRequestName, expectedRequestJson, directoryName);
            }
        }
    }

    public abstract String postmanCollectionFilePath();

    public abstract String serviceTestCasesFilePath();


    private void checkPostmanRequestBodyMatches(final String postmanRequestName,
                                                final JsonNode expected, final String directoryName) throws
            IOException {
        JsonNode result = retrievePostmanRequestItem(postmanRequestName, directoryName);

        JsonNode bodyJson = DataImportUtil.OBJECT_MAPPER.readTree(
                result.path("request")
                        .path("body")
                        .path("raw")
                        .asText());

        assertThat(bodyJson).isEqualTo(expected);
    }

    private static JsonNode retrievePostmanRequestItem(final String postmanRequestName,
                                                       final String directoryName) {
        final JsonNode directory = findNodeWithName(postmanCollectionJson.get("item"), directoryName);

        JsonNode itemCollectionArray = directory.get("item");

        return findNodeWithName(itemCollectionArray, postmanRequestName);
    }


    private static JsonNode findNodeWithName(final JsonNode itemCollectionArray, final String name) {

        for (final JsonNode item : itemCollectionArray) {
            if (name.equals(item.get("name").asText())) {
                return item;
            }
        }

        throw new AssertionError("Could not find node with name " + name);
    }


}
