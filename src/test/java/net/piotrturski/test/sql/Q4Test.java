package net.piotrturski.test.sql;

import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ZohhakRunner.class)
public class Q4Test extends DbTest {

    @TestWith({
            "2016-01-02, 2016-01-04, 0",
            "2016-01-03, 2016-01-04, 1",
            "2016-01-04, 2016-01-04, 2",
            "2016-01-05, 2016-01-05, 1",
            "2016-01-05, 2016-01-20, 1",
    })
    public void should_count_open_bugs(String startDate, String endDate, int openBugs) throws Exception {

        runScripts("drop table if exists bugs;",
                "create table bugs (id int, open_date date, close_date date, severity int)",

                "insert into bugs values (1, '2016-01-04', null, 3), (2, '2016-01-03', '2016-01-05', 1)",

                String.format("set @start_date = '%s';\n " +
                              "set @end_date = '%s';",
                                startDate, endDate)
                );


        List<Integer> integers = jdbcTemplate.queryForList(load("q4.sql"), int.class);

        assertThat(integers).containsExactly(openBugs);
    }
}
