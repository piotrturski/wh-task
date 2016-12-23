package net.piotrturski.test;

import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphalgo.impl.util.FibonacciHeap;

import java.io.Reader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TopPhrases {

    private static final Comparator<Map.Entry<String, Long>> MAX_VALUE_FIRST_COMPARATOR =
                                                                Map.Entry.<String, Long>comparingByValue().reversed();

    /**
     * assumptions: <br>
     *     - collection of unique labels fits memory <br>
     *     - labels must not span multiple lines <br>
     *     - edge whitespaces are not part of labels <br>
     *     - labels are not empty <br>
     * <br>
     *
     * computational complexity: amortized expected
     *              O(input-stream + number-of-requested-phrases log number-of-different-phrases) <br>
     * space complexity: O(input-stream)
     *
     * @return lazy stream of top phrases. take as many elements as you need.
     *         each element is generated in amortized time O(log number-of-different-phrases)
     */
    public static Stream<String> topPhrases(Reader reader) {

        Objects.requireNonNull(reader, "reader cannot be null");

        FibonacciHeap<Map.Entry<String, Long>> heap = new FibonacciHeap<>(MAX_VALUE_FIRST_COMPARATOR);

        StreamEx.ofLines(reader)
                .flatMap(line -> StreamEx.split(line, '|'))//  Stream.of(StringUtils.split(line, "|")))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.groupingBy(Function.identity(), HashMap::new, Collectors.counting()))
                .entrySet()
                .forEach(heap::insert);

        return StreamEx.generate(heap::extractMin).limit(heap.size())
                .map(Map.Entry::getKey);
    }

}
