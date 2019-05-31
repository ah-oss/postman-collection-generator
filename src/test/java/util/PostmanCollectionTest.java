package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static assertions.ITAssertions.assertThat;


class PostmanCollectionTest {

    private static final JsonNodeFactory NODE_FACTORY = JsonNodeFactory.instance;
    private final ObjectWriter prettyObjectWriter = DataImportUtil.OBJECT_MAPPER.writerWithDefaultPrettyPrinter();

    @Test
    void willAddItemToExistingPostmanCollection() throws Exception {

        final ObjectNode postmanCollectionJson = NODE_FACTORY.objectNode();
        postmanCollectionJson
                .putArray("item")
                .addObject()
                .put("name", "TestPostmanCollection")
                .putArray("item");

        final PostmanCollection postmanCollection = new PostmanCollection(postmanCollectionJson, "http://service.url");

        final ObjectNode requestJson = buildSimpleObjectNode("Hello 1");

        postmanCollection.addItems("RestService", ImmutableMap.of("Example", requestJson));

        JsonNode updatedPostmanCollectionJson = postmanCollection.getJson();

        assertThat(updatedPostmanCollectionJson).isNotNull();

        final JsonNode itemArray = updatedPostmanCollectionJson.get("item").get(0).get("item");
        assertThat(itemArray).hasSize(1);

        final JsonNode item = itemArray.get(0);
        Assertions.assertThat(item.get("name").asText()).isEqualTo("Example");

        final JsonNode bodyNode = item.get("request").get("body");
        checkBodyNode(requestJson, bodyNode);
    }

    @Test
    void willAddMultipleItemsToExistingPostmanCollection() throws Exception {
        final ObjectNode postmanCollectionJson = NODE_FACTORY.objectNode();
        postmanCollectionJson
                .putArray("item")
                .addObject()
                .put("name", "TestPostmanCollection")
                .putArray("item");

        final PostmanCollection postmanCollection = new PostmanCollection(postmanCollectionJson, "http://service.url");

        final ObjectNode requestJson1 = buildSimpleObjectNode("Hello 1");
        final ObjectNode requestJson2 = buildSimpleObjectNode("Hello 2");
        final ObjectNode requestJson3 = buildSimpleObjectNode("Hello 3");

        postmanCollection.addItems("RestService",
                                   ImmutableMap.of("1", requestJson1, "2", requestJson2, "3", requestJson3)
        );

        JsonNode updatedPostmanCollectionJson = postmanCollection.getJson();

        assertThat(updatedPostmanCollectionJson).isNotNull();

        final JsonNode itemArray = updatedPostmanCollectionJson.get("item").get(0).get("item");
        assertThat(itemArray).hasSize(3);

        checkItemNode(requestJson1, "1", itemArray.get(0));
        checkItemNode(requestJson2, "2", itemArray.get(1));
        checkItemNode(requestJson3, "3", itemArray.get(2));
    }

    @Test
    void willRemoveExistingItemsFromCollection() {
        final ObjectNode postmanCollectionJson = NODE_FACTORY.objectNode();
        postmanCollectionJson
                .putArray("item")
                .addObject()
                .put("name", "TestPostmanCollection")
                .putArray("item")
                .addObject()
                .put("name", "pre-existing-item");


        final PostmanCollection postmanCollection = new PostmanCollection(postmanCollectionJson, "http://service.url");

        JsonNode updatedPostmanCollectionJson = postmanCollection.getJson();

        assertThat(updatedPostmanCollectionJson).isNotNull();

        final JsonNode itemArray = updatedPostmanCollectionJson.get("item");
        assertThat(itemArray).isEmpty();
    }

    @Test
    void willUpdateUrlInPostmanCollectionItemRequest() throws Exception {
        final ObjectNode postmanCollectionJson = NODE_FACTORY.objectNode();
        postmanCollectionJson
                .putArray("item")
                .addObject()
                .put("name", "TestPostmanCollection")
                .putArray("item");

        final String serviceEndpointUrl = null;
        final PostmanCollection postmanCollection = new PostmanCollection(postmanCollectionJson, serviceEndpointUrl);

        final ObjectNode requestJson = buildSimpleObjectNode("Hello 1");

        postmanCollection.addItems("RestService", ImmutableMap.of("Example", requestJson));

        JsonNode updatedPostmanCollectionJson = postmanCollection.getJson();

        assertThat(updatedPostmanCollectionJson).isNotNull();

        final JsonNode itemArray = updatedPostmanCollectionJson.get("item").get(0).get("item");
        assertThat(itemArray).hasSize(1);

        final JsonNode item = itemArray.get(0);
        Assertions.assertThat(item.get("request").get("url").asText()).isEqualTo("http://jsonplaceholder.typicode.com/posts");
    }

    @Test
    void willDefaultUrlToTemplateUrlInPostmanCollectionItemRequest_whenServiceEndpointUrlIsNull() throws Exception {
        final ObjectNode postmanCollectionJson = NODE_FACTORY.objectNode();
        postmanCollectionJson
                .putArray("item")
                .addObject()
                .put("name", "TestPostmanCollection")
                .putArray("item");

        final String serviceEndpointUrl = "http://service.url";
        final PostmanCollection postmanCollection = new PostmanCollection(postmanCollectionJson, serviceEndpointUrl);

        final ObjectNode requestJson = buildSimpleObjectNode("Hello 1");

        postmanCollection.addItems("RestService", ImmutableMap.of("Example", requestJson));

        JsonNode updatedPostmanCollectionJson = postmanCollection.getJson();

        assertThat(updatedPostmanCollectionJson).isNotNull();

        final JsonNode itemArray = updatedPostmanCollectionJson.get("item").get(0).get("item");
        assertThat(itemArray).hasSize(1);

        final JsonNode item = itemArray.get(0);
        Assertions.assertThat(item.get("request").get("url").asText()).isEqualTo(serviceEndpointUrl);
    }

    private void checkItemNode(final ObjectNode requestJson1, final String expected, final JsonNode item) throws
            JsonProcessingException {
        Assertions.assertThat(item.get("name").asText()).isEqualTo(expected);
        checkBodyNode(requestJson1, item.get("request").get("body"));
    }

    private void checkBodyNode(final ObjectNode requestJson1, final JsonNode bodyNode) throws JsonProcessingException {
        Assertions.assertThat(bodyNode.get("mode").asText()).isEqualTo("raw");

        Assertions.assertThat(bodyNode.get("raw").asText())
                .isEqualTo(prettyObjectWriter.writeValueAsString(requestJson1));
    }

    private ObjectNode buildSimpleObjectNode(final String value) {
        final ObjectNode requestJson = NODE_FACTORY.objectNode();
        requestJson.put("description", value);
        return requestJson;
    }
}