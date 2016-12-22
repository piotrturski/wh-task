package net.piotrturski.test;


import com.googlecode.zohhak.api.Coercion;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.MIN_VALUE;
import static org.apache.commons.lang3.ArrayUtils.EMPTY_STRING_ARRAY;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ZohhakRunner.class)
public class ComplementaryPairsTest {


    @TestWith({
            "1 2 3 4 5 6,     8,      1 5; 2 4; 3 3",
            "1 1,             2,      0 1 ; 0 0; 1 1",
            "1 4 5,           3,      ",
            "-1 -2 1,         -3,     0 1",
            "           ,     0,      ",
    })
    public void should_find_complimentary_pairs(int[] numbers, int k, Set<Pair<Integer,Integer>> expectedResult) {

        Set<Pair<Integer, Integer>> actualPairs = ComplementaryPairs.complementaryPairs(numbers, k);

        assertThat(actualPairs).containsOnlyElementsOf(expectedResult);
    }

    @Test
    public void should_handle_int_overflows() {

        int overflow = MIN_VALUE + MIN_VALUE;

        Set<?> pairs = ComplementaryPairs.complementaryPairs(new int[]{MIN_VALUE, MIN_VALUE}, overflow);

        assertThat(pairs).isEmpty();
    }

    // " 1 2; 3 4; 5 6" -> {<1,2>, <3,4>, <5,6>}"
    @Coercion
    public Set<Pair<Integer,Integer>> toList(String input) {
        return Arrays.stream(StringUtils.split(input, ";"))
                .map(this::toIntArray)
                .map(twoInts -> Pair.of(twoInts[0], twoInts[1]))
                .collect(Collectors.toSet());
    }

    // "1 2 -3" -> [1,2,-3]
    @Coercion
    public int[] toIntArray(String input) {
        return Arrays.stream(StringUtils.split(input)).mapToInt(Integer::parseInt).toArray();
    }

}
