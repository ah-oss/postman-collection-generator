package postman;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import testcase.ServiceRequestTestData;
import testcase.ServiceTestCase;
import util.DataImportUtil;
import util.PostmanCollection;

import java.io.File;
import java.util.List;
import java.util.Map;

public class PostmanSync {

    public static void main(final String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Invalid arguments passed. The program required 2 arguments. arg 1 - postman collection file path, arg 2 - service test cases json file path");
        }
        final String postmanCollectionFilePath = args[0];
        final PostmanCollection postmanCollection = new PostmanCollection(
                DataImportUtil.loadJsonFile(postmanCollectionFilePath), "http://jsonplaceholder.typicode.com/posts");

        String serviceTestCasesPath = "/exampleservice/exampleServiceTestCases.json";
        if (args.length > 0 && !Strings.isNullOrEmpty(args[1])) {
            serviceTestCasesPath = args[1];
        }
        final ServiceTestCase serviceTestCase = new ServiceTestCase(serviceTestCasesPath);
        for (final Map.Entry<String, List<ServiceRequestTestData>> entry : serviceTestCase.retrieveRequestTestDataAsMap().entrySet()) {
            final String directoryName = entry.getKey();
            final Map<String, ObjectNode> directoryTests = ServiceTestCase.toMap(entry.getValue());
            postmanCollection.addItems(directoryName, directoryTests);
        }

        DataImportUtil.OBJECT_WRITER.writeValue(new File(postmanCollectionFilePath),
                postmanCollection.getJson());

        System.out.println("SUCCESS");
    }

}
