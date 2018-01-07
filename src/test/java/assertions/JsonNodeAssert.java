package assertions;

import com.fasterxml.jackson.databind.JsonNode;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.IterableAssert;

import javax.annotation.Nullable;
import java.math.BigDecimal;

public class JsonNodeAssert extends IterableAssert<JsonNode> {

    @Nullable
    private final JsonNode actual;

    private JsonNodeAssert(@Nullable final JsonNode actual) {
        super(actual);
        this.actual = actual;
    }

    public static JsonNodeAssert assertThat(@Nullable final JsonNode node) {
        return new JsonNodeAssert(node);
    }

    public void matchesDecimal(final BigDecimal decimal) {
        super.isNotNull();
        Assertions.assertThat(this.actual.decimalValue()).isEqualTo(decimal);
    }

    public void matchesText(final String text) {
        super.isNotNull();
        Assertions.assertThat(this.actual.textValue()).isEqualTo(text);
    }

    @Override
    public void isNull() {
        if (this.actual != null) {
            Assertions.assertThat(this.actual.isNull()).isEqualTo(true);
        }
    }

    @Override
    public JsonNodeAssert isNotNull() {
        super.isNotNull();
        Assertions.assertThat(this.actual.isNull()).isEqualTo(false);
        return this;
    }

    public JsonNodeAssert doesNotHave(final String propertyName) {
        super.isNotNull();
        Assertions.assertThat(this.actual.has(propertyName)).isFalse();
        return this;
    }
}
