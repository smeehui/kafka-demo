package org.smee.services;

import lombok.extern.slf4j.Slf4j;
import org.smee.dto.Logs;
import org.smee.repo.LogRepository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class LogService {
    private final ReentrantLock lock = new ReentrantLock();
    private final ExecutorService logExecutor = Executors.newFixedThreadPool(2);
    private final LogRepository repository;

    public LogService(LogRepository repository) {
        this.repository = repository;
    }

    public Future<Boolean> writeLogAsync(Logs record) {
        return logExecutor.submit(() -> {
            try {
                lock.lock();
                return repository.create(record);
            } finally {
                lock.unlock();
            }
        });
    }


    public void callInsertProcedure(Connection connection, String sql) throws SQLException {
        try (CallableStatement callableStatement = connection.prepareCall(sql)) {
            System.out.println("Calling PostgreSQL procedure...");
            callableStatement.execute();
            System.out.println("Procedure executed successfully.");
        }
    }
}
