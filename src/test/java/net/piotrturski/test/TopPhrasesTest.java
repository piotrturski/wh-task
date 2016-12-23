package net.piotrturski.test;

import com.google.common.collect.Lists;
import com.googlecode.zohhak.api.Coercion;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import one.util.streamex.StreamEx;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Reader;
import java.io.StringReader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ZohhakRunner.class)
public class TopPhrasesTest {

    @TestWith({
            "a b c|d|d|c|a b c|d,     d;a b c;c ",
            "a b c|d\nd|c\na b c\nd,  d;a b c;c ",
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


    @Test
    public void should_find_top_phrases_on_spark() {

        try (JavaSparkContext spark = new JavaSparkContext(new SparkConf().setMaster("local[3]").setAppName("test"))) {

            JavaRDD<String> rdd = spark.parallelize(Lists.newArrayList(
                    "a|a|a",
                    "a|b|b|c|d|c|d|c",
                    "b|b|b",
                    "c|c|c",
                    "d",
                    "d",
                    "d"
                    ));

            List<String> top3 = TopPhrases.sparkTopPhrases(rdd, 3)
                    .collect();

            assertThat(top3)
                    .startsWith("c")
                    .containsOnly("c", "b", "d");
        }
    }

    @Coercion
    public Reader toReader(String input) {
        return new StringReader(input);
    }

    @Coercion
    public String[] toLabels(String input) {
        return input.split(";");
    }
}