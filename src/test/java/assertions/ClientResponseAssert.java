package assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import javax.ws.rs.core.Response;
import java.io.IOException;

public class ClientResponseAssert extends AbstractAssert<ClientResponseAssert, Response> {

    private ClientResponseAssert(final Response actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    public static ClientResponseAssert assertThat(final Response clientResponse) {
        return new ClientResponseAssert(clientResponse, ClientResponseAssert.class);
    }

    public ClientResponseAssert hasStatus(final int expectedStatus) throws JsonProcessingException {
        isNotNull();
        if (actual.getStatus() != expectedStatus) {
            writeFailureMessage("status",
                                Integer.toString(expectedStatus),
                                Integer.toString(actual.getStatus()),
                                actual.readEntity(String.class));
        }

        return this;
    }

    public ClientResponseAssert hasSuccessfulStatus() throws JsonProcessingException {
        isNotNull();
        if (actual.getStatus() < 200 || actual.getStatus() >= 300) {
            writeFailureMessage("status",
                                "Successful",
                                Integer.toString(actual.getStatus()),
                                actual.readEntity(String.class));
        }

        return this;
    }

    public ClientResponseAssert hasUnsuccessfulStatus() throws JsonProcessingException {
        isNotNull();
        if (actual.getStatus() >= 200 && actual.getStatus() < 300) {
            writeFailureMessage("status",
                                "unsuccessful",
                                Integer.toString(actual.getStatus()),
                                actual.readEntity(String.class));
        }

        return this;
    }

    public ClientResponseAssert doesNotHaveStatus(final int unexpectedStatus) throws JsonProcessingException {
        isNotNull();

        if (actual.getStatus() == unexpectedStatus) {
            final String expectedValue = Integer.toString(unexpectedStatus);
            final String actualValue = Integer.toString(actual.getStatus());
            final String entity = actual.readEntity(String.class);
            final ObjectMapper objectMapper = new ObjectMapper();

            try {
                final ObjectNode jsonEntity = objectMapper.readValue(entity, ObjectNode.class);
                final ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
                failWithMessage("Expected <%s> to not to be <%s> but was <%s>. \nResponse was: \n<%s>",
                                "status",
                                expectedValue,
                                actualValue,
                                objectWriter.writeValueAsString(jsonEntity));
            } catch (final IOException e) {
                failWithMessage("Expected <%s> to not to be <%s> but was <%s>. \nResponse was: \n<%s>",
                                "status",
                                expectedValue,
                                actualValue,
                                entity);
            }
        }

        return this;
    }

    public ClientResponseAssert hasMessage(final String expectedMessage) throws JsonProcessingException {
        isNotNull();
        final ObjectNode entity = actual.readEntity(ObjectNode.class);
        final String actualMessage = entity.get("message").asText();
        if (!actualMessage.equals(expectedMessage)) {
            writeFailureMessage("message", expectedMessage, actualMessage, entity.toString());
        }
        return this;
    }

    private void writeFailureMessage(final String attribute,
                                     final String expectedValue,
                                     final String actualValue,
                                     final String entity) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            final ObjectNode jsonEntity = objectMapper.readValue(entity, ObjectNode.class);
            final ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            failWithMessage("Expected <%s> to be <%s> but was <%s>. \nResponse was: \n<%s>",
                            attribute,
                            expectedValue,
                            actualValue,
                            objectWriter.writeValueAsString(jsonEntity));
        } catch (final IOException e) {
            failWithMessage("Expected <%s> to be <%s> but was <%s>. \nResponse was: \n<%s>",
                            attribute,
                            expectedValue,
                            actualValue,
                            entity);
        }

    }

    public ClientResponseAssert hasEntity(final JsonNode entity) {
        isNotNull();
        final JsonNode actualEntity = actual.readEntity(JsonNode.class);
        Assertions.assertThat(actualEntity).isEqualTo(entity);
        return this;
    }

    public ClientResponseAssert hasEntity(final byte[] bytes) {
        final byte[] entity = actual.readEntity(byte[].class);
        Assertions.assertThat(entity).isEqualTo(bytes);
        return this;
    }

    public <T> ClientResponseAssert hasEntity(final T expectedEntity) {
        final Object actualEntity = getEntity(expectedEntity.getClass());
        Assertions.assertThat(actualEntity).isEqualToComparingFieldByFieldRecursively(expectedEntity);
        return this;
    }

    public ClientResponseAssert hasType(final String type) {
        Assertions.assertThat(actual.getStatusInfo().toString()).isEqualTo(type);
        return this;
    }

    public <T> T getEntity(final Class<T> objectNodeClass) {
        return actual.readEntity(objectNodeClass);
    }

}
