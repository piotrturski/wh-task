package net.piotrturski;

import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import net.piotrturski.test.Palindrome;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ZohhakRunner.class)
public class PalindromeTest {


    @TestWith({
            "",
            "a",
            "aa",
            "acbca"
    })
    public void should_detect_palindrome(String palindrome) {

        assertThat(Palindrome.isPalindrome(palindrome)).isTrue();
    }


    @TestWith({
            "ab",
            "aab"
    })
    public void should_detect_non_palindrome(String nonPalindrome) {

        assertThat(Palindrome.isPalindrome(nonPalindrome)).isFalse();
    }

}
