package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class DataImportUtil {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();

    static {
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static JsonNode loadJsonResource(final String name) {
        final Class<JsonNode> valueType = JsonNode.class;
        try {
            return loadJsonResourceAs(name, valueType);
        } catch (final Exception exception) {
            System.err.println("Failed to load " + name);
            throw new RuntimeException("Failed to load resource file: " + name );
        }
    }

    public static <T> T loadJsonResourceAs(final String name, final Class<T> valueType) throws IOException {
        return OBJECT_MAPPER.readValue(Object.class.getResourceAsStream(name), valueType);
    }

    public static ObjectNode loadJsonFile(final String path) throws IOException {
        return (ObjectNode) OBJECT_MAPPER.readTree(new File(path));
    }
}
