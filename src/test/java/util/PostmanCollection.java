package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

import java.io.IOException;
import java.util.Map;

public class PostmanCollection {
    private static final String POSTMAN_ITEM_TEMPLATE_PATH = "/postmanItemTemplate.json";
    private static final JsonNode POSTMAN_ITEM_TEMPLATE = loadPostmanItemTemplate();

    private static JsonNode loadPostmanItemTemplate() {
        try {
            return DataImportUtil.loadJsonResource(POSTMAN_ITEM_TEMPLATE_PATH);
        } catch (final IOException criticalError) {
            throw new RuntimeException(criticalError);
        }
    }

    private final ObjectNode postmanCollectionJson;
    private final String serviceEndpointUrl;

    public PostmanCollection(final ObjectNode postmanCollectionJson, String serviceEndpointUrl) {
        this.postmanCollectionJson = postmanCollectionJson.deepCopy();
        this.serviceEndpointUrl = serviceEndpointUrl;

        getDirectories().removeAll();
    }

    private ArrayNode getDirectories() {
        return this.postmanCollectionJson.withArray("item");
    }

    public void addItems(final String directoryName,
                         final Map<String, ObjectNode> requestBodyJsonArray) throws JsonProcessingException {

        final ObjectNode directory = getDirectories().addObject()
                                          .put("name", directoryName)
                                          .put("description", "");

        final ArrayNode itemArray = getRequestArray(directory);

        for (final Map.Entry<String, ObjectNode> entry : requestBodyJsonArray.entrySet()) {
            final ObjectNode requestBody = entry.getValue();
            final String name = entry.getKey();
            final JsonNode newItem = createNewItem(requestBody, name);
            itemArray.add(newItem);
        }
    }

    private ArrayNode getRequestArray(final ObjectNode directory) {
        return directory.withArray("item");
    }

    private JsonNode createNewItem(final ObjectNode requestBodyJson, final String name) throws JsonProcessingException {
        final ObjectNode newItem = POSTMAN_ITEM_TEMPLATE.deepCopy();
        newItem.put("name", name);
        final ObjectNode request = (ObjectNode)newItem.get("request");
        if (!Strings.isNullOrEmpty(serviceEndpointUrl)) {
            request.put("url", serviceEndpointUrl);
        }
        final ObjectNode bodyNode = (ObjectNode) request.get("body");
        final String requestAsPrettyJson = DataImportUtil.OBJECT_WRITER.writeValueAsString(requestBodyJson);
        bodyNode.put("raw", requestAsPrettyJson);
        return newItem;
    }

    public JsonNode getJson() {
        return postmanCollectionJson;
    }
}
