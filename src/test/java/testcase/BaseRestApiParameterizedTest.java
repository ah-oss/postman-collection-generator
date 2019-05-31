package testcase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.annotation.Nullable;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static assertions.ITAssertions.assertThat;


@RunWith(Parameterized.class)
public abstract class BaseRestApiParameterizedTest<T> {

    private final String requestName;
    private final ObjectNode requestBody;
    private final T expectedResponse;

    /**
     * Provide your rest service under test URL
     */
    protected abstract String getServiceUrl();


    private Client client;

    public BaseRestApiParameterizedTest(final String requestName,
                                        final ObjectNode requestBody,
                                        final T response) {
        this.requestName = requestName;
        this.requestBody = requestBody;
        this.expectedResponse = response;
    }

    @Before
    public void setUp() {
        client = ClientBuilder.newClient();
    }


    protected static List<Object[]> buildData(String serviceTestCasesFilePath) {
        final ServiceTestCase serviceTestCase = new ServiceTestCase(serviceTestCasesFilePath);
        return Lists.transform(serviceTestCase.retrieveServiceTestDataAsList(), new Function<ServiceRequestTestData, Object[]>() {

            public Object[] apply(@Nullable final ServiceRequestTestData requestTestData) {
                if (requestTestData == null) {
                    return null;
                }

                return new Object[]{
                        requestTestData.getRequestName(),
                        requestTestData.getRequestBody(),
                        requestTestData.getExpectedServiceResponse()
                };
            }

        });
    }

    @Test
    public void allRequestTestDataProducesExpectedResults() throws Exception {
        if (expectedResponse != null) {

            verifyServiceResponse(
                    requestName,
                    requestBody,
                    expectedResponse
            );

        }
    }

    private void verifyServiceResponse(final String name, final JsonNode requestEntity,
                                       final T expectedResponse) throws
            JsonProcessingException {
        final T serviceResponse = (T) assertThat(postToService(requestEntity))
                .as(name)
                .hasSuccessfulStatus()
                .getEntity(expectedResponse.getClass());

        assertThat(serviceResponse).isEqualToComparingFieldByField(expectedResponse);


    }

    private Response postToService(final Object requestEntity) {
        return client.target(getServiceUrl())
                     .request(MediaType.APPLICATION_JSON_TYPE)
                     .post(Entity.json(requestEntity), Response.class);
    }


}
