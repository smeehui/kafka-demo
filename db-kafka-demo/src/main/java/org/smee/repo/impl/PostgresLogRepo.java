package org.smee.repo.impl;

import lombok.extern.slf4j.Slf4j;
import org.smee.config.Database;
import org.smee.dto.Logs;
import org.smee.repo.LogRepository;

import java.sql.SQLException;

@Slf4j
public class PostgresLogRepo implements LogRepository {

    private final Database dataSource;

    public PostgresLogRepo() {
        dataSource = new Database();
    }

    @Override
    public boolean create(Logs record) {
        // 3. Each task gets its own connection from the pool.
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {

            connection.setAutoCommit(false);
            statement.executeUpdate(generateInsertStatement(record));
            connection.commit();

            log.info("Log wrote successful");
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            // In a production app, more granular handling is needed
            // such as checking the state and rolling back only when necessary.
            log.error("Log wrote failed", e);
            throw new RuntimeException(e);
        }
    }

    private String generateInsertStatement(Logs record) {
        return "INSERT INTO logs (log, time, log_type, before, after) VALUES ('%s', '%s', '%s', '%s', '%s')".formatted(
                record.getLog(),
                record.getTime(),
                record.getLogType().toString(),
                record.getBefore(),
                record.getAfter()
        );
    }
}
