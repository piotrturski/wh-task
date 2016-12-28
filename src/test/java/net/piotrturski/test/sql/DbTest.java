package net.piotrturski.test.sql;

import one.util.streamex.StreamEx;
import org.apache.commons.io.IOUtils;
import org.flywaydb.core.internal.dbsupport.SqlScript;
import org.flywaydb.core.internal.dbsupport.SqlStatement;
import org.flywaydb.core.internal.dbsupport.mysql.MySQLDbSupport;
import org.junit.Before;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * connects to local mysql using default port, root user and empty password, 'db' database:
 * docker run -p 3306:3306 --rm -e MYSQL_ALLOW_EMPTY_PASSWORD=true -e MYSQL_DATABASE=db  mysql:5.7.17
 *
 * it's a poor man's db testing because of lack of:
 * flyway and defined db schema, efficient db cleanup, autostarting docker, parameterized port etc.
 * but it's good enough for this task
 */
public abstract class DbTest {

    static final String jdbcUrl = "jdbc:mysql:///db";

    private SingleConnectionDataSource ds;
    protected JdbcTemplate jdbcTemplate;

    @Before
    public void connect() {
        if (ds != null) return;

        ds = new SingleConnectionDataSource(jdbcUrl, "root", "", true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ds.destroy();
        }));
        jdbcTemplate = new JdbcTemplate(ds);
    }

    protected void runScripts(String... scripts) {
        try (Connection connection = ds.getConnection()) {
            MySQLDbSupport dbSupport = new MySQLDbSupport(connection);

            StreamEx.of(scripts)
                    .flatCollection(script -> new SqlScript(script, dbSupport).getSqlStatements())
                    .map(SqlStatement::getSql)
                    .forEach(jdbcTemplate::execute);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected String load(String fileName) {
        try {
            return IOUtils.toString(new ClassPathResource(fileName).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
