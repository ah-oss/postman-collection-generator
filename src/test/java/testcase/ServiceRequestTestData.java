package testcase;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ServiceRequestTestData<T> {

    private final String requestName;
    private final ObjectNode requestBody;
    private final T expectedServiceResponse;

    public ServiceRequestTestData(final String requestName,
                                  final ObjectNode requestBody,
                                  final T expectedServiceResponse) {
        this.requestName = requestName;
        this.requestBody = requestBody;
        this.expectedServiceResponse = expectedServiceResponse;
    }

    public ObjectNode getRequestBody() {
        return requestBody;
    }

    public T getExpectedServiceResponse() {
        return expectedServiceResponse;
    }

    public String getRequestName() {
        return requestName;
    }
}
