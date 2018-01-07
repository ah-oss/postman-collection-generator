package assertions;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.Table;
import com.google.common.io.ByteSource;
import org.assertj.core.api.Assertions;
import org.assertj.guava.api.*;

import javax.annotation.Nullable;
import javax.ws.rs.core.Response;

public class ITAssertions extends Assertions {

    public static ClientResponseAssert assertThat(final Response clientResponse) {
        return ClientResponseAssert.assertThat(clientResponse);
    }

    public static JsonNodeAssert assertThat(@Nullable final JsonNode node) {
        return JsonNodeAssert.assertThat(node);
    }

    public static ByteSourceAssert assertThat(final ByteSource actual) {
        return org.assertj.guava.api.Assertions.assertThat(actual);
    }

    public static <K, V> MultimapAssert<K, V> assertThat(final Multimap<K, V> actual) {
        return org.assertj.guava.api.Assertions.assertThat(actual);
    }

    public static <T> OptionalAssert<T> assertThat(final Optional<T> actual) {
        return org.assertj.guava.api.Assertions.assertThat(actual);
    }

    public static <T extends Comparable<T>> RangeAssert<T> assertThat(final Range<T> actual) {
        return org.assertj.guava.api.Assertions.assertThat(actual);
    }

    public static <K extends Comparable<K>, V> RangeMapAssert<K, V> assertThat(final RangeMap<K, V> actual) {
        return org.assertj.guava.api.Assertions.assertThat(actual);
    }

    public static <R, C, V> TableAssert<R, C, V> assertThat(final Table<R, C, V> actual) {
        return org.assertj.guava.api.Assertions.assertThat(actual);
    }
}