package net.piotrturski.test.sql;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class Q1Test extends DbTest {

    @Test
    public void should_sort_by_votes_and_rank() throws Exception {

        runScripts(
        "drop table if exists votes;",
        "CREATE TABLE votes ( name CHAR(10), votes INT ); "
        );

        assertThat(jdbcTemplate.queryForList(load("q1.sql"))).isEmpty();

        runScripts("INSERT INTO votes VALUES ('Smith',10), ('Jones',15), ('White',20), " +
                "('Black',40), ('Green',50), ('Brown',20);");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(load("q1.sql"));

        assertThat(result.stream().map(m -> m.get("rank"))).containsExactly(1L, 2L, 3L, 4L, 5L, 6L);
        assertThat(result.stream().map(m -> m.get("votes"))).containsExactly(50, 40, 20, 20, 15, 10);
    }
}
