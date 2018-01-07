package exampleservice;

import postman.PostmanSyncTest;

public class ExampleServicePostmanSyncTest extends PostmanSyncTest {
    public String postmanCollectionFilePath() {
        return "postman/exampleservice.postman_collection.json";
    }

    public String serviceTestCasesFilePath() {
        return "/exampleservice/exampleServiceTestCases.json";
    }
}
