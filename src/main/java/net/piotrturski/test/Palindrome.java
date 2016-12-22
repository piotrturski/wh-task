package net.piotrturski.test;

import java.util.Objects;

public abstract class Palindrome {

    /**
     * Checks if charSequence is a palindrome <br>
     * computational complexity: O(n) (provided that chatAt is O(1) - that's the case for strings) <br>
     * memory complexity: O(1) not counting the input
     */
    public static boolean isPalindrome(CharSequence charSequence) {

        Objects.requireNonNull(charSequence, "char sequence cannot be null");

        int last = charSequence.length() - 1;
        int first = 0;
        while (first < last) {
            if (charSequence.charAt(first) != charSequence.charAt(last)) {
                return false;
            }
            last--;
            first++;
        }
        return true;
    }
}
