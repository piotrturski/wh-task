package net.piotrturski.test.sql;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class Q3Test extends DbTest {

    @Test
    public void should_split_columns_into_rows() throws Exception {
        runScripts(
                "drop procedure if exists to_rows;",
                "drop table if exists sometbl;",
                "CREATE TABLE sometbl ( ID INT, NAME VARCHAR(50) );",

                "drop table if exists result_table;",
                "create table result_table ( ID INT, NAME VARCHAR(50) );",

                "INSERT INTO sometbl VALUES (1, 'Smith'), (2, 'Julio|Jones|Falcons'), " +
                "(4, 'Paint|It|Red'), (6, 'Brown|bag|'), (7, ''), (8, null);",

                load("q3.sql"),

                "call to_rows()"
        );

        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from result_table");

        assertThat(maps.stream().map(m -> m.get("id")))
                .containsExactly(1,
                                2,2,2,
                                4,4,4,
                                6,6,6,
                                7,
                                8);

        assertThat(maps.stream().map(m -> m.get("name")))
                .containsExactly("Smith",
                                "Julio","Jones","Falcons",
                                "Paint", "It", "Red",
                                "Brown", "bag", "",
                                "",
                                null);

    }
}
