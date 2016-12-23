package net.piotrturski.test;

import com.googlecode.zohhak.api.Coercion;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Reader;
import java.io.StringReader;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ZohhakRunner.class)
public class TopPhrasesTest {

    @TestWith({
            "a b c | d | d | c | a b c  | d,                          d ; a b c ; c ",
            "\n a b c \n | \n d \n | d | \n | ||c|\na b c\n \n| d,    d ; a b c ; c ",
            "  , ",
            " | | | | , ",
            " \n \n , ",
            " \n | \n , ",
            " a , a "
    })
    public void should_find_top_phrases(Reader reader, String[] topLabels) {
        Stream<String> topPhrases = TopPhrases.topPhrases(reader);

        assertThat(topPhrases).containsExactly(topLabels);
    }

    @Test
    public void should_maintain_partial_order_even_if_there_is_no_enforced_total_order() {

        String content = StreamEx.of(1, 22, 22, 33, 33, 444, 444, 444, 555, 555, 555)
                .map(String::valueOf)
                .sortedBy(__ -> Math.random())
                .joining("|");

        Stream<String> topPhrases = TopPhrases.topPhrases(new StringReader(content));

        assertThat(topPhrases)
                .hasSize(5)
                .doesNotHaveDuplicates()
                .isSortedAccordingTo(Comparator.comparing(String::length).reversed());
    }

    @Coercion
    public Reader toReader(String input) {
        return new StringReader(input);
    }

    @Coercion
    public String[] toLabels(String input) {
        return StreamEx.split(input, ';').remove(StringUtils::isBlank).map(String::trim).toArray(String[]::new);
    }
}