package org.smee.repo;

import org.smee.dto.Logs;

public interface LogRepository {
    boolean create(Logs log);
}
