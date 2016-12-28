package net.piotrturski.test.sql;

import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ZohhakRunner.class)
public class Q2Test extends DbTest {


    @TestWith({
            "a, A",
            "aBc deF, Abc Def",
            " '  aB\tcD\neF ',  '  Ab\tCd\nEf ' ",
            "null, null"
    })
    public void should_capitalize_first_letters(String input, String output) throws Exception {

        runScripts(
                "drop function if exists initcap",
                        load("q2.sql")
        );

        List<String> result = jdbcTemplate.queryForList("select initcap(?)", new String[]{input}, String.class);

        assertThat(result).containsExactly(output);

    }
}
