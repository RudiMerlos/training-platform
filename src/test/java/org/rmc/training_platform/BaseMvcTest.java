package org.rmc.training_platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

@AutoConfigureMockMvc
public class BaseMvcTest {

    @Autowired
    protected MockMvc mvc;

    protected static void deleteAllTables(final Connection connection) throws SQLException {
        connection.createStatement().executeUpdate("SET REFERENTIAL_INTEGRITY FALSE");

        final ResultSet rs = connection.createStatement().executeQuery("""
                SELECT table_name, table_schema
                FROM information_schema.tables
                WHERE table_schema = 'PUBLIC'
                """);
        while (rs.next()) {
            final String tableName = rs.getString(1);
            final String tableSchema = rs.getString(2);
            connection.createStatement().executeUpdate("TRUNCATE TABLE " + tableSchema + "." + tableName
                    + " RESTART IDENTITY");
        }

        connection.createStatement().executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");
    }

    protected static String resourceAsString(final String file) throws IOException {
        final String result;

        try(final InputStream resource = new ClassPathResource(file).getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
            result = reader.lines().collect(Collectors.joining("\n"));
        }

        return result;
    }

}
