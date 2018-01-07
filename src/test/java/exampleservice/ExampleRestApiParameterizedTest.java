package exampleservice;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.runners.Parameterized;
import testcase.BaseRestApiParameterizedTest;

import java.util.Collection;

public class ExampleRestApiParameterizedTest extends BaseRestApiParameterizedTest<ExampleServiceResponse> {

    public ExampleRestApiParameterizedTest(String requestName, ObjectNode requestBody, ExampleServiceResponse response) {
        super(requestName, requestBody, response);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return buildData("/exampleservice/exampleServiceTestCases.json");
    }

    protected String getServiceUrl() {
        return "http://jsonplaceholder.typicode.com/posts";
    }

}
