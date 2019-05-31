package testcase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import exampleservice.ExampleServiceResponse;
import util.DataImportUtil;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServiceTestCase {

    private final String serviceTestCasesPath;

    public ServiceTestCase(String serviceTestCasesPath) {
        this.serviceTestCasesPath = serviceTestCasesPath;
    }

    public Map<String, List<RequestTestCase>> retrieveServiceTestCasesData() {
        Map<String, List<RequestTestCase>> value = getValueFromCache();
        if (value == null) {
            value = loadServiceTestCases();
            ServiceTestCaseCache.SERVICE_TEST_CASES_DATA_CACHE.put(this.serviceTestCasesPath, value);
        }
        return value;
    }

    public List<RequestTestCase> retrieveTestCases() {
        return Lists.newArrayList(Iterables.concat(retrieveServiceTestCasesData().values()));
    }

    public Map<String, List<ServiceRequestTestData>> retrieveRequestTestDataAsMap() {
        return loadFileMap();
    }

    public List<ServiceRequestTestData> retrieveServiceTestDataAsList() {
        return Lists.newArrayList(Iterables.concat(retrieveRequestTestDataAsMap().values()));
    }

    public static Map<String, ObjectNode> toMap(final List<ServiceRequestTestData> requestBodyJsonMap) {
        final Map<String, ObjectNode> map = new LinkedHashMap<String, ObjectNode>();

        for (final ServiceRequestTestData requestTestData : requestBodyJsonMap) {
            map.put(requestTestData.getRequestName(), requestTestData.getRequestBody());
        }
        return map;
    }

    private Map<String, List<ServiceRequestTestData>> loadFileMap() throws RuntimeException {
        return Maps.transformValues(retrieveServiceTestCasesData(), new Function<List<RequestTestCase>, List<ServiceRequestTestData>>() {

            @Nullable
            public List<ServiceRequestTestData> apply(@Nullable final List<RequestTestCase> requestTestCases) {
                if (requestTestCases == null) {
                    return null;
                }

                return Lists.transform(requestTestCases, new Function<RequestTestCase, ServiceRequestTestData>() {
                    @Nullable
                    public ServiceRequestTestData apply(@Nullable final RequestTestCase data) {
                        if (data == null) {
                            return null;
                        }

                        try {
                            final String requestName = data.name;
                            final String path = data.requestFileName;
                            final String expectationPath = data.expectedFileName;
                            final ObjectNode requestBody = loadJsonResource(path);

                            final ExampleServiceResponse expectedResponse = loadResponse(expectationPath);

                            return new ServiceRequestTestData(requestName, requestBody, expectedResponse);
                        } catch (final IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    }
                });
            }
        });
    }

    private static ExampleServiceResponse loadResponse(final String expectationPath) throws IOException {
        if (expectationPath == null) {
            return null;
        }

        return DataImportUtil.loadJsonResourceAs(expectationPath, ExampleServiceResponse.class);
    }

    private static ObjectNode loadJsonResource(final String path) {
        return (ObjectNode) DataImportUtil.loadJsonResource(path);
    }

    private Map<String, List<RequestTestCase>> loadServiceTestCases() throws RuntimeException {
        try {
            final InputStream resourceAsStream = Object.class.getResourceAsStream(this.serviceTestCasesPath);
            return DataImportUtil.OBJECT_MAPPER.readValue(resourceAsStream,
                    new TypeReference<Map<String, List<RequestTestCase>>>() {
                    });
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Map<String, List<RequestTestCase>> getValueFromCache() {
        Map<String, List<RequestTestCase>> value = null;
        try {
            value = ServiceTestCaseCache.SERVICE_TEST_CASES_DATA_CACHE.getUnchecked(this.serviceTestCasesPath);
        } catch (Exception e) {
        }
        return value;
    }
}
