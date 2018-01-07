package util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Ignore;
import org.junit.Test;
import testcase.RequestTestCase;
import testcase.ServiceTestCase;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataConvertUtil {

    @Test
    @Ignore
    public void convertAll() throws Exception {
        final ServiceTestCase serviceTestCase = new ServiceTestCase("/exampleServiceTestCases.json");
        convertList(serviceTestCase.retrieveTestCases());
    }

    @Test
    @Ignore
    public void convertLastElements() throws Exception {
        final ServiceTestCase serviceTestCase = new ServiceTestCase("/exampleServiceTestCases.json");
        convertList(sublist(1, serviceTestCase.retrieveTestCases()));
    }

    private void convertList(final List<RequestTestCase> updatedList) throws IOException {
        for (final RequestTestCase testCase : updatedList) {
            updateTestCase(testCase);
        }
    }

    private List<RequestTestCase> sublist(final int numberOfElements,
                                          final List<RequestTestCase> fullList) {
        return fullList.subList(fullList.size() - numberOfElements, fullList.size());
    }

    private void updateTestCase(final RequestTestCase testCase) throws IOException {
        final String request = testCase.requestFileName;
        final ObjectNode requestAsNode = (ObjectNode) DataImportUtil.loadJsonResource("/request/" + request);
        requestAsNode.put("title", "foo");

        DataImportUtil.OBJECT_WRITER.writeValue(new File("src/test/resources/request/" + request), requestAsNode);
    }


}
