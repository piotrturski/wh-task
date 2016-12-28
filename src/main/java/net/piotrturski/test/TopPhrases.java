package net.piotrturski.test;

import one.util.streamex.StreamEx;
import org.apache.spark.api.java.JavaRDD;
import org.neo4j.graphalgo.impl.util.FibonacciHeap;
import scala.Tuple2;

import java.io.Reader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TopPhrases {

    private static final Pattern SEPARATOR = Pattern.compile("\\|");
    private static final Comparator<Map.Entry<String, Long>> MAX_VALUE_FIRST_COMPARATOR =
                                                                Map.Entry.<String, Long>comparingByValue().reversed();

    /**
     * reads input line by line, reduces / counts on the fly, takes top k using fibonacci heap
     *
     * assumptions: <br>
     *     - collection of unique labels fits memory <br>
     *     - each file line fits memory
     *     - count of each label fits long value <br>
     *     - each label fits java max string size <br>
     * <br>
     *
     * computational complexity: expected
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
                .flatMap(line -> StreamEx.split(line, '|'))
                .collect(Collectors.groupingBy(Function.identity(), HashMap::new, Collectors.counting()))
                .entrySet()
                .forEach(heap::insert);

        return StreamEx.generate(heap::extractMin).limit(heap.size())
                .map(Map.Entry::getKey);
    }

    /**
     * uses spark to
     *
     * assumptions:
     *    - each line fits memory <br>
     *    - each label fits java max string size <br>
     *    - count of each label fits long value <br>
     *
     * <br>
     * it's hard to assess complexity due to lack of spark documentation on complexity. dominant operations are
     * one reduceByKey and one sortByKey, both of which require shuffle. I **guess**
     * that overall complexity is O(n log n) disk operations + O(n) network operations
     *
     * @param file any spark RDD (in-memory collection or hadoop-supported location like hdfs, s3, hbase etc)
     * @param top how many top phrases should be returned
     * @return spark rdd. it can be
     */
    public static JavaRDD<String> sparkTopPhrases(JavaRDD<String> file, long top) {

        return file.flatMap(line -> Arrays.asList(SEPARATOR.split(line)).iterator())
                .mapToPair(label -> new Tuple2<>(label, 1L))
                .reduceByKey(Long::sum) // complexity?
                .mapToPair(Tuple2::swap)
                .sortByKey(false) // complexity?
                .values()
                .zipWithIndex()
                .filter(tuple -> tuple._2() < top) // 0 based index
                .keys();
    }
}
