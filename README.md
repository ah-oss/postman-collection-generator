# postman-collection-generator
This is a framework/process for quickly creating integration test suite for a rest api along with generation of a postman collection.
The framework currently supports POST operation with a request only.
## Usage
### Step 1
Create a ```<serviceName>.postman_collection.json``` file by copying the exampleservice.postman_collection.json.
Make sure to change the "name" to Service Name
### Step 2
Create a ```<serviceName>TestCases.json``` file in your resource folder:
```$xslt
{
  "Service Test cases": [
    {
      "name": "Test case name 1",
      "requestFileName": "/exampleservice/request/testCase1.json",
      "expectedFileName": "/exampleservice/request/testCase1-expectedResponse.json"
    }
  ],
  "QA Test Cases": [
    {
      "name": "QA Test Case 1",
      "requestFileName": "/exampleservice/request/testCase2.json",
      "expectedFileName": "/exampleservice/request/testCase2-expectedResponse.json"
    }
  ]
}
```
Each node of the test cases json file will be converted into a collection folder for your rest service.
The node is to better manage the categories of your integration test cases for e.g QA test cases, Demo etc.,
The attributes of each node are used as follows:
* "name" - the name of the test case, is used in parameterized test as well as the request name in the postman collection
* "requestFileName" - path to the request json. This json file is used to submit a post request.
* "expectedFileName" - path to the expected json response. This json file is used to assert the response.


### Step 3
Create the Api integration parametrized test by extending the BaseRestApiParameterizedTest class.
You need to implement the following methods:
```
      @Parameterized.Parameters(name = "{0}")
      public static Collection<Object[]> data() {
          return buildData("/exampleservice/exampleServiceTestCases.json");
      }
  
      protected String getServiceUrl() {
          return "http://jsonplaceholder.typicode.com/posts";
      }

```
@see ExampleRestApiParmeterizedTest.java as an example

The buildData method accepts the path to the <testCases.json> file created in Step 1

### Step 4
Create the postman sync test by extending PostmanSyncTest class.
 You need to implement the following methods:
 ```$xslt
    public String postmanCollectionFilePath() {
        return "postman/exampleservice.postman_collection.json";
    }

    public String serviceTestCasesFilePath() {
        return "/exampleservice/exampleServiceTestCases.json";
    }
```
@see ExampleServicePostmanSyncTest as an example

### Step 5
Run tests

## Process
The framework allows you to create your rest service integration test along with the corresponding 
 postman collection file that can be imported in the postman tool.
 
 Any changes to the request, test cases etc., are forced to be kept in sync by <ServiceName>PostmanSyncTest failures.
 
 In case the ```<serviceName>PostmanSyncTest``` fails, then you will need to run PostmanSync java application to regenerate the 
 ```<serviceName>.postman_collection.json``` automatically. 
 The PostmanSync application accepts two parameters:
 * arg1 - The path to postman collection file (for e.g: postman/exampleservice.postman_collection.json)
 * arg2 - The path to test cases json file (for e.g: /exampleservice/exampleServiceTestCases.json)
