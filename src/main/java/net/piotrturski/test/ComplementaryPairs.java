package net.piotrturski.test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;
import java.util.Set;

public abstract class ComplementaryPairs {

    /**
     * doesn't return mirrored pairs: instead of (i,j) and (j,i), returns only (i,j) <br>
     *
     * computational complexity: expected O(n + number of complementary pairs). 'expected' because of hashmap / hashset <br>
     * if we were to return only number of pairs, it would be O(n) <br>
     * space complexity: O(n + number of complementary pairs) <br>
     * <br>
     * number of complementary pairs can be up to O(n^2)
     */
    public static Set<Pair<Integer, Integer>> complementaryPairs(int[] numbers, int k) {

        Objects.requireNonNull(numbers, "numbers cannot be null");

        Set<Pair<Integer, Integer>> result = Sets.newHashSet();
        Multimap<Integer, Integer> valueToIndices = ArrayListMultimap.create();
        long K = k; // to avoid int overflow

        for (int i = 0; i < numbers.length; i++) {

            valueToIndices.put(numbers[i], i);

            long missingNumber = K - numbers[i];
            if (missingNumber != (int) missingNumber) continue; // missing number is not an int so it's not in the map

            for (int complementaryIndex : valueToIndices.get((int)missingNumber)) {
                result.add(Pair.of(complementaryIndex, i));
//                result.add(Pair.of(i, complementaryIndex)); // uncomment to return also mirrored pairs
            }

        }
        return result;

    }
}
